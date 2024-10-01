-- see https://mariadb.com/kb/en/library/mysql_tzinfo_to_sql/
-- and unix command: mysql_tzinfo_to_sql /usr/share/zoneinfo | mysql -u root mysql -p
SET GLOBAL time_zone = 'Europe/Paris';

-- settings
DROP TABLE IF EXISTS settings;
CREATE TABLE IF NOT EXISTS settings (
  id bigint(19) unsigned AUTO_INCREMENT PRIMARY KEY,
  archiving_date datetime NULL,
  deletable boolean NOT NULL DEFAULT true,
  read_only boolean NOT NULL DEFAULT false,
  name varchar(48) NOT NULL,
  data varchar(1024) DEFAULT NULL,
  cdate datetime NOT NULL,
  mdate datetime NOT NULL,
  data_type varchar(48) NOT NULL,
  data_type_def varchar(255) DEFAULT NULL,
  scope varchar(48) DEFAULT NULL,
  label varchar(128) DEFAULT NULL,
  mini double DEFAULT NULL,
  maxi double DEFAULT NULL,
  default_data varchar(1024) DEFAULT NULL,
  CONSTRAINT unq_settings UNIQUE (name,scope)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE settings ADD INDEX idx_settings_archiving_date (archiving_date);
ALTER TABLE settings ADD INDEX idx_settings_deletable (deletable);
ALTER TABLE settings ADD INDEX idx_settings_readonly (read_only);
ALTER TABLE settings ADD INDEX idx_settings_name (name);
ALTER TABLE settings ADD INDEX idx_settings_type (data_type);
ALTER TABLE settings ADD INDEX idx_settings_scope (scope);

-- users
DROP TABLE IF EXISTS users_properties;
DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users (
  id bigint(19) unsigned AUTO_INCREMENT PRIMARY KEY,
  archiving_date datetime NULL,
  deletable boolean NOT NULL DEFAULT true,
  name varchar(48) NOT NULL,
  display_name varchar(96) NOT NULL,
  cdate datetime NOT NULL,
  mdate datetime NOT NULL,
  expiration_date date DEFAULT NULL,
  last_connection_date datetime DEFAULT NULL,
  connections_count int(10) unsigned NOT NULL DEFAULT 0,
  email varchar(255) DEFAULT NULL,
  last_ip varchar(48) DEFAULT NULL,
  locked boolean NOT NULL DEFAULT false,
  password varchar(48) NOT NULL,
  roles varchar(512) NOT NULL DEFAULT 'GUEST',
  CONSTRAINT unq_users UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE users ADD INDEX idx_users_archiving_date (archiving_date);
ALTER TABLE users ADD INDEX idx_users_deletable (deletable);
ALTER TABLE users ADD INDEX idx_users_name (name);
ALTER TABLE users ADD INDEX idx_users_displayname (display_name);
ALTER TABLE users ADD INDEX idx_users_locked (locked);
ALTER TABLE users ADD INDEX idx_users_email (email);
ALTER TABLE users ADD INDEX idx_users_lastip (last_ip);

-- users_properties
CREATE TABLE IF NOT EXISTS users_properties (
  user_id bigint(19) unsigned NOT NULL,
  name varchar(48) NOT NULL,
  read_only BOOLEAN DEFAULT false,
  data VARCHAR(1024) DEFAULT NULL,
  data_type VARCHAR(48) NOT NULL,
  data_type_def varchar(255) DEFAULT NULL,
  scope varchar(48) DEFAULT NULL,
  label varchar(128) DEFAULT NULL,
  mini double DEFAULT NULL,
  maxi double DEFAULT NULL,
  default_data varchar(1024) DEFAULT NULL,
  CONSTRAINT unq_usersproperties UNIQUE (user_id,name,scope),
  CONSTRAINT fk_usersproperties_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
