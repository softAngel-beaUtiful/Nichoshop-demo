create table `permissions` (
                               `id` INT(11) NOT NULL AUTO_INCREMENT,
                               `user_id` INT(11) NOT NULL,
                               `code` varchar(64),
                               PRIMARY KEY (`id`),
                               UNIQUE KEY `section_group_code` (`user_id`, `code`),
                               KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
