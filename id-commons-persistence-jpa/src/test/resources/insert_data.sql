INSERT INTO settings (deletable, read_only, name, cdate, mdate, data_type, data, scope, data_type_def, label, default_data) VALUES
  (false, true, 'schema.version', NOW(), NOW(), 'STRING', '${project.version}', NULL, NULL, 'DB schema version', '1.0.0'),
  (false, true, 'application.grantedTo', NOW(), NOW(), 'STRING', 'Infodavid', NULL, NULL, 'Customer', 'Infodavid'),
  (false, false, 'Param1', NOW(), NOW(), 'STRING', 'Donnée1', NULL, NULL, 'Param 1', NULL),
  (true, false, 'Param2', NOW(), NOW(), 'STRING', 'Donnée2', NULL, NULL, 'Param 2', NULL),
  (true, false, 'Param3', NOW(), NOW(), 'STRING', 'Donnée2', 'Scope1', NULL, 'Param 3', NULL);

INSERT INTO users (deletable, name, display_name, cdate, mdate, connections_count, email, expiration_date, last_connection_date, last_ip, locked, password, roles) VALUES
  (false, 'admin', 'Administrator', NOW(), NOW(), 0, 'Support@infodavid.org', NULL, NULL, NULL, false, '21232F297A57A5A743894A0E4A801FC3', 'ROLE_ADMIN'),
  (false, 'anonymous', 'Anonymous', NOW(), NOW(), 0, NULL, NULL, NULL, NULL, false, '', 'ROLE_ANONYMOUS'),
  (true, 'user1', 'User 1', NOW(), NOW(), 2, 'user1@infodavid.org', NULL, NOW(), '192.168.0.101', false, '24C9E15E52AFC47C225B757E7BEE1F9D', 'ROLE_USER'),
  (true, 'user2', 'User 2', NOW(), NOW(), 2, 'user2@infodavid.org', NULL, NOW(), '192.168.0.102', false, '24C9E15E52AFC47C225B757E7BEE1F9D', 'ROLE_USER'),
  (true, 'user3', 'User 3', NOW(), NOW(), 2, 'user3@infodavid.org', NULL, NOW(), '192.168.0.103', false, '24C9E15E52AFC47C225B757E7BEE1F9D', 'ROLE_USER');
UPDATE users set connections_count=5,email='admin@infodavid.org',last_ip='192.168.0.100',last_connection_date=NOW() WHERE name='admin';

INSERT INTO users_properties (user_id, name, data, data_type, data_type_def, label, default_data) VALUES
  (1, 'prop10', 'val10', 'STRING', NULL, 'Property 10', NULL),
  (1, 'prop11', 'val11', 'STRING', NULL, 'Property 11', NULL),
  (3, 'prop20', 'val20', 'STRING', NULL, 'Property 20', NULL);
