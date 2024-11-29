-- see https://mariadb.com/kb/en/library/mysql_tzinfo_to_sql/
-- and unix command: mysql_tzinfo_to_sql /usr/share/zoneinfo | mysql -u root mysql -p
SET GLOBAL time_zone = 'Europe/Paris';

-- configuration_properties
DROP TABLE IF EXISTS configuration_properties;
CREATE TABLE IF NOT EXISTS configuration_properties (
  id BIGINT(19) unsigned AUTO_INCREMENT PRIMARY KEY,
  archiving_date DATETIME NULL,
  deletable BOOLEAN NOT NULL DEFAULT true,
  read_only BOOLEAN NOT NULL DEFAULT false,
  name VARCHAR(128) NOT NULL,
  data VARCHAR(1024) DEFAULT NULL,
  creation_date DATETIME NOT NULL,
  modification_date DATETIME NOT NULL,
  data_type VARCHAR(48) NOT NULL,
  data_type_def VARCHAR(255) DEFAULT NULL,
  application VARCHAR(128) DEFAULT NULL,
  scope VARCHAR(128) DEFAULT NULL,
  label VARCHAR(128) DEFAULT NULL,
  mini DOUBLE DEFAULT NULL,
  maxi DOUBLE DEFAULT NULL,
  default_data VARCHAR(1024) DEFAULT NULL,
  CONSTRAINT unq_configuration_properties UNIQUE (name,application,scope)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE configuration_properties ADD INDEX idx_configuration_properties_application (application);
ALTER TABLE configuration_properties ADD INDEX idx_configuration_properties_archiving_date (archiving_date);
ALTER TABLE configuration_properties ADD INDEX idx_configuration_properties_deletable (deletable);
ALTER TABLE configuration_properties ADD INDEX idx_configuration_properties_readonly (read_only);
ALTER TABLE configuration_properties ADD INDEX idx_configuration_properties_name (name);
ALTER TABLE configuration_properties ADD INDEX idx_configuration_properties_type (data_type);
ALTER TABLE configuration_properties ADD INDEX idx_configuration_properties_scope (scope);

