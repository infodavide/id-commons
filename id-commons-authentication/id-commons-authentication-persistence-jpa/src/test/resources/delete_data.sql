TRUNCATE TABLE users_properties;
TRUNCATE TABLE users;
TRUNCATE TABLE configuration_properties;

ALTER SEQUENCE sq_users RESTART WITH 1;
ALTER SEQUENCE sq_configuration_properties RESTART WITH 1;
