--данные в базе, их инициализация
INSERT INTO MPA ( NAME, DESCRIPTION )  VALUES ( 'G', 'У фильма нет возрастных ограничений');
INSERT INTO MPA ( NAME, DESCRIPTION )  VALUES ( 'PG', 'Детям рекомендуется смотреть фильм с родителями');
INSERT INTO MPA( NAME, DESCRIPTION )  VALUES ( 'PG-13', 'Детям до 13 лет просмотр не желателен');
INSERT INTO MPA( NAME, DESCRIPTION )  VALUES ( 'R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого');
INSERT INTO MPA ( NAME, DESCRIPTION )  VALUES ( 'NC-17', 'Лицам до 18 лет просмотр запрещён');

INSERT INTO GENRE ( NAME )  VALUES ('Комедия');
INSERT INTO GENRE ( NAME )  VALUES ('Драма');
INSERT INTO GENRE ( NAME )  VALUES ('Мультфильм');
INSERT INTO GENRE ( NAME )  VALUES ('Триллер');
INSERT INTO GENRE ( NAME )  VALUES ('Документальный');
INSERT INTO GENRE ( NAME )  VALUES ('Боевик');

/*MERGE INTO MPA
    KEY(MPA_ID)
    VALUES (1, 'G', 'У фильма нет возрастных ограничений'),
           (2, 'PG', 'Детям рекомендуется смотреть фильм с родителями'),
           (3, 'PG-13', 'Детям до 13 лет просмотр не желателен'),
           (4, 'R', 'Лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
           (5, 'NC-17', 'Лицам до 18 лет просмотр запрещён');

MERGE INTO GENRE
    KEY(GENRE_ID)
    VALUES (1, 'Драма'),
           (2, 'Мелодрама'),
           (3, 'Комедия'),
           (4, 'Фантастика'),
           (5, 'Ужасы');*/
