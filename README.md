# ER-диаграмма filmorate

<img alt = "https://github.com/MariiaTrofimova/filmorate-ER/blob/f4b4e553f6c7f8e7f4b04efebd4bcccd37b811ac/src/resource/filmorateER.svg" src = "https://github.com/MariiaTrofimova/filmorate-ER/blob/f4b4e553f6c7f8e7f4b04efebd4bcccd37b811ac/src/resource/filmorateER.svg" width="720" height = "520">

[Ссылка на диаграмму](https://github.com/MariiaTrofimova/filmorate-ER/blob/f4b4e553f6c7f8e7f4b04efebd4bcccd37b811ac/src/resource/filmorateER.svg)\
[Ссылка на диаграмму в редакторе диаграмм](https://app.quickdatabasediagrams.com/#/d/avNQfe)

## Примеры запросов

### Film

#### Запрос списка фильмов

```
SELECT *
FROM film;
```

#### Запрос фильма по id

```
SELECT *
FROM film
WHERE film_id = 1;
```

#### Запрос топ-10 фильмов

```
SELECT *
FROM film
WHERE film_id IN
    (SELECT film_id
     FROM likes
     GROUP BY film_id
     ORDER BY COUNT(user_id) DESC
     LIMIT 10);
```

### User

#### Запрос списка пользователей

```
SELECT *
FROM user;
```

#### Запрос пользователя по id

```
SELECT *
FROM user
WHERE user_id = 1;
```

#### Запрос общих друзей

```
SELECT *
FROM user
WHERE user_id IN
    (SELECT friend_id
     FROM friends
     WHERE user_id = 2
       AND status = '1'
       AND friend_id IN
         (SELECT friend_id
          FROM friends
          WHERE user_id = 1
            AND status = '1'));
```

## Описание БД

#### user

Содержит данные о пользователях.\
**Поля:**

* первичный ключ user_id — идентификатор пользователя;
* email — электронная почта;
* login — логин пользователя;
* name — имя пользователя;
* birthday — дата рождения

#### film

Содержит информацию о фильмах.\
**Поля:**

* первичный ключ film_id — идентификатор фильма;
* name — название фильма;
* description — описание фильма;
* release_date — дата выхода;
* duration — длительность фильма;
* rating_id — идентификатор возрастного рейтинга

#### rating

Содержит информацию о возрастном рейтинге MPAA\
**Поля:**

* первичный ключ rating_id
* name — значение рейтинга:
* description — подробное описание, например:
    - G — у фильма нет возрастных ограничений,
    - PG — детям рекомендуется смотреть фильм с родителями,
    - PG-13 — детям до 13 лет просмотр не желателен,
    - R — лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
    - NC-17 — лицам до 18 лет просмотр запрещён.

#### film_genre

Содержит информацию о жанрах фильмов из таблицы film.\
**Поля:**

* первичный ключ film_id — идентификатор фильма;
* внешний ключ category_id (отсылает к таблице genre) — идентификатор жанра;

#### genre

Содержит информацию о жанрах кино.\
**Поля:**

* первичный ключ genre_id — идентификатор жанра;
* name — название жанра, например:
    - Комедия.
    - Драма.
    - Мультфильм.
    - Триллер.
    - Документальный.
    - Боевик.

#### likes

Содержит информацию о лайках фильмов из таблицы film.\
**Поля:**

* первичный ключ film_id — идентификатор фильма;
* внешний ключ user_id (отсылает к таблице user) — id пользователя, поставившего лайк;

#### friends

Содержит информацию о друзьях\
**Поля:**

* первичный ключ user_id — идентификатор пользователя;
* внешний ключ friend_id
* status — статус дружбы, например: \
  ‘0’ — неподтвержденная, ‘1’ — подтвержденная