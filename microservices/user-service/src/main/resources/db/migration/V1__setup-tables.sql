CREATE TABLE IF NOT EXISTS users
(
    id           UUID          PRIMARY KEY,
    phone_number VARCHAR(20)   UNIQUE NOT NULL,
    first_name   VARCHAR(100)  NOT NULL,
    last_name    VARCHAR(100)  NOT NULL,
    password     VARCHAR(255)  NOT NULL,
    bio          VARCHAR(500),
    interests    JSONB
)