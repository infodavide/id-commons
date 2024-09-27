INSERT INTO settings (deletable, read_only, name, cdate, mdate, data_type, data, scope, data_type_def, label, mini, maxi, default_data) VALUES
  (false, true, 'schema.version', NOW(), NOW(), 'STRING', '0.0.1', NULL, NULL, 'DB schema version', NULL, NULL, '1.0.0'),
  (false, true, 'application.grantedTo', NOW(), NOW(), 'STRING', 'Infodavid', NULL, NULL, 'Customer', NULL, NULL, 'Infodavid');

INSERT INTO users (deletable, name, display_name, cdate, mdate, connections_count, email, expiration_date, last_connection_date, last_ip, locked, password, role) VALUES
  (false, 'admin', 'Administrator', NOW(), NOW(), 0, 'Support@infodavid.org', NULL, NULL, NULL, false, '21232F297A57A5A743894A0E4A801FC3', 'ADMINISTRATOR'),
  (false, 'anonymous', 'Anonymous', NOW(), NOW(), 0, NULL, NULL, NULL, NULL, false, '', 'ANONYMOUS');
