INSERT INTO configuration_properties (deletable, read_only, name, creation_date, modification_date, data_type, data, scope, data_type_def, label, default_data) VALUES
  (false, true, 'schema.version', NOW(), NOW(), 'STRING', '${project.version}', NULL, NULL, 'DB schema version', '1.0.0'),
  (false, true, 'application.grantedTo', NOW(), NOW(), 'STRING', 'Infodavid', NULL, NULL, 'Customer', 'Infodavid'),
  (false, false, 'Param1', NOW(), NOW(), 'STRING', 'Donnée1', NULL, NULL, 'Param 1', NULL),
  (true, false, 'Param2', NOW(), NOW(), 'STRING', 'Donnée2', NULL, NULL, 'Param 2', NULL),
  (true, false, 'Param3', NOW(), NOW(), 'STRING', 'Donnée2', 'Scope1', NULL, 'Param 3', NULL);
