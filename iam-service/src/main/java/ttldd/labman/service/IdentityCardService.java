package ttldd.labman.service;

import org.springframework.stereotype.Service;
import ttldd.labman.dto.request.UserCardRequest;
import ttldd.labman.dto.response.UserCardResponse;

@Service
public interface IdentityCardService {
    UserCardResponse saveIdentityCard(UserCardRequest userCardDTO);
    UserCardResponse getIdentityCardByUserId();
}
