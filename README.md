# ER-диаграмма filmorate

<img alt = "ER-диаграмма" src = "src/main/resources/static/filmorateER.svg" width="675" height = "510">

[Ссылка на диаграмму](src/main/resources/static/filmorateER.svg)\
[Ссылка на диаграмму в редакторе диаграмм](https://app.quickdatabasediagrams.com/#/d/avNQfe)

#### Комментарии от проверявшего промежуточное ТЗ напарника:
**Freddycs14**\
Отличная работа! Молодец*\
[Ссылка на pull-реквест промежуточного ТЗ](https://github.com/MariiaTrofimova/java-filmorate/pull/6)\
<sub>*После этого немного корректировала, исходя из ТЗ11</sub>

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
WHERE film_id = ?;
```

#### Запрос топ-? фильмов
##### Вывод в порядке id
```
SELECT *
FROM films
WHERE film_id IN
    (SELECT film_id
     FROM likes
     GROUP BY film_id
     ORDER BY COUNT(user_id) DESC
     LIMIT ?);
```
##### Вывод в порядке убывания лайков (0 лайков тоже учитываются)
```
SELECT f.*
FROM films AS f
LEFT JOIN
  (SELECT film_id,
          COUNT(user_id) AS likes_qty
   FROM likes
   GROUP BY film_id
   ORDER BY likes_qty DESC
   LIMIT ?) AS top ON f.film_id = top.film_id
ORDER BY top.likes_qty DESC
LIMIT ?;
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
WHERE user_id = ?;
```

#### Запрос списка друзей для пользователя с id ?

```
SELECT *
FROM users
WHERE user_id IN
    (SELECT friend_id
     FROM friendship
     WHERE user_id = ?
       AND status = true
     UNION SELECT user_id
     FROM friendship
     WHERE friend_id = ?);
```

#### Запрос общих друзей для пользователей с id 1 и 2

```
SELECT *
FROM users
WHERE user_id IN (
                    (SELECT friend_id
                     FROM friendship
                     WHERE user_id = 1
                       AND status = true
                     UNION SELECT user_id
                     FROM friendship
                     WHERE friend_id = 1) INTERSECT
                    (SELECT friend_id
                     FROM friendship
                     WHERE user_id = 2
                       AND status = true
                     UNION SELECT user_id
                     FROM friendship
                     WHERE friend_id = 2));
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
* mpa_id — идентификатор возрастного рейтинга, внешний ключ, отсылает к таблице mpa

#### mpa

Содержит информацию о возрастном рейтинге MPAA\
**Поля:**

* первичный ключ mpa_id
* name — значение рейтинга: G, PG, PG-13, R, NC-17
* description — подробное описание:
  - G — у фильма нет возрастных ограничений,
  - PG — детям рекомендуется смотреть фильм с родителями,
  - PG-13 — детям до 13 лет просмотр не желателен,
  - R — лицам до 17 лет просматривать фильм можно только в присутствии взрослого,
  - NC-17 — лицам до 18 лет просмотр запрещён.

#### genre

Содержит информацию о жанрах кино.\
**Поля:**

* первичный ключ genre_id — идентификатор жанра;
* name — название жанра:
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
* внешний ключ genre_id (отсылает к таблице genre) — идентификатор жанра;
* первичный ключ: составной из двух id

#### likes

Содержит информацию о лайках фильмов из таблицы film.\
**Поля:**

* внешний ключ film_id (отсылает к таблице films) — идентификатор фильма;
* внешний ключ user_id (отсылает к таблице users) — id пользователя, поставившего лайк;
* первичный ключ: составной из двух id

#### friendship

Содержит информацию о друзьях в формате 1 — 2 — status\
При обработке запроса на добавление друга надо будет проверять существование пары в обратном порядке\
**Поля:**

* внешний ключ user_id (отсылает к таблице users) — идентификатор пользователя;
* внешний ключ friend_id (отсылает к таблице users) — идентификатор друга;
* status — статус дружбы: \
  false — неподтвержденная, true — подтвержденная
* первичный ключ: составной из двух id