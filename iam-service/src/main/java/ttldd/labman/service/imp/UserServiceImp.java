package ttldd.labman.service.imp;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import ttldd.event.dto.PatientUpdateEvent;
import ttldd.event.dto.UserUpdatedEvent;
import ttldd.labman.dto.request.UpdateAvatarRequest;
import ttldd.labman.dto.request.UserCreationRequest;
import ttldd.labman.dto.request.UserUpdateRequest;
import ttldd.labman.dto.response.AuthResponse;
import ttldd.labman.dto.request.UserRequest;
import ttldd.labman.dto.response.UserResponse;
import ttldd.labman.entity.IdentityCard;
import ttldd.labman.entity.Permission;
import ttldd.labman.entity.Role;
import ttldd.labman.entity.User;
import ttldd.labman.exception.GetException;
import ttldd.labman.exception.InsertException;
import ttldd.labman.producer.NotificationProducer;
import ttldd.labman.repo.IdentityCardRepo;
import ttldd.labman.repo.RoleRepo;
import ttldd.labman.repo.UserRepo;
import ttldd.labman.service.UserService;
import ttldd.labman.utils.CryptoUtil;
import ttldd.labman.utils.JwtHelper;

import javax.crypto.SecretKey;
import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public  class UserServiceImp implements UserService {


    // Oauth2 Google
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;
    @Value("${google.auth-uri}")
    private String googleAuthUri;
    @Value("${google.token-uri}")
    private String googleTokenUri;
    @Value("${google.user-info-uri}")
    private String googleUserInfoUri;


    @Value("${spring.security.oauth2.client.registration.facebook.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.facebook.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.facebook.redirect-uri}")
    private String redirectUri;
    @Value("${facebook.auth-uri}")
    private String authUri;
    @Value("${facebook.token-uri}")
    private String tokenUri;
    @Value("${spring.security.oauth2.client.registration.facebook.scope:email,public_profile}")
    private String facebookScope;
    @Value("${facebook.user-info-uri}")
    private String facebookUserInfoUri;
    @Value("${facebook.response-type}")
    private String responseType;

    private final UserRepo userRepo;

    private final RoleRepo roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secret;

    private final JwtHelper jwtHelper;

    private final CryptoUtil cryptoUtils;

    private final IdentityCardRepo identityCardRepo;




    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final NotificationProducer notificationProducer;

    @Override
    @Transactional
    public void registerUser(UserRequest userDTO, String role) {


        // Kiểm tra tên đăng nhập đã tồn tại chưa
        if (userRepo.existsByEmail(userDTO.getEmail())) {
            throw new InsertException("Email already exists");
        }

        try {
            String rootPass = cryptoUtils.decrypt(userDTO.getPassword());
            log.info("decrypted password: {}", userDTO.getPassword());
            // Mã hóa mật khẩu
            String encodedPassword = passwordEncoder.encode(rootPass);
            log.info("encrypted password: " + encodedPassword);

            // Tìm role

            Role roles = roleRepository.findByRoleCode(role)
                    .orElseThrow(() -> new InsertException("Role not found: " + role));


            // Tạo mới user
            User user = new User();
            user.setEmail(userDTO.getEmail());
            user.setPassword(encodedPassword);
            user.setFullName(userDTO.getFullName());
            user.setRole(roles);
            user.setLoginProvider("local");


            // Lưu vào database
            userRepo.save(user);
            //send mail notification
            String safeFullName = (user.getFullName() == null || user.getFullName().trim().isEmpty())
                    ? "Bạn"
                    : user.getFullName();
            notificationProducer.sendEmail(
                    "send-email",
                    user.getEmail(),
                    "Chào mừng đến với Laboratory Management System",
                    "WELCOME_EMAIL",
                    Map.of("userName", safeFullName)
            );


        } catch (InsertException e) {
            throw e;
        }catch (KafkaProducerException ke){
            log.error("Failed to send notification email for user: {}", userDTO.getEmail(), ke);
        }
        catch (Exception e) {
            throw new InsertException("Error while inserting user: " + e.getMessage());
        }
    }

    @Override
    public AuthResponse loginUser(UserRequest userDTO) {
        String accessToken = "";
        String refreshToken = "";

        // Tìm user theo email
        User userEntity = userRepo.findByEmail(userDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        // Kiểm tra password
        String rootPass = cryptoUtils.decrypt(userDTO.getPassword());
        if (!passwordEncoder.matches(rootPass, userEntity.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác");
        }


        //AccessToken
        accessToken = generateAccessToken(userEntity);

        //RefreshToken
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_WEEK, 7); // hết hạn sau 1 giờ
        Date refreshExpiration = calendar.getTime();
        refreshToken = Jwts.builder()
                .claim("userId", userEntity.getId())
                .setIssuedAt(now)
                .setExpiration(refreshExpiration)
                .signWith(key)
                .compact();
        UserResponse us = new UserResponse();
        us.setId(userEntity.getId());
        us.setEmail(userEntity.getEmail());
        us.setFullName(userEntity.getFullName());
        us.setRole(userEntity.getRole().getRoleCode());
        us.setAvatarUrl(userEntity.getAvatarUrl());
        us.setDateOfBirth(userEntity.getDateOfBirth());
        us.setGender(userEntity.getGender());
        us.setAddress(userEntity.getAddress());
        us.setPhone(userEntity.getPhoneNumber());
        return new AuthResponse(accessToken, refreshToken, us);
    }

    @Override
    public String generateAuthorizationUri(String loginType) {
        String url = "";
        loginType = loginType.toLowerCase();
        switch (loginType) {
            case "facebook":
                url = authUri + "?client_id=" + clientId + "&redirect_uri=" + redirectUri + "&scope=" + facebookScope + "&response_type=" + responseType + "&loginType=" + loginType;
                break;

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
        Map<String, Object> userInfo = null;
        switch (loginType) {
            case "facebook":
                System.out.println("Facebook OAuth code: " + code);
                System.out.println("Facebook OAuth redirect_uri: " + redirectUri);
                url = tokenUri
                        + "?client_id=" + clientId
                        + "&redirect_uri=" + redirectUri
                        + "&client_secret=" + clientSecret
                        + "&scope=" + facebookScope
                        + "&response_type=" + responseType
                        + "&code=" + code;


                Map<String, Object> response = restTemplate.getForObject(url, Map.class);
                System.out.println("Facebook token response: " + response); // Add logging

                if (response == null) {
                    throw new RuntimeException("Không lấy được phản hồi từ Facebook khi lấy access_token. URL: " + url);
                }
                if (!response.containsKey("access_token")) {
                    if (response.containsKey("error")) {
                        throw new RuntimeException("Facebook token error: " + response.get("error") + ", URL: " + url);
                    }
                    throw new RuntimeException("Không lấy được access_token từ Facebook: " + response + ", URL: " + url);
                }

                accessToken = response.get("access_token").toString();
                userInfoUri = "https://graph.facebook.com/v18.0/me?access_token=" + accessToken + "&fields=id,name,email,picture.type(large)";

                // Lấy thông tin user từ Facebook
                userInfo = restTemplate.getForObject(userInfoUri, Map.class);

                // Xử lý trường hợp thiếu thông tin
                if (userInfo != null) {
                    // Đảm bảo có đủ các trường cần thiết
                    if (!userInfo.containsKey("email") || userInfo.get("email") == null) {
                        userInfo.put("email", userInfo.get("id") + "@facebook.com");
                    }
                    if (!userInfo.containsKey("sub") || userInfo.get("sub") == null) {
                        userInfo.put("sub", userInfo.get("id"));
                    }
                    if (!userInfo.containsKey("name") || userInfo.get("name") == null) {
                        userInfo.put("name", "Facebook User");
                    }
                } else {
                    throw new RuntimeException("Không lấy được thông tin người dùng từ Facebook. AccessToken: " + accessToken);
                }
                break;
            case "google":
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
    public AuthResponse loginOrSignup(Map<String, Object> userInfo, String role) {
        UserRequest userDTO = new UserRequest();
        userDTO.setEmail(userInfo.get("email") != null ? userInfo.get("email").toString() : (userInfo.get("id") + "@facebook.com"));
        userDTO.setFullName(userInfo.get("name") != null ? userInfo.get("name").toString() : "Facebook User");
        userDTO.setSub(userInfo.get("sub") != null ? userInfo.get("sub").toString() : userInfo.get("id").toString());

        String accessToken;
        String refreshToken;
        Optional<User> existingUser = userRepo.findByEmail(userDTO.getEmail());
        User userEntity;

        if (existingUser.isPresent()) {
            userEntity = existingUser.get();
            if(!userEntity.getLoginProvider().equalsIgnoreCase("google")){
                throw new RuntimeException("Email đã được đăng ký bằng phương thức khác");
            }
        } else {

            // Sign Up
            Role roleEntity = roleRepository.findByRoleCode(role)
                    .orElseThrow(() -> new RuntimeException("Role not found: " + role));
            userEntity = new User();
            userEntity.setFullName(userDTO.getFullName());
            userEntity.setEmail(userDTO.getEmail());
            userEntity.setGoogleId(userDTO.getSub());
            userEntity.setRole(roleEntity);
            userEntity.setLoginProvider("google");
            userEntity = userRepo.save(userEntity);
        }

        // AccessToken
        accessToken = generateAccessToken(userEntity);

        // RefreshToken
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_WEEK, 7);
        Date refreshExpiration = calendar.getTime();

        refreshToken = Jwts.builder()
                .claim("userId", userEntity.getId())
                .setIssuedAt(now)
                .setExpiration(refreshExpiration)
                .signWith(key)
                .compact();

        UserResponse us = new UserResponse();
        us.setId(userEntity.getId());
        us.setEmail(userEntity.getEmail());
        us.setFullName(userEntity.getFullName());
        us.setRole(userEntity.getRole().getRoleCode());
        us.setAddress(userEntity.getAddress());
        us.setGender(userEntity.getGender());
        us.setPhone(userEntity.getPhoneNumber());
        us.setDateOfBirth(userEntity.getDateOfBirth());
        us.setAvatarUrl(userEntity.getAvatarUrl());

        return new AuthResponse(accessToken, refreshToken, us);
    }



    @Override
    public AuthResponse refreshAccessToken(String refreshToken) {
        //1. Validate refresh token
        if(refreshToken == null || !jwtHelper.validateToken(refreshToken)){
            throw new RuntimeException("Refresh token không hợp lệ hoặc đã hết hạn");
        }
        //2. Generate new access token
        Long userId = jwtHelper.getUserId(refreshToken);

        //RefreshToken
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_WEEK, 7); // hết hạn sau 1 giờ
        Date refreshExpiration = calendar.getTime();
        refreshToken = Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(refreshExpiration)
                .signWith(key)
                .compact();


        User user = userRepo.findById(userId).orElseThrow(() -> new GetException("User not found"));
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setEmail(user.getEmail());
        userResponse.setFullName(user.getFullName());
        userResponse.setRole(user.getRole().getRoleCode());

        return new AuthResponse(generateAccessToken(user), refreshToken, userResponse);
    }

    @Override
    public List<UserResponse> getAllUser() {
        List<User> user = userRepo.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        for (User u : user) {
            userResponses.add(convertUserToUserResponse(u));
        }
        return userResponses;

    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepo.findById(id).orElseThrow(() -> new GetException("User not found with id: " + id));
        IdentityCard identityCard = identityCardRepo.findByUserId(user.getId());
        if(identityCard!= null){
            user.setIdentifyNumber(identityCard.getIdentityNumber());
        }
        return convertUserToUserResponse(user);
    }

    @Override
    public UserResponse createUser(UserCreationRequest userRequest) {
        if (userRepo.existsByEmail(userRequest.getEmail())) {
            throw new InsertException("Email already exists");
        }
        try {
            // Mã hóa mật khẩu
            String rootPass = cryptoUtils.decrypt(userRequest.getPassword());
            String encodedPassword = passwordEncoder.encode(rootPass);

            Role role = roleRepository.findById(userRequest.getRoleId())
                    .orElseThrow(() -> new InsertException("Role not found: " + userRequest.getRoleId()));

            // Tạo mới user
            User user = User.builder()
                    .email(userRequest.getEmail())
                    .password(encodedPassword)
                    .fullName(userRequest.getFullName())
                    .address(userRequest.getAddress())
                    .gender(userRequest.getGender())
                    .dateOfBirth(userRequest.getDateOfBirth())
                    .phoneNumber(userRequest.getPhone())
                    .role(role)
                    .loginProvider("local")
                    .build();

            // Lưu vào database
            userRepo.save(user);

            String safeFullName = (user.getFullName() == null || user.getFullName().trim().isEmpty())
                    ? "Bạn"
                    : user.getFullName();
            notificationProducer.sendEmail(
                    "send-email",
                    user.getEmail(),
                    "Chào mừng đến với Laboratory Management System",
                    "ACCOUNT_CREDENTIAL_EMAIL",
                    Map.of("userName", safeFullName, "password", rootPass, "hashPass", userRequest.getPassword(), "email", user.getEmail())
            );

            return convertUserToUserResponse(user);

        } catch (InsertException e) {
            throw e;
        } catch (Exception e) {
            throw new InsertException("Error while inserting user: " + e.getMessage());
        }
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest userRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findById(id).orElseThrow(() -> new GetException("User not found with id: " + id));
        if (StringUtils.hasText(userRequest.getFullName())) {
            user.setFullName(userRequest.getFullName());
        }
        if (StringUtils.hasText(userRequest.getEmail())) {
            if (!user.getEmail().equals(userRequest.getEmail()) && userRepo.existsByEmail(userRequest.getEmail())) {
                throw new InsertException("Email already exists");
            }
            user.setEmail(userRequest.getEmail());
        }
        if (StringUtils.hasText(userRequest.getPhone())){
            user.setPhoneNumber(userRequest.getPhone());
        }
        if (StringUtils.hasText(userRequest.getAddress())) {
            user.setAddress(userRequest.getAddress());
        }
        if (StringUtils.hasText(userRequest.getGender())) {
            user.setGender(userRequest.getGender());
        }
        if (userRequest.getDateOfBirth() != null) {
            user.setDateOfBirth(userRequest.getDateOfBirth());
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (userRequest.getRoleId() != null) {
            if (!isAdmin) {
                throw new IllegalArgumentException("Only admin can change roles");
            }
            Role role = roleRepository.findById(userRequest.getRoleId())
                    .orElseThrow(() -> new InsertException("Role not found: " + userRequest.getRoleId()));
            user.setRole(role);
        }
        userRepo.save(user);
        UserUpdatedEvent userUpdatedEvent = UserUpdatedEvent.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhoneNumber())
                .address(user.getAddress())
                .avatarUrl(user.getAvatarUrl())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .build();
        kafkaTemplate.send("user-updated-topic", userUpdatedEvent);
        log.info("Published UserUpdatedEvent for user ID: {}", userUpdatedEvent);
        return convertUserToUserResponse(user);
    }

    @Override
    public UserResponse updateAvatar(UpdateAvatarRequest updateAvatarRequest) {
        User user = userRepo.findById(jwtHelper.getCurrentUserId())
                .orElseThrow(() -> new GetException("User not found with id: " + jwtHelper.getCurrentUserId()));
        user.setAvatarUrl(updateAvatarRequest.getAvatarUrl());
        userRepo.save(user);
        return convertUserToUserResponse(user);
    }

    @Override
    public void syncUserFromPatient(PatientUpdateEvent event) {
        userRepo.findById(event.getUserId()).ifPresentOrElse(user -> {
            if (StringUtils.hasText(event.getFullName())) {
                user.setFullName(event.getFullName());
            }
            if (StringUtils.hasText(event.getGender())) {
                user.setGender(event.getGender());
            }
            if (event.getDateOfBirth() != null) {
                user.setDateOfBirth(event.getDateOfBirth());
            }
            if (StringUtils.hasText(event.getAddress())) {
                user.setAddress(event.getAddress());
            }
            if (StringUtils.hasText(event.getEmail())) {
                user.setEmail(event.getEmail());
            }
            if (StringUtils.hasText(event.getPhone())) {
                user.setPhoneNumber(event.getPhone());
            }
            if (StringUtils.hasText(event.getAvatarUrl())) {
                user.setAvatarUrl(event.getAvatarUrl());
            }
            userRepo.save(user);
            log.info("User info synced with patient update: {}", user.getId());
        }, () -> {
            log.warn("No user found with ID {} to sync from patient update", event.getId());
        });
    }

    public String generateAccessToken(User user) {
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.MINUTE, 60); // 1 giờ
        Date refreshExpiration = calendar.getTime();

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        return Jwts.builder()
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("name", user.getFullName())
                .claim("role", user.getRole().getRoleCode())
                .setIssuedAt(now)
                .setExpiration(refreshExpiration)
                .signWith(key)
                .compact();
    }

    private UserResponse convertUserToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .identifyNumber(user.getIdentifyNumber())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().getRoleCode())
                .address(user.getAddress())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .phone(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
