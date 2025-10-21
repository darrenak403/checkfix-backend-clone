package ttldd.labman.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ttldd.labman.dto.response.UserCardResponse;


@Service
public interface VnptKycService {
    UserCardResponse extractIdCardInfo(MultipartFile frontImage,
                                       MultipartFile backImage);
}
