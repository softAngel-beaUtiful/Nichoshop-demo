CREATE TABLE product_specific_attributes (
  id                    INT UNSIGNED      NOT NULL AUTO_INCREMENT,
  cat_id                INT UNSIGNED      NOT NULL,
  name                  VARCHAR(128)      NOT NULL,
-- string - string
-- int - integer
-- uint0 - unsigned integer starting with 0
-- uint1 - unsigned integer starting with 1
-- enum - values from enum. Enum values are stored in value_options
  value_type            ENUM('string', 'int', 'uint0', 'uint1', 'enum') NOT NULL DEFAULT 'string',
  value_options         BLOB,
  default_value         VARCHAR(256),
  creation_time         BIGINT UNSIGNED   NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


ALTER TABLE `categories` ADD COLUMN  (condition_type varchar(64));

create table `tpermissions` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `type` enum ('buyer','seller','subadmin', 'admin') NOT NULL DEFAULT 'buyer',
  PRIMARY KEY (`id`),
  UNIQUE KEY `section_group_code` (`user_id`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
