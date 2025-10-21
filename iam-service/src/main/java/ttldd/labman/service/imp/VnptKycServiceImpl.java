package ttldd.labman.service.imp;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ttldd.labman.dto.CardImgDTO;
import ttldd.labman.dto.PostCodeDTO;
import ttldd.labman.dto.request.UserCardRequest;
import ttldd.labman.dto.response.UserCardResponse;
import ttldd.labman.dto.VnptOcrDTO;
import ttldd.labman.dto.request.VnptClassifyRequest;
import ttldd.labman.dto.request.VnptOcrFullRequest;
import ttldd.labman.dto.response.UserResponse;
import ttldd.labman.dto.response.VnptClassifyResponse;
import ttldd.labman.dto.response.VnptOcrFullResponse;
import ttldd.labman.dto.response.VnptUploadResponse;
import ttldd.labman.entity.Card;
import ttldd.labman.entity.IdentityCard;
import ttldd.labman.entity.User;
import ttldd.labman.exception.GetException;
import ttldd.labman.repo.IdentityCardRepo;
import ttldd.labman.repo.UserRepo;
import ttldd.labman.service.CloudinaryService;
import ttldd.labman.service.VnptKycService;
import ttldd.labman.utils.DateUtils;
import ttldd.labman.utils.JwtHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class VnptKycServiceImpl implements VnptKycService {

    WebClient vnptWebClient;

    DateUtils dateUtils;

    UserRepo userRepo;

    JwtHelper jwtHelper;

    CloudinaryService cloudinaryService;


    @Override
    public UserCardResponse extractIdCardInfo(MultipartFile frontImage,
                                              MultipartFile backImage) {
        User user = userRepo.findById(jwtHelper.getCurrentUserId())
                .orElseThrow(() -> new GetException("User not found with id: " + jwtHelper.getCurrentUserId()));
        String initialClientSession = user.getEmail() + "_" + System.currentTimeMillis();
        log.info("Đang upload mặt trước...");
        String frontHash = uploadFile(frontImage);
        log.info("Đang upload mặt sau...");
        String backHash = uploadFile(backImage);

        String safeClientSession = initialClientSession.replaceAll("[^a-zA-Z0-9]", "");
        String transactionToken = UUID.randomUUID().toString().replace("-", "");
        Integer idType = callClassifyApi(frontHash, safeClientSession, transactionToken);
        log.info("API Phân loại trả về type: {}", idType);
        validateDocumentType(idType);

        // Request body
        VnptOcrFullRequest requestBody = new VnptOcrFullRequest();
        requestBody.setImgFront(frontHash);
        requestBody.setImgBack(backHash);
        requestBody.setClientSession(safeClientSession);
        requestBody.setType(idType);
        requestBody.setCropParam("");
        requestBody.setValidatePostcode(true);
        requestBody.setToken(transactionToken);

        // Call api 6
        VnptOcrFullResponse response = callFullOcrApi(requestBody);

        // Map data
        if (response == null || response.getObject() == null) {
            log.error("API VNPT trả về rỗng hoặc không có 'object'");
            throw new IllegalArgumentException("Không bóc tách được dữ liệu (object is null)");
        }
        VnptOcrDTO data = response.getObject();
        UserCardResponse userCardResponse = mapDataToUser(data);
        userCardResponse.setCardImages(generateCardImageDTOs(frontImage, backImage));
        log.info("Bóc tách thành công User: {}", user.getFullName());
        return userCardResponse;
    }


    private List<CardImgDTO> generateCardImageDTOs(MultipartFile frontImage,
                                                   MultipartFile backImage) {
        try{
            String frontImageUrl = cloudinaryService.uploadFile(frontImage);
            String backImageUrl = cloudinaryService.uploadFile(backImage);
            return Stream.of(
                            new String[]{"front", "Ảnh mặt trước CCCD", frontImageUrl},
                            new String[]{"back", "Ảnh mặt sau CCCD", backImageUrl}
                    )
                    .map(arr -> CardImgDTO.builder()
                            .type(arr[0])
                            .description(arr[1])
                            .imageUrl(arr[2])
                            .build())
                    .toList();
        }catch(IOException e){
            log.error("Lỗi khi upload ảnh lên Cloudinary: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Lỗi khi upload ảnh lên Cloudinary: " + e.getMessage(), e);
        }
    }

    private void validateDocumentType(Integer idType) {
        List<Integer> allowedTypes = Arrays.asList(-1, 0, 2);
        if (allowedTypes.contains(idType)) {
            return;
        }

        log.warn("Loại giấy tờ không được hỗ trợ. Type: {}", idType);

        String errorMessage = switch (idType) {
            case 1, 3 -> "Loại giấy tờ không khớp với mặt trước (CCCD).";
            case 4 -> "Loại giấy tờ không được hỗ trợ (Giấy tờ khác).";
            case 5 -> "Loại giấy tờ không được hỗ trợ (Hộ chiếu).";
            default -> "Không thể xác định loại giấy tờ hoặc giấy tờ không được hỗ trợ. Vui lòng thử lại.";
        };

        throw new IllegalArgumentException(errorMessage);
    }


    private Integer callClassifyApi(String imageHash, String clientSession, String token) {
        log.info("Đang gọi API Phân loại giấy tờ (/ai/v1/classify/id)...");

        // Tạo body
        VnptClassifyRequest requestBody = VnptClassifyRequest.builder()
                .imgCard(imageHash)
                .clientSession(clientSession)
                .token(token)
                .build();

        try {
            VnptClassifyResponse response = vnptWebClient.post()
                    .uri("/ai/v1/classify/id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("mac-address", "TEST1")
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .bodyToMono(VnptClassifyResponse.class)
                    .block();

            // Lấy type từ object con
            if (response != null && response.getObject() != null && response.getObject().getType() != null) {
                return response.getObject().getType();
            } else {
                log.warn("Phân loại giấy tờ trả về 200 OK nhưng không có 'object' hoặc 'type'. Dùng type -1.");
                return -2;
            }

        } catch (WebClientResponseException e) {
            log.error("LỖI API VNPT (Classify): Status code {}, Response body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new IllegalArgumentException("Lỗi khi gọi API Phân loại: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("LỖI HỆ THỐNG (Classify): {}", e.getMessage(), e);
            throw new IllegalArgumentException("Lỗi hệ thống khi gọi API Phân loại: " + e.getMessage(), e);
        }
    }


    private VnptOcrFullResponse callFullOcrApi(VnptOcrFullRequest requestBody) {
        log.info("Đang gọi API OCR gộp (/ai/v1/ocr/id)...");
        try {
            return vnptWebClient.post()
                    .uri("/ai/v1/ocr/id")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("mac-address", "TEST1")
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .bodyToMono(VnptOcrFullResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("LỖI API VNPT (Full OCR): Status code {}, Response body: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new IllegalArgumentException("Lỗi khi gọi API Bóc tách gộp: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("LỖI HỆ THỐNG (Full OCR): {}", e.getMessage(), e);
            throw new IllegalArgumentException("Lỗi hệ thống khi gọi API Bóc tách gộp: " + e.getMessage(), e);
        }
    }


    private String uploadFile(MultipartFile file) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file.getResource());
        body.add("title", "CCCD Upload");
        body.add("description", "Upload file eKYC cho session: user_123");

        try {
            VnptUploadResponse response = vnptWebClient.post()
                    .uri("/file-service/v1/addFile")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(VnptUploadResponse.class)
                    .block();

            if (response != null && response.getObject() != null && response.getObject().getHash() != null) {
                return response.getObject().getHash();
            } else {
                log.info("Upload file thất bại hoặc response không có hash.");
                throw new IllegalArgumentException("Upload file thất bại hoặc response không có hash.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Lỗi khi gọi API Upload: " + e.getMessage(), e);
        }
    }


    private UserCardResponse mapDataToUser(VnptOcrDTO data) {
        String address = formatAddressFromPostCode(data.getNewPostCode());
        return UserCardResponse.builder()
                .identifyNumber(data.getId())
                .fullName(data.getName())
                .birthDate(data.getBirthDay())
                .nationality(data.getNationality())
                .recentLocation(address != null ? address : data.getRecentLocation().replace("\n", ", "))
                .validDate(data.getValidDate())
                .issueDate(data.getIssueDate())
                .gender(data.getGender())
                .features(data.getFeatures())
                .issuePlace(data.getIssuePlace())
                .build();
    }

    private String formatAddressFromPostCode(List<PostCodeDTO> postCodes) {
        if (postCodes == null || postCodes.isEmpty()) return null;
        PostCodeDTO addressData = postCodes.stream()
                .filter(p -> "address".equalsIgnoreCase(p.getType()))
                .findFirst()
                .orElse(null);

        if (addressData == null) return null;
        try {
            String detail = addressData.getDetail();
            String ward = addressData.getWard().get(1).toString().trim();
            String district = addressData.getDistrict().get(1).toString().trim();
            String city = addressData.getCity().get(1).toString().trim();
            return String.join(", ", detail, ward, district, city);
        } catch (Exception e) {
            log.warn("Lỗi khi format địa chỉ từ new_post_code: {}", e.getMessage());
            return null;
        }
    }

    private VnptOcrDTO convertIdentityCardToResponse(IdentityCard identityCard) {
        return VnptOcrDTO.builder()
                .id(identityCard.getIdentityNumber())
                .name(identityCard.getFullName())
                .birthDay(identityCard.getDateOfBirth())
                .features(identityCard.getFeatures())
                .issuePlace(identityCard.getIssuePlace())
                .nationality(identityCard.getNationality())
                .gender(identityCard.getGender())
                .recentLocation(identityCard.getRecentLocation())
                .nationality(identityCard.getNationality())
                .issueDate(identityCard.getIssueDate())
                .validDate(identityCard.getValidDate())
                .build();
    }


}
