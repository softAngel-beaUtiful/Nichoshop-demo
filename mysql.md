## MySQL config file
It is required to setup in mysql config
```
[client]
default-character-set = utf8mb4

[mysql]
default-character-set = utf8mb4

[mysqld]
character-set-client-handshake = FALSE
character-set-server = utf8mb4
collation-server = utf8mb4_unicode_ci
```

This it important when users have an ability to write text (messages for example), this will
add full utf8 support including letters encoded with more than 3 bytes.