package ttldd.labman.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.io.Decoders;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtDecoderCustomizer implements JwtDecoder {
    @Value("${jwt.secret}")
    private String secretKey;

    private NimbusJwtDecoder nimbusJwtDecoder = null;

    public boolean verifyToken(String token) throws ParseException, JOSEException {
        if(StringUtils.isBlank(token)) {
            return false;
        }
        SignedJWT signedJWT = SignedJWT.parse(token);

        if(signedJWT.getJWTClaimsSet().getExpirationTime().before(new Date())) {
            return false;
        }

        return signedJWT.verify(new MACVerifier(Decoders.BASE64.decode(secretKey)));
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            if(!verifyToken(token)) {
                throw new JwtException("Invalid token");
            }

            if(Objects.isNull(nimbusJwtDecoder)) {
                SecretKeySpec secretKeySpec = new SecretKeySpec(
                        Decoders.BASE64.decode(secretKey),
                        "HS256");

                nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec)
                        .macAlgorithm(MacAlgorithm.HS256)
                        .build();
            }
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }
        return nimbusJwtDecoder.decode(token);
    }
}
