INSERT
IGNORE INTO role (role_name, role_code) VALUES
    ('Administrator', 'ROLE_ADMIN'),
    ('Laboratory Manager', 'ROLE_MANAGER'),
    ('Service', 'ROLE_STAFF'),
    ('Doctor', 'ROLE_DOCTOR'),
    ('Patient', 'ROLE_PATIENT');
-- permissions
INSERT
IGNORE INTO permissions (name, description) VALUES
('READ_ONLY', 'Only have right to view patient test orders and patient test order results.'),
('CREATE_TEST_ORDER', 'Have right to create a new patient test order.'),
('MODIFY_TEST_ORDER', 'Have right to modify information a patient test order.'),
('DELETE_TEST_ORDER', 'Have right to delete an exist test order.'),
('REVIEW_TEST_ORDER', 'Have right to review, modify test result of test order'),
('ADD_COMMENT', 'Have right to add a new comment for test result'),
('MODIFY_COMMENT', 'Have right to modify a comment.'),
('DELETE_COMMENT', 'Have right to delete a comment.'),
('VIEW_CONFIGURATION', 'Have right to view, add, modify and delete configurations.'),
('CREATE_CONFIGURATION', 'Have right to add a new configuration.'),
('MODIFY_CONFIGURATION', 'Have right to modify a configuration.'),
('DELETE_CONFIGURATION', 'Have right to delete a configuration.'),
('VIEW_USER', 'Have right to view all user profiles.'),
('CREATE_USER', 'Have right to create a new user.'),
('MODIFY_USER', 'Have right to modify a user.'),
('DELETE_USER', 'Have right to delete a user.'),
('LOCK_AND_UNLOCK_USER', 'Have right to lock or unlock a user.'),
('VIEW_ROLE', 'Have right to view all role privileges.'),
('CREATE_ROLE', 'Have right to create a new custom role.'),
('UPDATE_ROLE', 'Have right to modify privileges of custom role.'),
('DELETE_ROLE', 'Have right to delete a custom role.'),
('VIEW_EVENT_LOGS', 'Have right to view event logs'),
('ADD_REAGENTS', 'Have right to add new reagents.'),
('MODIFY_REAGENTS', 'Have right to modify reagent information.'),
('DELETE_REAGENTS', 'Have right to delete a reagents'),
('ADD_INSTRUMENT', 'Have right to add a new instrument into system management'),
('VIEW_INSTRUMENT', 'Have right to view all instrument and check instrument status.'),
('ACTIVATE_OR_DEACTIVATE_INSTRUMENT', 'Have right to activate or deactivate instrument'),
('EXECUTE_BLOOD_TESTING', 'Have right to execute a blood testing');
-- Role_Permissions
INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM role r, permissions p
WHERE r.role_code = 'ROLE_ADMIN';

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM role r
         JOIN permissions p ON p.name IN (
                                          'READ_ONLY',
                                          'VIEW_USER', 'CREATE_USER', 'MODIFY_USER', 'DELETE_USER', 'LOCK_AND_UNLOCK_USER',
                                          'VIEW_ROLE', 'CREATE_ROLE', 'UPDATE_ROLE', 'DELETE_ROLE',
                                          'VIEW_EVENT_LOGS',
                                          'ADD_INSTRUMENT', 'VIEW_INSTRUMENT', 'ACTIVATE_OR_DEACTIVATE_INSTRUMENT'
    )
WHERE r.role_code = 'ROLE_MANAGER';

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM role r
         JOIN permissions p ON p.name IN (
                                          'VIEW_CONFIGURATION', 'CREATE_CONFIGURATION', 'MODIFY_CONFIGURATION', 'DELETE_CONFIGURATION',
                                          'VIEW_EVENT_LOGS',
                                          'ADD_REAGENTS', 'MODIFY_REAGENTS', 'DELETE_REAGENTS',
                                          'ADD_INSTRUMENT', 'VIEW_INSTRUMENT', 'ACTIVATE_OR_DEACTIVATE_INSTRUMENT',
                                          'EXECUTE_BLOOD_TESTING'
    )
WHERE r.role_code = 'ROLE_STAFF';

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM role r
         JOIN permissions p ON p.name IN (
                                          'READ_ONLY',
                                          'CREATE_TEST_ORDER', 'MODIFY_TEST_ORDER', 'DELETE_TEST_ORDER', 'REVIEW_TEST_ORDER',
                                          'ADD_COMMENT', 'MODIFY_COMMENT', 'DELETE_COMMENT',
                                          'VIEW_EVENT_LOGS',
                                          'ADD_REAGENTS', 'MODIFY_REAGENTS', 'DELETE_REAGENTS',
                                          'ADD_INSTRUMENT', 'VIEW_INSTRUMENT', 'ACTIVATE_OR_DEACTIVATE_INSTRUMENT',
                                          'EXECUTE_BLOOD_TESTING'
    )
WHERE r.role_code = 'ROLE_DOCTOR';

-- User
INSERT IGNORE INTO user (email, password, role_id, full_name)
VALUES ('admin@admin.com', '$2a$12$1s9Bj8qTGJUfuiEbNtflUeATUI0auNDDA8Mji0gxA2WqGjeUUVGJe', 1, 'Administrator');

INSERT IGNORE INTO user (email, password, role_id, full_name)
VALUES ('staff@staff.com', '$2a$12$rmJWdZfP/Jtjp4wsY/qeoOW6LZBelLxlVH4TqI2EUiFX.6ILuqWVO', 3, 'System Staff');

INSERT IGNORE INTO user (email, password, role_id, full_name)
VALUES ('doctor@doctor.com', '$2a$12$idUpNgOL6/1BD9nXfNBtV.YJellA6HAEDFyosUwLxbyLT3rrdFsR6', 4, 'Dr. John Smith');

INSERT IGNORE INTO user (email, password, role_id, full_name)
VALUES ('manager@manager.com', '$2a$12$yRLO5BWuDh2DqOeyoDr1QObKGP4d86nhlp2/glzixzr9QRO0qNhpe', 2, 'Manager');
