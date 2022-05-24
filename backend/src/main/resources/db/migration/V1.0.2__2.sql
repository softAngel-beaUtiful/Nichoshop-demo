-- Up
alter table `users` add COLUMN (`phone` varchar(32));

alter table `tokens` add COLUMN (`hash_session` varchar(64) NOT NULL UNIQUE);

create table `password_reset` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `type` enum('email','phone_sms','phone_call') COLLATE utf8_unicode_ci NOT NULL,
  `created` datetime NOT NULL,
  `hash` varchar(64),
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `hash` (`hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

create table `permissions` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `code` varchar(64),
  PRIMARY KEY (`id`),
  UNIQUE KEY `section_group_code` (`user_id`, `code`),
  KEY `code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- Down
alter table `users` drop COLUMN `phone`;

alter table `tokens` drop COLUMN `hash_session`;

drop table `password_reset`;

drop table `permissions`;