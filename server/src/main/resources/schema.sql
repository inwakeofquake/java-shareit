DROP TABLE IF EXISTS users, items, requests, bookings, comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name      VARCHAR(255) NOT NULL,
    email          VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user       PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
    );

CREATE TABLE IF NOT EXISTS requests (
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description  VARCHAR(1000) NOT NULL,
    requestor_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    created TIMESTAMP,
    CONSTRAINT pk_request PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS items (
    id BIGINT     GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name          VARCHAR(100) NOT NULL,
    description        VARCHAR(1000) NOT NULL,
    available       BOOLEAN NOT NULL,
    owner_id           BIGINT REFERENCES users (id) ON DELETE CASCADE,
    request_id         BIGINT REFERENCES requests (id) ON DELETE CASCADE,
    CONSTRAINT pk_item PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS bookings (
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date        TIMESTAMP,
    end_date          TIMESTAMP,
    item_id BIGINT    REFERENCES items (id) ON DELETE CASCADE,
    booker_id BIGINT  REFERENCES users (id) ON DELETE CASCADE,
    status            VARCHAR(255),
    CONSTRAINT pk_booking PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS comments (
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text              VARCHAR(1000) NOT NULL,
    item_id BIGINT    REFERENCES items (id) ON DELETE CASCADE,
    author_id         BIGINT REFERENCES users (id) ON DELETE CASCADE,
    created           TIMESTAMP,
    CONSTRAINT pk_comment PRIMARY KEY (id)
    );