package ttldd.labman;


import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ttldd.labman.dto.request.IntrospectRequest;
import ttldd.labman.dto.response.IntrospectResponse;
import ttldd.labman.service.imp.*;
import ttldd.labman.utils.JwtHelper;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// ============== AuthServiceImpl Tests ==============
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private JwtHelper jwtHelper;

    @InjectMocks
    private AuthServiceImpl authService;

    private IntrospectRequest introspectRequest;

    @BeforeEach
    void setUp() {
        introspectRequest = new IntrospectRequest();
        introspectRequest.setToken("valid.jwt.token");
    }

    @Test
    void introspect_ValidToken_ReturnsValid() throws JOSEException, ParseException {

        IntrospectResponse response = authService.introspect(introspectRequest);
        assertTrue(response.isValid());
        verify(jwtHelper, times(1)).verifyToken("valid.jwt.token", false);
    }

    @Test
    void introspect_InvalidToken_ReturnsInvalid() throws JOSEException, ParseException {
        // Arrange
        doThrow(new JOSEException("Invalid token"))
                .when(jwtHelper).verifyToken(anyString(), anyBoolean());

        // Act
        IntrospectResponse response = authService.introspect(introspectRequest);

        // Assert
        assertFalse(response.isValid());
        verify(jwtHelper, times(1)).verifyToken("valid.jwt.token", false);
    }

    @Test
    void introspect_ParseException_ReturnsInvalid() throws JOSEException, ParseException {
        // Arrange
        doThrow(new ParseException("Parse error", 0))
                .when(jwtHelper).verifyToken(anyString(), anyBoolean());

        // Act
        IntrospectResponse response = authService.introspect(introspectRequest);

        // Assert
        assertFalse(response.isValid());
    }
}

