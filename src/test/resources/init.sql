CREATE TABLE IF NOT EXISTS author (
   id SERIAL PRIMARY KEY,
   name VARCHAR ( 50 ) NOT NULL
);
TRUNCATE author CASCADE;

CREATE TABLE IF NOT EXISTS book (
   id SERIAL PRIMARY KEY,
   title VARCHAR ( 50 ) NOT NULL,
   author_id SERIAL REFERENCES author(id)
);
TRUNCATE book CASCADE;

INSERT INTO author (id, name)
VALUES (1001, 'Jim Morrison');

INSERT INTO book (id, title, author_id)
VALUES (1001, 'the doors 1', 1001);
INSERT INTO book (id, title, author_id)
VALUES (1002, 'the doors 2', 1001);