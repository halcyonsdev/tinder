CREATE TABLE IF NOT EXISTS users
(
    id           UUID          PRIMARY KEY,
    created_at   TIMESTAMP     NOT NULL,
    phone_number VARCHAR(20)   UNIQUE NOT NULL,
    first_name   VARCHAR(100)  NOT NULL,
    last_name    VARCHAR(100)  NOT NULL,
    age          INT           NOT NULL,
    gender       VARCHAR(10)   NOT NULL,
    password     VARCHAR(255)  NOT NULL,
    bio          VARCHAR(500),
    interests    JSONB,
    avatar TEXT
);

CREATE TABLE IF NOT EXISTS users_preferences
(
    id      UUID         PRIMARY KEY,
    gender  VARCHAR(10),
    age     INT,
    user_id UUID         NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS users_images
(
    id         UUID PRIMARY KEY,
    image_name TEXT NOT NULL,
    user_id    UUID NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(id)
)