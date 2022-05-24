# CREATE TABLE products (
#   id                    INT UNSIGNED      NOT NULL AUTO_INCREMENT,
#   seller_id             INT UNSIGNED      NOT NULL,
#   title                 VARCHAR(80)       NOT NULL,
#   subtitle              VARCHAR(80)       NOT NULL,
#   item_condition        TINYINT           NOT NULL,
#   condition_desc        TEXT,
#   images                TEXT              NOT NULL,
#   description           TEXT              NOT NULL,
#   fixed_price           BIT(1)            NOT NULL,
#   starting_price        INT UNSIGNED      NOT NULL,
#   now_price             INT UNSIGNED,
#   best_offer            BIT(1)            NOT NULL DEFAULT 0,
#   quantity              SMALLINT UNSIGNED NOT NULL,
#   duration              INT UNSIGNED      NOT NULL,
#   credit                BIT(1)            NOT NULL,
#   paypal                BIT(1)            NOT NULL,
#   bitcoin               BIT(1)            NOT NULL,
#   gift                  BIT(1)            NOT NULL,
#   returns               BIT(1)            NOT NULL,
#   returns_details       TEXT,
#   location              VARCHAR(64)       NOT NULL,
#   domestic_service      TINYINT           NOT NULL,
#   domestic_cost         INT UNSIGNED,
#   domestic_collect      BIT(1)            NOT NULL,
#   international_service TINYINT,
#   international_cost    INT UNSIGNED,
#   post_to               VARCHAR(64),
#   creation_time         BIGINT UNSIGNED   NOT NULL,
#   state                 TINYINT           NOT NULL DEFAULT 0,
#   PRIMARY KEY (id)
# );

CREATE TABLE products_1 (
  id                    INT UNSIGNED      NOT NULL AUTO_INCREMENT,
  seller_id             INT UNSIGNED      NOT NULL,
  cat_id                INT UNSIGNED      NOT NULL,
  title                 VARCHAR(256)       NOT NULL,
  description           TEXT              NOT NULL,
  creation_time         BIGINT UNSIGNED   NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE product_variants (
  id                    INT UNSIGNED      NOT NULL AUTO_INCREMENT,
  product_id            INT UNSIGNED      NOT NULL,
  title                 VARCHAR(256),
  description           TEXT,
  `condition`           ENUM('new', 'used') NOT NULL DEFAULT 'new',
  price                 INT UNSIGNED      NOT NULL,
  currency_id           INT UNSIGNED      NOT NULL,
  amount                INT UNSIGNED      NOT NULL,
  creation_time         BIGINT UNSIGNED   NOT NULL,
  PRIMARY KEY (id),
  KEY `product_id` (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE product_reservation (
  id                    INT UNSIGNED      NOT NULL AUTO_INCREMENT,
  product_id            INT UNSIGNED      NOT NULL,
  product_variant_id    INT UNSIGNED      NOT NULL,
  user_id               INT UNSIGNED      NOT NULL,
  seller_id             INT UNSIGNED      NOT NULL,
  creation_time         BIGINT UNSIGNED   NOT NULL,
  reservation_period    BIGINT UNSIGNED   NOT NULL DEFAULT 300000,
  amount                INT UNSIGNED      NOT NULL DEFAULT 1,
  price                 INT UNSIGNED      NOT NULL,
  currency_id           INT UNSIGNED      NOT NULL,
  product_data          BLOB              NOT NULL,
  PRIMARY KEY (id),
  KEY `user_id` (user_id),
  KEY `seller_id` (seller_id)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE product_purchases (
  id                    INT UNSIGNED      NOT NULL AUTO_INCREMENT,
  reservation_id        INT UNSIGNED      NOT NULL,
  creation_time         BIGINT UNSIGNED   NOT NULL,
  data                  BLOB,
  PRIMARY KEY (id),
  KEY `reservation_id` (reservation_id)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- ################ BID
CREATE TABLE auctions (
  id                    INT UNSIGNED      NOT NULL AUTO_INCREMENT,
  product_id            INT UNSIGNED      NOT NULL,
  variant_id            INT UNSIGNED      NOT NULL,
  product               BLOB              NOT NULL,
  currency_id           INT UNSIGNED      NOT NULL,
  start_price           INT UNSIGNED      NOT NULL,
  bid_step              INT UNSIGNED      NOT NULL,
  start_time            BIGINT UNSIGNED   NOT NULL,
  finish_time           BIGINT UNSIGNED   NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE product_bids (
  id                    INT UNSIGNED      NOT NULL AUTO_INCREMENT,
  auction_id            INT UNSIGNED      NOT NULL,
  creation_time         BIGINT UNSIGNED   NOT NULL,
  user_id               INT UNSIGNED      NOT NULL,
  amount                INT UNSIGNED      NOT NULL,
  currency_id           INT UNSIGNED      NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY `winner` (auction_id, creation_time, amount, currency_id)
);

-- ##################### OFFERS
CREATE TABLE product_offer_scopes (
  id                    INT UNSIGNED      NOT NULL AUTO_INCREMENT,
  product_id            INT UNSIGNED      NOT NULL,
  variant_id            INT UNSIGNED      NOT NULL,
  start_time            BIGINT UNSIGNED   NOT NULL,
  end_time              BIGINT UNSIGNED   NOT NULL,
  closed                BOOLEAN   NOT NULL DEFAULT FALSE,
  PRIMARY KEY (id)
);
# CREATE TABLE offers (
#   id                     INT UNSIGNED      NOT NULL AUTO_INCREMENT,
#   product_offer_scope_id INT UNSIGNED      NOT NULL,
#   user_id                INT UNSIGNED      NOT NULL,
#   offer_price            INT UNSIGNED      NOT NULL,
#   currency_id            INT UNSIGNED      NOT NULL,
#   offer_qty              INT UNSIGNED      NOT NULL,
#   message                TEXT,
#   timestamp              BIGINT UNSIGNED   NOT NULL,
#   state                  ENUM('open', 'accepted','rejected') NOT NULL DEFAULT 'open',
#   PRIMARY KEY (id)
# );
