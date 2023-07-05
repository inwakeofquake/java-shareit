CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description TEXT,
    requestor_id BIGINT,
    created TIMESTAMP,
    FOREIGN KEY (requestor_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_available BOOLEAN,
    owner_id BIGINT,
    request_id BIGINT,
    FOREIGN KEY (owner_id) REFERENCES users (id),
    FOREIGN KEY (request_id) REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    item_id BIGINT,
    booker_id BIGINT,
    status VARCHAR(255),
    FOREIGN KEY (item_id) REFERENCES items (id),
    FOREIGN KEY (booker_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    text TEXT,
    item_id BIGINT,
    author_id BIGINT,
    created TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES items (id),
    FOREIGN KEY (author_id) REFERENCES users (id)
);