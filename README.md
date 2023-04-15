# ER-диаграмма filmorate

<img src = "https://github.com/MariiaTrofimova/filmorate-ER/blob/303445c5d288278efc6922e1e4eba9bf581cde8d/src/resource/filmorateER.svg" width="720" height = "520">

[Ссылка на диаграмму](https://github.com/MariiaTrofimova/filmorate-ER/blob/303445c5d288278efc6922e1e4eba9bf581cde8d/src/resource/filmorateER.svg)\
[Ссылка на диаграмму в редакторе диаграмм](https://app.quickdatabasediagrams.com/#/d/avNQfe)

## Примеры запросов

### Film

#### Запрос списка фильмов

```
SELECT *
FROM films;
```

#### Запрос фильма по id

```
SELECT *
FROM films
WHERE film_id = 1;
```

#### Запрос топ-10 фильмов

```
SELECT *
FROM films
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
FROM users;
```

#### Запрос пользователя по id

```
SELECT *
FROM users
WHERE user_id = 1;
```

#### Запрос списка друзей для пользователя с id 1

```
SELECT *
FROM users
WHERE user_id IN
    (SELECT friend_id
     FROM friendship
     WHERE user_id = 1
     UNION SELECT user_id
     FROM friendship
     WHERE friend_id = 1);
```

#### Запрос общих друзей для пользователей с id 1 и 2

```
SELECT *
FROM users
WHERE user_id IN
    (SELECT friend_id
     FROM friendship
     WHERE (user_id = 1
            OR user_id = 2)
       AND status = '1'
       AND friend_id NOT IN (1,
                             2)
     UNION SELECT user_id
     FROM friendship
     WHERE (friend_id = 1
            OR friend_id = 2)
       AND status = '1'
       AND user_id NOT IN (1,
                           2));
```

## Описание БД

#### users

Содержит данные о пользователях.\
**Поля:**

* первичный ключ user_id — идентификатор пользователя;
* email — электронная почта;
* login — логин пользователя;
* name — имя пользователя;
* birthday — дата рождения

#### films

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

#### film_genre

Содержит информацию о жанрах фильмов из таблицы film.\
**Поля:**

* внешний ключ film_id (отсылает к таблице films) — идентификатор фильма;
* внешний ключ category_id (отсылает к таблице genre) — идентификатор жанра;

#### likes

Содержит информацию о лайках фильмов из таблицы film.\
**Поля:**

* внешний ключ film_id (отсылает к таблице films) — идентификатор фильма;
* внешний ключ user_id (отсылает к таблице users) — id пользователя, поставившего лайк;

#### friendship

Содержит информацию о друзьях в формате 1 — 2 — status\
При обработке запроса на добавление друга надо будет проверять существование пары в обратном порядке\
**Поля:**

* внешний ключ user_id (отсылает к таблице users) — идентификатор пользователя;
* внешний ключ friend_id (отсылает к таблице users) — идентификатор друга;
* status — статус дружбы, например: \
  ‘0’ — неподтвержденная, ‘1’ — подтвержденная