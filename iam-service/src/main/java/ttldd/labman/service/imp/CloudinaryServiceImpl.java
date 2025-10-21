package ttldd.labman.service.imp;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ttldd.labman.service.CloudinaryService;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file) throws IOException {
        var result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "/upload",
                "use_filename", true,
                "unique_filename", true,
                "resource_type", "auto"
        ));

        return result.get("secure_url").toString();
    }
}
