package ttldd.labman.service.imp;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ttldd.labman.dto.CardImgDTO;
import ttldd.labman.dto.request.UserCardRequest;
import ttldd.labman.dto.response.UserCardResponse;
import ttldd.labman.entity.Card;
import ttldd.labman.entity.IdentityCard;
import ttldd.labman.entity.User;
import ttldd.labman.exception.GetException;
import ttldd.labman.repo.IdentityCardRepo;
import ttldd.labman.repo.UserRepo;
import ttldd.labman.service.IdentityCardService;
import ttldd.labman.utils.JwtHelper;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class IdentityCardServiceImpl implements IdentityCardService {

    IdentityCardRepo identityCardRepo;

    JwtHelper jwtHelper;

    UserRepo userRepo;

    @Override
    public UserCardResponse saveIdentityCard(UserCardRequest userCardDTO) {
        User user = userRepo.findById(jwtHelper.getCurrentUserId()).orElseThrow(() -> new GetException("User not found with id: " + jwtHelper.getCurrentUserId()));
        IdentityCard identityCard = identityCardRepo.findByIdentityNumber(userCardDTO.getIdentifyNumber())
                .orElseGet(IdentityCard::new);
        identityCard.setIdentityNumber(userCardDTO.getIdentifyNumber());
        identityCard.setFullName(userCardDTO.getFullName());
        identityCard.setDateOfBirth(userCardDTO.getBirthDate());
        identityCard.setNationality(userCardDTO.getNationality());
        identityCard.setRecentLocation(userCardDTO.getRecentLocation());
        identityCard.setValidDate(userCardDTO.getValidDate());
        identityCard.setIssueDate(userCardDTO.getIssueDate());
        identityCard.setFeatures(userCardDTO.getFeatures());
        identityCard.setIssuePlace(userCardDTO.getIssuePlace());
        identityCard.setUser(user);
        if (userCardDTO.getCardImages() != null) {
            for (CardImgDTO img : userCardDTO.getCardImages()) {
                Card card = Card.builder()
                        .cardUrl(img.getImageUrl())
                        .type(img.getType())
                        .description(img.getDescription())
                        .identityCard(identityCard)
                        .build();
                identityCard.getCards().add(card);
            }
        }
        identityCardRepo.save(identityCard);
        log.info("Cập nhật thông tin giấy tờ cho User: {}", identityCard.getFullName());
        return mapToResponse(identityCard);
    }

    @Override
    public UserCardResponse getIdentityCardByUserId() {
        IdentityCard identityCard = identityCardRepo.findByUserId(jwtHelper.getCurrentUserId());
        if (identityCard == null) {
            throw new IllegalArgumentException("Not found identity card for user id: " + jwtHelper.getCurrentUserId());
        }
        return mapToResponse(identityCard);
    }

    private UserCardResponse mapToResponse(IdentityCard identityCard) {
        return UserCardResponse.builder()
                .identifyNumber(identityCard.getIdentityNumber())
                .fullName(identityCard.getFullName())
                .birthDate(identityCard.getDateOfBirth())
                .nationality(identityCard.getNationality())
                .recentLocation(identityCard.getRecentLocation())
                .validDate(identityCard.getValidDate())
                .issueDate(identityCard.getIssueDate())
                .features(identityCard.getFeatures())
                .issuePlace(identityCard.getIssuePlace())
                .cardImages(mapToCardImgDTOList(identityCard.getCards()))
                .build();
    }

    private List<CardImgDTO> mapToCardImgDTOList(List<Card> cards) {
        return cards.stream()
                .map(card -> CardImgDTO.builder()
                        .imageUrl(card.getCardUrl())
                        .type(card.getType())
                        .description(card.getDescription())
                        .build())
                .toList();
    }
}
