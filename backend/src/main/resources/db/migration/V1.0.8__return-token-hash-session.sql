alter table `tokens` add COLUMN (`hash_session` varchar(64) NOT NULL UNIQUE);

alter table tpermissions modify type enum('buyer', 'seller', 'customer_support', 'admin') default 'buyer' not null;



