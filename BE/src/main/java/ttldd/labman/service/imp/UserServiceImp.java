package ttldd.labman.service.imp;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ttldd.labman.dto.UserDTO;
import ttldd.labman.entity.Role;
import ttldd.labman.entity.User;
import ttldd.labman.exception.InsertException;
import ttldd.labman.repo.RoleRepo;
import ttldd.labman.repo.UserRepo;
import ttldd.labman.service.UserService;
import javax.crypto.SecretKey;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Optional;


@Service
public class UserServiceImp implements UserService {

    // Oauth2 Google
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;
    @Value("${spring.security.oauth2.client.registration.google.auth-uri}")
    private String googleAuthUri;
    @Value("${spring.security.oauth2.client.registration.google.token-uri}")
    private String googleTokenUri;
    @Value("${spring.security.oauth2.client.registration.google.user-info-uri}")
    private String googleUserInfoUri;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secret;


    @Override
    public void registerUser(UserDTO userDTO, String role) {
        // Kiểm tra tên đăng nhập đã tồn tại chưa
        if (userRepo.existsByEmail(userDTO.getEmail())) {
            throw new InsertException("Username already exists");
        }

        try {
            // Mã hóa mật khẩu
            String encodedPassword = passwordEncoder.encode(userDTO.getPassword());

            // Tìm role

            Role roles = roleRepository.findByRoleCode(role)
                    .orElseThrow(() -> new InsertException("Role not found: " + role));

            // Tạo mới user
            User user = new User();
            user.setEmail(userDTO.getEmail());
            user.setPassword(encodedPassword);
            user.setRole(roles);
            user.setLoginProvider("local");


            // Lưu vào database
            userRepo.save(user);

        } catch (InsertException e) {
            throw e;
        } catch (Exception e) {
            throw new InsertException("Error while inserting user: " + e.getMessage());
        }
    }

    @Override
    public String loginUser(UserDTO userDTO) {
        // Tìm user theo email
        User userEntity = userRepo.findByEmail(userDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        // Kiểm tra password
        if (!passwordEncoder.matches(userDTO.getPassword(), userEntity.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác");
        }


        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR, 1); // hết hạn sau 1 giờ
        Date expiration = calendar.getTime();

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        return Jwts.builder()
                .claim("userId", userEntity.getId())
                .claim("googleID", userEntity.getGoogleId())
                .claim("email",userEntity.getEmail())
                .claim("name", userEntity.getFullName())
                .claim("role", userEntity.getRole().getRoleName())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key)
                .compact();
    }

    @Override
    public String generateAuthorizationUri(String loginType) {
        String url = "";
        loginType = loginType.toLowerCase();
        switch (loginType) {
            case "google":
                url = googleAuthUri + "?client_id=" + googleClientId + "&redirect_uri=" + googleRedirectUri + "&scope=email%20profile" + "&response_type=code" + "&loginType=" + loginType;
                break;
            default:
        }
        return url;
    }

    @Override
    public Map<String, Object> authenticateAndFetchProfile(String code, String loginType) {
        RestTemplate restTemplate = new RestTemplate();
        loginType = loginType.toLowerCase();
        String accessToken = "";
        String url = "";
        String userInfoUri = "";
        Map<String, Object>  userInfo = null;
        switch (loginType) {
            case "google":
                // Call Google API to get user profile
                Map<String, String> request = Map.of(
                        "client_id", googleClientId, "redirect_uri", googleRedirectUri,
                        "client_secret", googleClientSecret, "code", code,
                        "grant_type", "authorization_code"
                );
                ResponseEntity res = restTemplate.postForEntity(googleTokenUri, request, Map.class);

                String body = res.getBody().toString();
                accessToken = body.split(",")[0].replace("{access_token=", "");
                userInfoUri = googleUserInfoUri + "?access_token=" + accessToken;
                break;
            default:
        }

        userInfo = restTemplate.getForObject(userInfoUri, Map.class);

        return userInfo;
    }

    @Override
    public String loginOrSignup(Map<String, Object> userInfo, String role) {
        UserDTO userDTO = new UserDTO();
        //Lấy email của người dùng
        userDTO.setEmail(userInfo.get("email").toString());
        //Lấy tên của người dùng
        userDTO.setFullName(userInfo.get("name").toString());
        //Lấy google_id của người dùng
        userDTO.setSub(userInfo.get("sub").toString());
        String token = "";



        // Kiểm tra googleId có tồn tại ở database chưa
        Optional<User> existingUserEmail =  userRepo.findByGoogleId(userDTO.getSub());

        // Kiểm tra tên đăng nhập đã tồn tại chưa
        if(existingUserEmail.isPresent()){
            //Login
            User userEntity = existingUserEmail.get();

            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.HOUR, 1); // Set expiration time to 1 hour from now
            Date expiration = calendar.getTime();
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
            token = Jwts.builder()

                    .claim("userId", userEntity.getId())
                    .claim("googleID", userEntity.getGoogleId())
                    .claim("email",userEntity.getEmail())
                    .claim("name", userEntity.getFullName())
                    .claim("role", userEntity.getRole().getRoleName())
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .signWith(key)
                    .compact();
        }else{
            //Sign Up
            Role roleEntity = roleRepository.findByRoleCode(role).
                    orElseThrow(() -> new RuntimeException("Role not found: " + role));


            User userEntity = new User();


            userEntity.setFullName(userDTO.getFullName());
            userEntity.setEmail(userDTO.getEmail());
            userEntity.setGoogleId(userDTO.getSub());
            userEntity.setRole(roleEntity);
            userEntity.setLoginProvider("google");
            userRepo.save(userEntity);

            //Tạo Jwt
            Date now = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.HOUR, 1); // Set expiration time to 1 hour from now
            Date expiration = calendar.getTime();
            SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
            token = Jwts.builder()
                    .claim("userId", userEntity.getId())
                    .claim("googleID", userEntity.getGoogleId())
                    .claim("email",userEntity.getEmail())
                    .claim("name", userEntity.getFullName())
                    .claim("role", userEntity.getRole().getRoleName())
                    .setIssuedAt(now)
                    .setExpiration(expiration)
                    .signWith(key)
                    .compact();
        }

        return token;
    }
}


