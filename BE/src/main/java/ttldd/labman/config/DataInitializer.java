package ttldd.labman.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ttldd.labman.entity.Role;
import ttldd.labman.service.RoleService;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleService roleService;

    @Override
    public void run(String... args) throws Exception {
        Role role1 = Role.builder()
                .roleName("Administrator")
                .roleCode("ROLE_ADMIN")
                .description("System administrator with full access to all system features.")
                .privileges("FULL_ACCESS")
                .build();

        Role role2 = Role.builder()
                .roleName("Laboratory Manager")
                .roleCode("ROLE_MANAGER")
                .description("Responsible for managing the lab, lab users, service users, and monitoring the overall system.")
                .privileges("MANAGE_LAB")
                .build();

        Role role3 = Role.builder()
                .roleName("Service")
                .roleCode("ROLE_SERVICE")
                .description("Authorized personnel for system operation and maintenance, ensuring optimal performance and reliability.")
                .privileges("SYSTEM_MAINTENANCE")
                .build();

        Role role4 = Role.builder()
                .roleName("Lab User")
                .roleCode("ROLE_LAB_USER")
                .description("Laboratory staff responsible for conducting tests, analyzing samples, and managing lab processes.")
                .privileges("LAB_OPERATIONS")
                .build();

        Role role5 = Role.builder()
                .roleName("Patient")
                .roleCode("ROLE_PATIENT")
                .description("Patient user with permission to view their own test results only.")
                .privileges("READ_ONLY")
                .build();
        roleService.createRole(role1);
        roleService.createRole(role2);
        roleService.createRole(role3);
        roleService.createRole(role4);
        roleService.createRole(role5);
    }
}
