package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FriendshipDao;
import ru.yandex.practicum.filmorate.storage.GenreDao;
import ru.yandex.practicum.filmorate.storage.MpaDao;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final FriendshipDao friendshipDao;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;

    private final FilmService filmService;
    private final LocalDate testReleaseDate = LocalDate.of(2000, 1, 1);
    private final JdbcTemplate jdbcTemplate;
    User.UserBuilder userBuilder;
    Film.FilmBuilder filmBuilder;
    Genre.GenreBuilder genreBuilder;
    Mpa.MpaBuilder mpaBuilder;

    @BeforeEach
    public void setup() {
        userBuilder = User.builder()
                .email("e@mail.ru")
                .login("Login")
                .name("Name")
                .birthday(LocalDate.of(1985, 9, 7));

        mpaBuilder = Mpa.builder()
                .id(1);

        genreBuilder = Genre.builder()
                .id(1);

        filmBuilder = Film.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(testReleaseDate)
                .duration(90)
                .mpa(mpaBuilder.build());
    }

    @AfterEach
    public void cleanDb() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "users", "films", "friendship", "film_genre", "likes");
        jdbcTemplate.update("ALTER TABLE USERS ALTER COLUMN user_id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE FILMS ALTER COLUMN film_id RESTART WITH 1");
    }

    @Test
    public void testAddUser() {
        User user = userBuilder.build();
        User userAdded = userStorage.addUser(user);
        assertThat(userAdded)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testFindUserById() {
        User user = userBuilder.build();
        User userAdded = userStorage.addUser(user);
        User userFound = userStorage.findUserById(userAdded.getId());
        assertThat(userFound)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .isEqualTo(userAdded);

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> userStorage.findUserById(-1L)
        );
        assertEquals("Пользователь с id -1 не найден", ex.getMessage());

        ex = assertThrows(
                NotFoundException.class,
                () -> userStorage.findUserById(999L)
        );
        assertEquals("Пользователь с id 999 не найден", ex.getMessage());
    }

    @Test
    public void testListUsers() {
        List<User> users = userStorage.listUsers();
        assertThat(users)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);

        User user = userBuilder.build();
        userStorage.addUser(user);
        users = userStorage.listUsers();
        assertNotNull(users);
        assertEquals(users.size(), 1);
        assertEquals(users.get(0).getId(), 1);
    }

    @Test
    public void testUpdateUser() {
        User user = userBuilder.build();
        userStorage.addUser(user);
        User userToUpdate = userBuilder.id(1L).name("Name Updated").build();
        User userUpdated = userStorage.updateUser(userToUpdate);
        assertThat(userUpdated)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Name Updated");

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> userStorage.updateUser(userBuilder.id(-1L).build())
        );
        assertEquals("Пользователь с id -1 не найден", ex.getMessage());

        ex = assertThrows(
                NotFoundException.class,
                () -> userStorage.updateUser(userBuilder.id(999L).build())
        );
        assertEquals("Пользователь с id 999 не найден", ex.getMessage());
    }

    @Test
    public void testAddFilm() {
        Film film = filmBuilder.build();
        Film filmAdded = filmStorage.addFilm(film);
        assertThat(filmAdded)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testFindFilmById() {
        Film film = filmBuilder.build();
        Film filmAdded = filmStorage.addFilm(film);
        Film filmFound = filmStorage.findFilmById(filmAdded.getId());
        assertThat(filmFound)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("mpa", mpaBuilder.name("G").build());
        //.isEqualTo(filmAdded);

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> filmStorage.findFilmById(-1L)
        );
        assertEquals("Фильм с id -1 не найден", ex.getMessage());

        ex = assertThrows(
                NotFoundException.class,
                () -> filmStorage.findFilmById(999L)
        );
        assertEquals("Фильм с id 999 не найден", ex.getMessage());
    }

    @Test
    public void testListFilms() {
        List<Film> films = filmStorage.listFilms();
        assertThat(films)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);

        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        films = filmStorage.listFilms();
        assertNotNull(films);
        assertEquals(films.size(), 1);
        assertEquals(films.get(0).getId(), 1);
    }

    @Test
    public void testUpdateFilm() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        Film filmToUpdate = filmBuilder.id(1L).name("Film name Updated").build();
        Film filmUpdated = filmStorage.updateFilm(filmToUpdate);
        assertThat(filmUpdated)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Film name Updated");

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> filmStorage.updateFilm(filmBuilder.id(-1L).build())
        );
        assertEquals("Фильм с id -1 не найден", ex.getMessage());

        ex = assertThrows(
                NotFoundException.class,
                () -> filmStorage.updateFilm(filmBuilder.id(999L).build())
        );
        assertEquals("Фильм с id 999 не найден", ex.getMessage());
    }

    @Test
    public void testListTopFilms() {
        List<Film> topFilms = filmStorage.listTopFilms(10);
        assertThat(topFilms)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);

        filmStorage.addFilm(filmBuilder.build());
        filmStorage.addFilm(filmBuilder.build());
        userStorage.addUser(userBuilder.build());

        topFilms = filmStorage.listTopFilms(1);
        assertNotNull(topFilms);
        assertEquals(topFilms.size(), 1);
        assertEquals(topFilms.get(0).getId(), 1);

        filmStorage.addLike(2, 1);
        topFilms = filmStorage.listTopFilms(2);
        assertNotNull(topFilms);
        assertEquals(topFilms.size(), 2);
        assertEquals(topFilms.get(0).getId(), 2);
    }

    @Test
    public void testAddGenreToFilm() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        filmStorage.addGenreToFilm(1, 1);

        List<Genre> genreId = genreDao.getGenresByFilm(1);
        assertNotNull(genreId);
        assertEquals(genreId.size(), 1);
        assertEquals(genreId.get(0).getId(), 1);

        filmStorage.addGenreToFilm(1, 2);
        genreId = genreDao.getGenresByFilm(1);
        assertNotNull(genreId);
        assertEquals(genreId.size(), 2);
        assertEquals(genreId.get(0).getId(), 1);
    }

    @Test
    public void testDeleteGenreFromFilm() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        filmStorage.addGenreToFilm(1, 1);
        filmStorage.addGenreToFilm(1, 2);

        filmStorage.deleteGenreFromFilm(1, 2);

        List<Genre> genres = genreDao.getGenresByFilm(1);
        assertNotNull(genres);
        assertEquals(genres.size(), 1);
        assertEquals(genres.get(0).getId(), 1);

        filmStorage.deleteGenreFromFilm(1, 1);

        genres = genreDao.getGenresByFilm(1);
        assertThat(genres)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void testClearGenresFromFilm() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        filmStorage.addGenreToFilm(1, 1);
        filmStorage.addGenreToFilm(1, 2);

        filmStorage.clearGenresFromFilm(1);

        List<Genre> genreId = genreDao.getGenresByFilm(1);
        assertThat(genreId)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void testAddFriend() {
        User user = userBuilder.build();
        userStorage.addUser(user);
        User friend = userBuilder.name("friend").build();
        userStorage.addUser(friend);

        friendshipDao.addFriend(1, 2);
        List<Long> friends = friendshipDao.getFriendsByUser(1);
        assertNotNull(friends);
        assertEquals(friends.size(), 1);
        assertEquals(friends.get(0), 2);
    }

    @Test
    public void testGetFriendsByUser() {
        User user = userBuilder.build();
        userStorage.addUser(user);
        User friend = userBuilder.name("friend").build();
        userStorage.addUser(friend);

        List<Long> friends = friendshipDao.getFriendsByUser(1);
        assertThat(friends)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);

        friendshipDao.addFriend(1, 2);
        friends = friendshipDao.getFriendsByUser(1);
        assertNotNull(friends);
        assertEquals(friends.size(), 1);
        assertEquals(friends.get(0), 2);
    }

    @Test
    public void testUpdateFriend() {
        User user = userBuilder.build();
        userStorage.addUser(user);
        User friend = userBuilder.name("friend").build();
        userStorage.addUser(friend);
        friendshipDao.addFriend(1, 2);

        friendshipDao.updateFriend(2, 1, true);
        List<Long> friends = friendshipDao.getFriendsByUser(2);
        assertNotNull(friends);
        assertEquals(friends.size(), 1);
        assertEquals(friends.get(0), 1);
    }

    @Test
    public void testDeleteFriend() {
        User user = userBuilder.build();
        userStorage.addUser(user);
        User friend = userBuilder.name("friend").build();
        userStorage.addUser(friend);
        friendshipDao.addFriend(1, 2);
        friendshipDao.deleteFriend(2, 1);

        List<Long> friends = friendshipDao.getFriendsByUser(1);
        assertThat(friends)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void testAddLike() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        User user = userBuilder.build();
        userStorage.addUser(user);

        filmStorage.addLike(1, 1);
        List<Long> likes = filmStorage.getLikesByFilm(1);
        assertNotNull(likes);
        assertEquals(likes.size(), 1);
        assertEquals(likes.get(0), 1);
    }

    @Test
    public void testGetLikesByFilm() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        List<Long> likes = filmStorage.getLikesByFilm(1);
        assertThat(likes)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void testDeleteLike() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        User user = userBuilder.build();
        userStorage.addUser(user);
        userStorage.addUser(user);
        filmStorage.addLike(1, 1);
        filmStorage.addLike(1, 2);

        filmStorage.deleteLike(1, 2);
        List<Long> likes = filmStorage.getLikesByFilm(1);
        assertNotNull(likes);
        assertEquals(likes.size(), 1);
        assertEquals(likes.get(0), 1);

        filmStorage.deleteLike(1, 1);
        likes = filmStorage.getLikesByFilm(1);
        assertThat(likes)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void testGetMpas() {
        List<Mpa> mpas = mpaDao.getMpas();
        assertNotNull(mpas);
        assertEquals(mpas.size(), 5);
        assertThat(mpas.get(0))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    public void testFindMpaById() {
        Mpa mpa = mpaDao.findMpaById(1);
        assertThat(mpa)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "G")
                .hasFieldOrPropertyWithValue("description", "У фильма нет возрастных ограничений");

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> mpaDao.findMpaById(-1)
        );
        assertEquals("Mpa с id -1 не найден", ex.getMessage());

        ex = assertThrows(
                NotFoundException.class,
                () -> mpaDao.findMpaById(999)
        );
        assertEquals("Mpa с id 999 не найден", ex.getMessage());
    }

    @Test
    public void testFindGenreById() {
        Genre genre = genreDao.findGenreById(1);
        assertThat(genre)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "Комедия");

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> genreDao.findGenreById(-1)
        );
        assertEquals("Жанр с id -1 не найден", ex.getMessage());

        ex = assertThrows(
                NotFoundException.class,
                () -> genreDao.findGenreById(999)
        );
        assertEquals("Жанр с id 999 не найден", ex.getMessage());
    }

    @Test
    public void testGetGenres() {
        List<Genre> genres = genreDao.getGenres();
        assertNotNull(genres);
        assertEquals(genres.size(), 6);
        assertThat(genres.get(0))
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    public void testGetGenresByFilm() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        List<Genre> genres = genreDao.getGenresByFilm(1);
        assertThat(genres)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void testGetGenresByFilmList() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        List<Long> idList = new ArrayList<>();
        idList.add(1L);
        Map<Long, Set<Genre>> genresByFilmList = genreDao.getGenresByFilmList(idList);
        assertThat(genresByFilmList)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_MAP);

        filmStorage.addFilm(film);
        filmStorage.addGenreToFilm(1L, 1);

        genresByFilmList = genreDao.getGenresByFilmList(idList);
        assertNotNull(genresByFilmList);
        assertEquals(genresByFilmList.get(1L).size(), 1);

        filmStorage.addGenreToFilm(1L, 2);
        genresByFilmList = genreDao.getGenresByFilmList(idList);
        assertNotNull(genresByFilmList);
        assertEquals(genresByFilmList.get(1L).size(), 2);

        filmStorage.addFilm(film);
        filmStorage.addGenreToFilm(2L, 1);
        idList.add(2L);
        genresByFilmList = genreDao.getGenresByFilmList(idList);
        assertNotNull(genresByFilmList);
        assertEquals(genresByFilmList.get(2L).size(), 1);
    }

    @Test
    public void testServiceGetAllFilms() {
        Film film = filmBuilder.build();

        List<Film> films = filmService.listFilms();
        assertThat(films)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);

        filmService.addFilm(film);
        films = filmService.listFilms();
        assertNotNull(films);
        assertEquals(films.size(), 1);

        filmStorage.addGenreToFilm(1L, 1);
        films = filmService.listFilms();
        assertNotNull(films);
        assertEquals(films.size(), 1);
        assertEquals(films.get(0).getGenres().get(0).getName(), "Комедия");

        filmStorage.addGenreToFilm(1L, 2);
        films = filmService.listFilms();
        assertNotNull(films);
        assertEquals(films.size(), 1);
        assertEquals(films.get(0).getGenres().get(0).getName(), "Комедия");

        filmService.addFilm(film);
        films = filmService.listFilms();
        assertNotNull(films);
        assertEquals(films.size(), 2);
        assertEquals(films.get(0).getGenres().get(0).getName(), "Комедия");
    }
}