CREATE SEQUENCE IF NOT EXISTS customer_id START WITH 1 INCREMENT BY 1;

CREATE TABLE customer
(
    customer_id  BIGINT NOT NULL,
    name         TEXT,
    email        TEXT   NOT NULL,
    password     TEXT   NOT NULL,
    phone_number BIGINT NOT NULL,
    CONSTRAINT pk_customer PRIMARY KEY (customer_id)
);

ALTER TABLE customer
    ADD CONSTRAINT email_id_unique UNIQUE (email);

ALTER TABLE customer
    ADD CONSTRAINT phone_number_unique UNIQUE (phone_number);

CREATE SEQUENCE IF NOT EXISTS movie_id START WITH 1 INCREMENT BY 1;

CREATE TABLE movie
(
    movie_id BIGINT           NOT NULL,
    name     TEXT             NOT NULL,
    cost     DOUBLE PRECISION NOT NULL,
    rating   DOUBLE PRECISION NOT NULL,
    CONSTRAINT pk_movie PRIMARY KEY (movie_id)
);

ALTER TABLE movie
    ADD CONSTRAINT movie_name_unique UNIQUE (name);