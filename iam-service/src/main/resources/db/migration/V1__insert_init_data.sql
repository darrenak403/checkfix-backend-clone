-- Role
DELETE FROM `user`;
ALTER TABLE `user` AUTO_INCREMENT = 1;

DELETE FROM role;
ALTER TABLE role AUTO_INCREMENT = 1;

INSERT INTO role (role_name, role_code, description, privileges)
VALUES ('Administrator', 'ROLE_ADMIN', 'System administrator with full access to all system features.', 'FULL_ACCESS');

INSERT INTO role (role_name, role_code, description, privileges)
VALUES ('Laboratory Manager', 'ROLE_MANAGER', 'Responsible for managing the lab, lab users, service users, and monitoring the overall system.', 'MANAGE_LAB');

INSERT INTO role (role_name, role_code, description, privileges)
VALUES ('Service', 'ROLE_STAFF', 'Authorized personnel for system operation and maintenance, ensuring optimal performance and reliability.', 'SYSTEM_MAINTENANCE');

INSERT INTO role (role_name, role_code, description, privileges)
VALUES ('Doctor', 'ROLE_DOCTOR', 'Laboratory staff responsible for conducting tests, analyzing samples, and managing lab processes.', 'LAB_OPERATIONS');

INSERT INTO role (role_name, role_code, description, privileges)
VALUES ('Patient', 'ROLE_PATIENT', 'Patient user with permission to view their own test results only.', 'READ_ONLY');


-- User
INSERT INTO user (email, password, role_id)
VALUES ('admin@admin.com', '$2a$10$ENjuWcDRZZSfbF3TWfhVBeGtz7oR9/dUO0p4NAyFA.XjWDgfnM0VC', '1');