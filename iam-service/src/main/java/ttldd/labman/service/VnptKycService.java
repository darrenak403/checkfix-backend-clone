package ttldd.labman.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ttldd.labman.dto.request.UserCardRequest;
import ttldd.labman.dto.response.UserCardResponse;
import ttldd.labman.dto.response.UserResponse;

@Service
public interface VnptKycService {
    UserCardResponse extractIdCardInfo(MultipartFile frontImage,
                                       MultipartFile backImage);
    UserResponse saveUserCard(UserCardRequest userCardDTO);
}
