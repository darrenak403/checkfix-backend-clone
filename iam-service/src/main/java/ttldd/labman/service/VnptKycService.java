package ttldd.labman.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ttldd.labman.dto.response.UserResponse;

@Service
public interface VnptKycService {
    UserResponse extractIdCardInfo(MultipartFile frontImage,
                                   MultipartFile backImage);
}
