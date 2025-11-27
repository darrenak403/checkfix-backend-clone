package ttldd.labman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ttldd.labman.dto.request.RoleRequest;
import ttldd.labman.entity.Role;
import ttldd.labman.repo.RoleRepo;
import ttldd.labman.service.imp.RoleServiceImp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// ============== RoleServiceImp Tests ==============
@ExtendWith(MockitoExtension.class)
class RoleServiceImpTest {

    @Mock
    private RoleRepo roleRepo;

    @InjectMocks
    private RoleServiceImp roleService;

    private Role role;

    private RoleRequest roleRequest;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setRoleCode("ADMIN");
        role.setRoleName("Administrator");
    }

    @Test
    void createRole_NewRole_SavesSuccessfully() {
        // Arrange
        when(roleRepo.findByRoleCode(anyString())).thenReturn(java.util.Optional.empty());
        when(roleRepo.save(any())).thenReturn(role);

        // Act
        roleService.createRole(roleRequest);

        // Assert
        verify(roleRepo, times(1)).save(role);
    }

    @Test
    void createRole_ExistingRole_DoesNotSave() {
        // Arrange
        when(roleRepo.findByRoleCode(anyString())).thenReturn(java.util.Optional.of(role));

        // Act
        roleService.createRole(roleRequest);

        // Assert
        verify(roleRepo, never()).save(any());
    }

    @Test
    void getRoles_ReturnsAllRoles() {
        // Arrange
        java.util.List<ttldd.labman.entity.Role> roles = java.util.Arrays.asList(role);
        when(roleRepo.findAll()).thenReturn(roles);

        // Act
        var response = roleService.getRoles();

        // Assert
        assertEquals(1, response.size());
        assertEquals("ADMIN", response.get(0).getRoleCode());
        assertEquals("Administrator", response.get(0).getRoleName());
    }
}
