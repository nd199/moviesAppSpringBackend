CREATE SEQUENCE IF NOT EXISTS movie_id START WITH 1 INCREMENT BY 1;

CREATE TABLE movie
(
    movie_id INTEGER NOT NULL,
    name     TEXT    NOT NULL,
    cost     INTEGER NOT NULL,
    rating   INTEGER NOT NULL,
    CONSTRAINT pk_movie PRIMARY KEY (movie_id)
);

ALTER TABLE movie
    ADD CONSTRAINT movie_name_unique UNIQUE (name);
CREATE SEQUENCE IF NOT EXISTS customer_id START WITH 1 INCREMENT BY 1;

CREATE TABLE customer
(
    customer_id INTEGER      NOT NULL,
    name        TEXT,
    email       VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    phoneNumber       BIGINT       NOT NULL,
    CONSTRAINT pk_customer PRIMARY KEY (customer_id)
);

ALTER TABLE customer
    ADD CONSTRAINT email_id_unique UNIQUE (email);

ALTER TABLE customer
    ADD CONSTRAINT phone_number_unique UNIQUE (phoneNumber);