CREATE TABLE users (
  id                      INT UNSIGNED    NOT NULL AUTO_INCREMENT,
  userid                  VARCHAR(16)     NOT NULL,
  password                CHAR(60)        NOT NULL,
  email                   VARCHAR(64)     NOT NULL,
  name                    VARCHAR(16)     NOT NULL,
  lname                   VARCHAR(16)     NOT NULL,
  registration_date       BIGINT UNSIGNED NOT NULL,
  email_confirmed         BIT(1)          NOT NULL DEFAULT 0,
  suspended               BIT(1)          NOT NULL DEFAULT 0,
  deleted                 BIT(1)          NOT NULL DEFAULT 0,
  deleted_date            BIGINT UNSIGNED,
  gift_card_balance       INT UNSIGNED    NOT NULL DEFAULT 0,
  question                VARCHAR(64),
  answer                  VARCHAR(16),
  registration_address_id INT UNSIGNED,
  from_address_id         INT UNSIGNED,
  to_address_id           INT UNSIGNED,
  return_address_id       INT UNSIGNED,
  payment_address_id      INT UNSIGNED,
  PRIMARY KEY (id),
  UNIQUE (userid),
  UNIQUE (email)
);

CREATE TABLE sessions (
  id            INT UNSIGNED NOT NULL AUTO_INCREMENT,
  userid        VARCHAR(16)  NOT NULL,
  hash          VARCHAR(32)  NOT NULL UNIQUE,
  creation_time BIGINT       NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (userid) REFERENCES users (userid)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE tokens (
  id            INT UNSIGNED NOT NULL AUTO_INCREMENT,
  userid        VARCHAR(16)  NOT NULL,
  hash          VARCHAR(64)  NOT NULL UNIQUE,
  creation_time BIGINT       NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (userid) REFERENCES users (userid)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

CREATE TABLE credit_cards (
  id        INT UNSIGNED    NOT NULL AUTO_INCREMENT,
  user_id   INT UNSIGNED    NOT NULL,
  holder    VARCHAR(33)     NOT NULL,
  card_type TINYINT         NOT NULL,
  number    BIGINT UNSIGNED NOT NULL,
  month     TINYINT         NOT NULL,
  year      SMALLINT        NOT NULL,
  code      SMALLINT        NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE paypal_accounts (
  id          INT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id     INT UNSIGNED NOT NULL,
  email       VARCHAR(64)  NOT NULL,
  primary_acc BIT(1)       NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE addresses (
  id             INT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id        INT UNSIGNED NOT NULL,
  credit_card_id INT UNSIGNED,
  contact_name   VARCHAR(33)  NOT NULL,
  address1       VARCHAR(32)  NOT NULL,
  address2       VARCHAR(32),
  city           VARCHAR(16)  NOT NULL,
  state          VARCHAR(16)  NOT NULL,
  zip            VARCHAR(16)  NOT NULL,
  country        CHAR(2)      NOT NULL,
  phone          VARCHAR(16)  NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (credit_card_id) REFERENCES credit_cards (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE categories (
  id        INT UNSIGNED NOT NULL AUTO_INCREMENT,
  name      VARCHAR(32)  NOT NULL,
  leaf      BIT(1)       NOT NULL DEFAULT 0,
  parent_id INT          NOT NULL DEFAULT 0,
  ord       INT UNSIGNED NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  INDEX parent_id_idx (parent_id ASC)
);

UPDATE categories
SET ord = id;

CREATE TABLE category_fields (
  id          INT UNSIGNED NOT NULL AUTO_INCREMENT,
  category_id INT UNSIGNED NOT NULL,
  name        VARCHAR(16)  NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (category_id) REFERENCES categories (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE category_fields_values (
  id                INT UNSIGNED NOT NULL AUTO_INCREMENT,
  category_field_id INT UNSIGNED NOT NULL,
  name              VARCHAR(100) NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (category_field_id) REFERENCES category_fields (id)
    ON DELETE CASCADE
);

CREATE TABLE products (
  id                    INT UNSIGNED      NOT NULL AUTO_INCREMENT,
  seller_id             INT UNSIGNED      NOT NULL,
  cat_id                INT UNSIGNED      NOT NULL,
  title                 VARCHAR(80)       NOT NULL,
  subtitle              VARCHAR(80)       NOT NULL,
  item_condition        TINYINT           NOT NULL,
  condition_desc        TEXT,
  images                TEXT              NOT NULL,
  description           TEXT              NOT NULL,
  fixed_price           BIT(1)            NOT NULL,
  starting_price        INT UNSIGNED      NOT NULL,
  now_price             INT UNSIGNED,
  best_offer            BIT(1)            NOT NULL DEFAULT 0,
  quantity              SMALLINT UNSIGNED NOT NULL,
  duration              INT UNSIGNED      NOT NULL,
  credit                BIT(1)            NOT NULL,
  paypal                BIT(1)            NOT NULL,
  bitcoin               BIT(1)            NOT NULL,
  gift                  BIT(1)            NOT NULL,
  returns               BIT(1)            NOT NULL,
  returns_details       TEXT,
  location              VARCHAR(64)       NOT NULL,
  domestic_service      TINYINT           NOT NULL,
  domestic_cost         INT UNSIGNED,
  domestic_collect      BIT(1)            NOT NULL,
  international_service TINYINT,
  international_cost    INT UNSIGNED,
  post_to               VARCHAR(64),
  creation_time         BIGINT UNSIGNED   NOT NULL,
  state                 TINYINT           NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  FOREIGN KEY (seller_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (cat_id) REFERENCES categories (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE product_field_values (
  id         INT UNSIGNED NOT NULL AUTO_INCREMENT,
  field_id   INT UNSIGNED NOT NULL,
  product_id INT UNSIGNED NOT NULL,
  value      VARCHAR(32)  NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (field_id) REFERENCES category_fields (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE watchlist (
  id         INT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id    INT UNSIGNED NOT NULL,
  product_id INT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  UNIQUE (user_id, product_id)
);

CREATE TABLE bids (
  id         INT UNSIGNED    NOT NULL AUTO_INCREMENT,
  bidder_id  INT UNSIGNED    NOT NULL,
  product_id INT UNSIGNED    NOT NULL,
  price      INT UNSIGNED    NOT NULL,
  auto       BIT(1)          NOT NULL DEFAULT 0,
  timestamp  BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (bidder_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE offers (
  id         INT UNSIGNED      NOT NULL AUTO_INCREMENT,
  user_id    INT UNSIGNED      NOT NULL,
  product_id INT UNSIGNED      NOT NULL,
  price      INT UNSIGNED      NOT NULL,
  quantity   SMALLINT UNSIGNED NOT NULL,
  accepted   BIT(1)            NOT NULL DEFAULT 0,
  paid       BIT(1)            NOT NULL DEFAULT 0,
  offer_time BIGINT UNSIGNED   NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE sells (
  id         INT UNSIGNED      NOT NULL AUTO_INCREMENT,
  seller_id  INT UNSIGNED      NOT NULL,
  buyer_id   INT UNSIGNED      NOT NULL,
  product_id INT UNSIGNED      NOT NULL,
  quantity   SMALLINT UNSIGNED NOT NULL,
  price      INT UNSIGNED      NOT NULL,
  paid       BIT(1)            NOT NULL DEFAULT 0,
  dispatched BIT(1)            NOT NULL DEFAULT 0,
  sold_time  BIGINT UNSIGNED   NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (buyer_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (seller_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (product_id) REFERENCES products (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE feedback (
  id                INT UNSIGNED    NOT NULL AUTO_INCREMENT,
  seller_id         INT UNSIGNED    NOT NULL,
  sell_id           INT UNSIGNED    NOT NULL,
  rating            TINYINT         NOT NULL,
  item_as_described TINYINT         NOT NULL,
  communication     TINYINT         NOT NULL,
  shipping_time     TINYINT         NOT NULL,
  shipping_charges  TINYINT         NOT NULL,
  message           VARCHAR(80),
  timestamp         BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (seller_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE tracking (
  id      INT UNSIGNED NOT NULL AUTO_INCREMENT,
  sell_id INT UNSIGNED NOT NULL,
  number  VARCHAR(20)  NOT NULL,
  courier VARCHAR(20)  NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (sell_id) REFERENCES sells (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE product_views (
  product_id INT UNSIGNED    NOT NULL,
  timestamp  BIGINT UNSIGNED NOT NULL,
  FOREIGN KEY (product_id) REFERENCES products (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE message_folders (
  id      INT UNSIGNED NOT NULL AUTO_INCREMENT,
  name    VARCHAR(16)  NOT NULL,
  user_id INT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE messages (
  id            INT UNSIGNED    NOT NULL AUTO_INCREMENT,
  from_id       INT UNSIGNED,
  from_userid   VARCHAR(16),
  to_id         INT UNSIGNED    NOT NULL,
  subject       VARCHAR(64)     NOT NULL,
  message       TEXT            NOT NULL,
  creation_time BIGINT UNSIGNED NOT NULL,
  flag          BIT(1)          NOT NULL DEFAULT 0,
  msg_read      BIT(1)          NOT NULL DEFAULT 0,
  item_id       INT UNSIGNED,
  item_title    VARCHAR(80),
  folder_id     INT             NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  FOREIGN KEY (from_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (to_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (item_id) REFERENCES products (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE email_confirmation (
  user_id INT UNSIGNED NOT NULL,
  code    CHAR(32),
  PRIMARY KEY (user_id),
  FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE
);

CREATE TABLE password_recovery (
  user_id INT UNSIGNED NOT NULL,
  code    CHAR(32),
  PRIMARY KEY (user_id),
  FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE suggestions (
  id        INT UNSIGNED    NOT NULL AUTO_INCREMENT,
  user_id   INT UNSIGNED,
  topic     TINYINT         NOT NULL,
  message   TEXT            NOT NULL,
  timestamp BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE tell_us (
  id        INT UNSIGNED    NOT NULL AUTO_INCREMENT,
  message   TEXT            NOT NULL,
  timestamp BIGINT UNSIGNED NOT NULL,
  ip        VARCHAR(15)     NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE gift_card_orders (
  id             INT UNSIGNED    NOT NULL AUTO_INCREMENT,
  buyer_id       INT UNSIGNED    NOT NULL,
  amount         INT UNSIGNED    NOT NULL,
  sender_name    VARCHAR(64)     NOT NULL,
  message        TEXT            NOT NULL,
  recipients     TEXT            NOT NULL,
  timestamp      BIGINT UNSIGNED NOT NULL,
  transaction_id VARCHAR(32)     NOT NULL,
  delivery_date  BIGINT UNSIGNED,
  FOREIGN KEY (buyer_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY (id)
);

CREATE TABLE gift_cards (
  id              INT UNSIGNED NOT NULL AUTO_INCREMENT,
  code            CHAR(32)     NOT NULL,
  order_id        INT UNSIGNED NOT NULL,
  activator_id    INT UNSIGNED,
  email           VARCHAR(64)  NOT NULL,
  delivered       BIT(1)       NOT NULL DEFAULT 0,
  activation_date BIGINT UNSIGNED,
  PRIMARY KEY (id),
  FOREIGN KEY (order_id) REFERENCES gift_card_orders (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (activator_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  UNIQUE (code)
);

CREATE TABLE bid_retractions (
  id        INT UNSIGNED     NOT NULL AUTO_INCREMENT,
  item_id   INT UNSIGNED     NOT NULL,
  user_id   INT UNSIGNED     NOT NULL,
  problem   TINYINT UNSIGNED NOT NULL,
  requested BIGINT UNSIGNED  NOT NULL,
  retracted BIT(1)           NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  FOREIGN KEY (item_id) REFERENCES products (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE cases (
  id                INT UNSIGNED     NOT NULL AUTO_INCREMENT,
  sell_id           INT UNSIGNED     NOT NULL,
  opened_by_id      INT UNSIGNED     NOT NULL,
  opened_against_id INT UNSIGNED     NOT NULL,
  problem           TINYINT UNSIGNED NOT NULL,
  message           TEXT             NOT NULL,
  opened            BIGINT UNSIGNED  NOT NULL,
  closed            BIT(1)           NOT NULL DEFAULT 0,
  item_problem      TINYINT UNSIGNED,
  help_option       TINYINT UNSIGNED,
  phone             VARCHAR(16),
  refund            INT UNSIGNED,
  PRIMARY KEY (id),
  FOREIGN KEY (sell_id) REFERENCES sells (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (opened_by_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (opened_against_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE case_messages (
  id        INT UNSIGNED    NOT NULL AUTO_INCREMENT,
  case_id   INT UNSIGNED    NOT NULL,
  sender_id INT UNSIGNED    NOT NULL,
  message   TEXT            NOT NULL,
  timestamp BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (case_id) REFERENCES cases (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (sender_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE case_changes (
  id          INT UNSIGNED     NOT NULL AUTO_INCREMENT,
  case_id     INT UNSIGNED     NOT NULL,
  help_option TINYINT UNSIGNED NOT NULL,
  refund      INT UNSIGNED,
  timestamp   BIGINT UNSIGNED  NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (case_id) REFERENCES cases (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE donations (
  id         INT UNSIGNED    NOT NULL AUTO_INCREMENT,
  user_id    INT UNSIGNED    NOT NULL,
  amount     INT UNSIGNED    NOT NULL,
  timestamp  BIGINT UNSIGNED NOT NULL,
  profile_id VARCHAR(96),
  FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY (id)
);

CREATE TABLE scheduled_payments (
  id           INT UNSIGNED    NOT NULL AUTO_INCREMENT,
  from_id      INT UNSIGNED    NOT NULL,
  to_id        INT UNSIGNED    NOT NULL,
  amount       INT UNSIGNED    NOT NULL,
  payment_date BIGINT UNSIGNED NOT NULL,
  delivered    BIT(1)          NOT NULL DEFAULT 0,
  pay_key      VARCHAR(32),
  FOREIGN KEY (from_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  FOREIGN KEY (to_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY (id)
);

CREATE TABLE chat_sessions (
  id      INT UNSIGNED    NOT NULL AUTO_INCREMENT,
  user_id INT UNSIGNED    NOT NULL,
  created BIGINT UNSIGNED NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  PRIMARY KEY (id)
);

CREATE TABLE chat_messages (
  session_id INT UNSIGNED NOT NULL,
  message    TEXT         NOT NULL,
  timestamp  BIGINT UNSIGNED,
  FOREIGN KEY (session_id) REFERENCES chat_sessions (id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE admin_settings (
  auction_enabled    BIT(1)   NOT NULL DEFAULT 1,
  disabled_countries TEXT     NOT NULL,
  password           CHAR(32) NOT NULL
);

INSERT INTO admin_settings (disabled_countries, password) VALUES ('[]', '21232f297a57a5a743894a0e4a801fc3');

CREATE TABLE disabled_ips (
  ip VARCHAR(15) NOT NULL,
  PRIMARY KEY (ip)
);