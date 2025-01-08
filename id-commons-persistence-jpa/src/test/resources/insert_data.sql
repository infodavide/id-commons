INSERT INTO configuration_properties (deletable, read_only, application, name, creation_date, modification_date, data_type, data, scope, data_type_def, default_data) VALUES
  (false, true, 'app1', 'schema.version', NOW(), NOW(), 'STRING', '${project.version}', NULL, NULL, '1.0.0'),
  (false, true, 'app1', 'application.grantedTo', NOW(), NOW(), 'STRING', 'Infodavid', NULL, NULL, 'Infodavid'),
  (false, false, 'app1', 'Param1', NOW(), NOW(), 'STRING', 'Donnée1', NULL, NULL, NULL),
  (true, false, 'app1', 'Param2', NOW(), NOW(), 'STRING', 'Donnée2', NULL, NULL, NULL),
  (true, false, 'app1', 'Param3', NOW(), NOW(), 'STRING', 'Donnée2', 'Scope1', NULL, NULL);
