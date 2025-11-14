CREATE TABLE post (
    id SERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    link TEXT,
    description TEXT,
    time BIGINT
);