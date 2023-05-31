--данные в базе, их инициализация
/*INSERT INTO MPA (NAME, DESCRIPTION) VALUES ('G', 'У фильма нет возрастных ограничений');
INSERT INTO MPA (NAME, DESCRIPTION) VALUES ('PG', 'Детям рекомендуется смотреть фильм с родителями');
INSERT INTO MPA(NAME, DESCRIPTION) VALUES ('PG-13', 'Детям до 13 лет просмотр не желателен');
INSERT INTO MPA(NAME, DESCRIPTION) VALUES ('R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого');
INSERT INTO MPA (NAME, DESCRIPTION) VALUES ('NC-17', 'Лицам до 18 лет просмотр запрещён');

INSERT INTO GENRE (NAME) VALUES ('Комедия');
INSERT INTO GENRE (NAME) VALUES ('Драма');
INSERT INTO GENRE (NAME) VALUES ('Мультфильм');
INSERT INTO GENRE (NAME) VALUES ('Триллер');
INSERT INTO GENRE (NAME) VALUES ('Документальный');
INSERT INTO GENRE (NAME) VALUES ('Боевик');*/


delete
from USERS;
delete
from FILMS;
delete
from FILM_GENRE;
delete
from FRIENDSHIP;
delete
from MARKS;
delete
from DIRECTORS;
delete
from FILM_DIRECTOR;
delete
from REVIEWS;
delete
from REVIEW_LIKES;
delete
from FEED;


alter table USERS
    alter COLUMN user_id RESTART with 1;
alter table FILMS
    alter COLUMN film_id RESTART with 1;
alter table DIRECTORS
    alter COLUMN director_id RESTART with 1;
alter table REVIEWS
    alter COLUMN review_id RESTART with 1;
alter table FEED
    alter COLUMN id_event RESTART with 1;

merge into MPA
    KEY (MPA_ID)
    VALUES (1, 'G', 'У фильма нет возрастных ограничений'),
           (2, 'PG', 'Детям рекомендуется смотреть фильм с родителями'),
           (3, 'PG-13', 'Детям до 13 лет просмотр не желателен'),
           (4, 'R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
           (5, 'NC-17', 'Лицам до 18 лет просмотр запрещён');

merge into GENRE
    KEY (GENRE_ID)
    VALUES (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');
