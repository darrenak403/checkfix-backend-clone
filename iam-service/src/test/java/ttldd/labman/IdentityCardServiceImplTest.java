package ttldd.labman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ttldd.labman.service.imp.IdentityCardServiceImpl;
import ttldd.labman.utils.JwtHelper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// ============== IdentityCardServiceImpl Tests ==============
@ExtendWith(MockitoExtension.class)
class IdentityCardServiceImplTest {

    @Mock
    private ttldd.labman.repo.IdentityCardRepo identityCardRepo;

    @Mock
    private JwtHelper jwtHelper;

    @Mock
    private ttldd.labman.repo.UserRepo userRepo;

    @InjectMocks
    private IdentityCardServiceImpl identityCardService;

    private ttldd.labman.dto.request.UserCardRequest userCardRequest;
    private ttldd.labman.entity.User user;
    private ttldd.labman.entity.IdentityCard identityCard;

    @BeforeEach
    void setUp() {
        user = new ttldd.labman.entity.User();
        user.setId(1L);
        user.setEmail("test@example.com");

        userCardRequest = new ttldd.labman.dto.request.UserCardRequest();
        userCardRequest.setIdentifyNumber("123456789012");
        userCardRequest.setFullName("Test User");
        userCardRequest.setBirthDate("01/01/1990");
        userCardRequest.setNationality("Vietnam");

        identityCard = new ttldd.labman.entity.IdentityCard();
        identityCard.setId(1L);
        identityCard.setIdentityNumber("123456789012");
        identityCard.setFullName("Test User");
        identityCard.setUser(user);
    }

    @Test
    void saveIdentityCard_NewCard_SavesSuccessfully() {
        // Arrange
        when(jwtHelper.getCurrentUserId()).thenReturn(1L);
        when(userRepo.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(identityCardRepo.findByIdentityNumber(anyString())).thenReturn(java.util.Optional.empty());
        when(identityCardRepo.save(any())).thenReturn(identityCard);

        // Act
        var response = identityCardService.saveIdentityCard(userCardRequest);

        // Assert
        assertNotNull(response);
        assertEquals("123456789012", response.getIdentifyNumber());
        verify(identityCardRepo, times(1)).save(any());
    }

    @Test
    void saveIdentityCard_ExistingCardDifferentUser_ThrowsException() {
        // Arrange
        ttldd.labman.entity.User anotherUser = new ttldd.labman.entity.User();
        anotherUser.setId(2L);
        identityCard.setUser(anotherUser);

        when(jwtHelper.getCurrentUserId()).thenReturn(1L);
        when(userRepo.findById(1L)).thenReturn(java.util.Optional.of(user));
        when(identityCardRepo.findByIdentityNumber(anyString()))
                .thenReturn(java.util.Optional.of(identityCard));

        // Act & Assert
        assertThrows(ttldd.labman.exception.GetException.class,
                () -> identityCardService.saveIdentityCard(userCardRequest));
    }

    @Test
    void getIdentityCardByUserId_CardExists_ReturnsCard() {
        // Arrange
        when(jwtHelper.getCurrentUserId()).thenReturn(1L);
        when(identityCardRepo.findByUserId(1L)).thenReturn(identityCard);

        // Act
        var response = identityCardService.getIdentityCardByUserId();

        // Assert
        assertNotNull(response);
        assertEquals("123456789012", response.getIdentifyNumber());
    }

    @Test
    void getIdentityCardByUserId_CardNotFound_ThrowsException() {
        // Arrange
        when(jwtHelper.getCurrentUserId()).thenReturn(1L);
        when(identityCardRepo.findByUserId(1L)).thenReturn(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> identityCardService.getIdentityCardByUserId());
    }
}
