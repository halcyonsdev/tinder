CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE IF NOT EXISTS user_geolocations
(
    id       UUID                  PRIMARY KEY,
    location GEOMETRY(Point, 4326) NOT NULL,
    user_id  UUID                  NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(id)
)