--схема будет создаваться заново при каждом запуске приложения: создание таблиц
--избежать ошибок, связанных с многократным применением скрипта к БД — IF NOT EXISTS при создании таблиц и индексов.
create table IF NOT EXISTS USERS
(
    USER_ID  INTEGER auto_increment
        primary key,
    EMAIL    CHARACTER VARYING(255) not null,
    LOGIN    CHARACTER VARYING(50)  not null,
    NAME     CHARACTER VARYING(50)  not null,
    BIRTHDAY DATE                   not null
);

create table IF NOT EXISTS FRIENDSHIP
(
    USER_ID   INTEGER not null
        references USERS (USER_ID) ON DELETE CASCADE,
    FRIEND_ID INTEGER not null
        references USERS (USER_ID) ON DELETE CASCADE,
    STATUS    BOOLEAN not null,
    primary key (USER_ID, FRIEND_ID)
);

create table IF NOT EXISTS MPA
(
    MPA_ID      INTEGER auto_increment
        primary key,
    NAME        CHARACTER VARYING(50) not null UNIQUE,
    DESCRIPTION CHARACTER VARYING(100)
);

create table IF NOT EXISTS GENRE
(
    GENRE_ID INTEGER auto_increment
        primary key,
    NAME     CHARACTER VARYING(50) UNIQUE
);

create table IF NOT EXISTS FILMS
(
    FILM_ID      INTEGER auto_increment
        primary key,
    NAME         CHARACTER VARYING(150) not null,
    DESCRIPTION  CHARACTER VARYING(200) not null,
    RELEASE_DATE DATE                   not null,
    DURATION     INTEGER                not null,
    MPA_ID       INTEGER                not null
        references MPA (MPA_ID) ON DELETE RESTRICT
);

create table IF NOT EXISTS FILM_GENRE
(
    FILM_ID  INTEGER not null
        references FILMS ON DELETE CASCADE,
    GENRE_ID INTEGER not null
        references GENRE ON DELETE CASCADE,
    primary key (FILM_ID, GENRE_ID)
);

create table IF NOT EXISTS LIKES
(
    FILM_ID INTEGER not null
        references FILMS ON DELETE CASCADE,
    USER_ID INTEGER not null
        references USERS ON DELETE CASCADE,
    primary key (FILM_ID, USER_ID)
);