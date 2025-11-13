create table post
(
    id       serial primary key,
    name     varchar(100),
    text     varchar(1000),
    link     varchar(100) unique not null,
    created   date
);