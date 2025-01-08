INSERT INTO configuration_properties (deletable, read_only, application, name, creation_date, modification_date, data_type, data, scope, data_type_def, label, default_data) VALUES
  (false, true, 'app1', 'schema.version', NOW(), NOW(), 'STRING', '${project.version}', NULL, NULL, 'DB schema version', '1.0.0'),
  (false, true, 'app1', 'application.grantedTo', NOW(), NOW(), 'STRING', 'Infodavid', NULL, NULL, 'Customer', 'Infodavid'),
  (false, false, 'app1', 'Param1', NOW(), NOW(), 'STRING', 'Donnée1', NULL, NULL, 'Param 1', NULL),
  (true, false, 'app1', 'Param2', NOW(), NOW(), 'STRING', 'Donnée2', NULL, NULL, 'Param 2', NULL),
  (true, false, 'app1', 'Param3', NOW(), NOW(), 'STRING', 'Donnée2', 'Scope1', NULL, 'Param 3', NULL);

INSERT INTO groups (name, creation_date, modification_date, roles) VALUES
  ('admins', NOW(), NOW(), 'ROLE_ADMIN'),
  ('users', NOW(), NOW(), 'ROLE_USER');

INSERT INTO groups_properties (group_id, name, data, data_type, data_type_def, label, default_data) VALUES
  (1, 'prop10', 'val10', 'STRING', NULL, 'Property 10', NULL),
  (1, 'prop11', 'val11', 'STRING', NULL, 'Property 11', NULL),
  (2, 'prop20', 'val20', 'STRING', NULL, 'Property 20', NULL);
  
INSERT INTO users (name, display_name, creation_date, modification_date, email, expiration_date, last_connection_date, last_ip, locked, password) VALUES
  ('admin', 'Administrator', NOW(), NOW(), 'Support@infodavid.org', NULL, NULL, NULL, false, '21232F297A57A5A743894A0E4A801FC3'),
  ('anonymous', 'Anonymous', NOW(), NOW(), NULL, NULL, NULL, NULL, false, ''),
  ('user1', 'User 1', NOW(), NOW(), 'user1@infodavid.org', NULL, NOW(), '192.168.0.101', false, '24C9E15E52AFC47C225B757E7BEE1F9D'),
  ('user2', 'User 2', NOW(), NOW(), 'user2@infodavid.org', NULL, NOW(), '192.168.0.102', false, '24C9E15E52AFC47C225B757E7BEE1F9D'),
  ('user3', 'User 3', NOW(), NOW(), 'user3@infodavid.org', NULL, NOW(), '192.168.0.103', false, '24C9E15E52AFC47C225B757E7BEE1F9D');
  UPDATE users set email='admin@infodavid.org',last_ip='192.168.0.100',last_connection_date=NOW() WHERE name='admin';

INSERT INTO users_properties (user_id, name, data, data_type, data_type_def, label, default_data) VALUES
  (1, 'prop10', 'val10', 'STRING', NULL, 'Property 10', NULL),
  (1, 'prop11', 'val11', 'STRING', NULL, 'Property 11', NULL),
  (3, 'prop20', 'val20', 'STRING', NULL, 'Property 20', NULL);
  
INSERT INTO users_groups (user_id, group_id) VALUES
  (1, 1),
  (3, 2),
  (4, 2),
  (5, 2);