TRUNCATE TABLE settings;
TRUNCATE TABLE users_properties;
TRUNCATE TABLE users;

ALTER SEQUENCE sq_settings RESTART WITH 1;
ALTER SEQUENCE sq_users RESTART WITH 1;