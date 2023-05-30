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
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.*;
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
    private final FriendshipStorage friendshipStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final DirectorStorage directorStorage;
    private final ReviewStorage reviewStorage;
    private final ReviewLikesStorage reviewLikesStorage;
    private final FeedStorage feedStorage;

    private final FilmService filmService;
    private final UserService userService;

    User.UserBuilder userBuilder;
    Film.FilmBuilder filmBuilder;
    Genre.GenreBuilder genreBuilder;
    Mpa.MpaBuilder mpaBuilder;
    Director.DirectorBuilder directorBuilder;
    Review.ReviewBuilder reviewBuilder;

    private final LocalDate testReleaseDate = LocalDate.of(2000, 1, 1);

    private final JdbcTemplate jdbcTemplate;

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

        directorBuilder = Director.builder()
                .name("Director name");

        reviewBuilder = Review.builder()
                .content("Content")
                .isPositive(true)
                .userId(1L)
                .filmId(1L);
    }

    @AfterEach
    public void cleanDb() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate,
                "users", "films", "friendship", "film_genre", "likes",
                "directors", "film_director", "reviews", "review_likes", "feed");
        jdbcTemplate.update("ALTER TABLE USERS ALTER COLUMN user_id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE FILMS ALTER COLUMN film_id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE DIRECTORS ALTER COLUMN director_id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE REVIEWS ALTER COLUMN REVIEW_ID RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE FEED ALTER COLUMN ID_EVENT RESTART WITH 1");
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
    public void testDeleteUser() {
        boolean isDeleted;
        isDeleted = userStorage.deleteUser(0);
        assertFalse(isDeleted);

        User user = userBuilder.build();
        userStorage.addUser(user);
        isDeleted = userStorage.deleteUser(1);
        assertTrue(isDeleted);

        List<User> users = userStorage.listUsers();
        assertEquals(0, users.size());
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
    public void testDeleteFilm() {
        boolean isDeleted;
        isDeleted = filmStorage.deleteFilm(0);
        assertFalse(isDeleted);

        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        isDeleted = filmStorage.deleteFilm(1);
        assertTrue(isDeleted);

        List<Film> films = filmStorage.listFilms();
        assertEquals(0, films.size());
    }

//    @Test
//    public void testListTopFilms() {
//        List<Film> topFilms = filmStorage.listTopFilms(10);
//        assertThat(topFilms)
//                .isNotNull()
//                .isEqualTo(Collections.EMPTY_LIST);
//
//        filmStorage.addFilm(filmBuilder.build());
//        filmStorage.addFilm(filmBuilder.build());
//        userStorage.addUser(userBuilder.build());
//
//        topFilms = filmStorage.listTopFilms(1);
//        assertNotNull(topFilms);
//        assertEquals(topFilms.size(), 1);
//        assertEquals(topFilms.get(0).getId(), 1);
//
//        filmStorage.addMark(2, 1);
//        topFilms = filmStorage.listTopFilms(2);
//        assertNotNull(topFilms);
//        assertEquals(topFilms.size(), 2);
//        assertEquals(topFilms.get(0).getId(), 2);
//    }

    @Test
    public void testListTopFilmsByYear() {
        List<Film> topFilms = filmStorage.listTopFilmsByYear(2000);
        assertThat(topFilms)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);

        filmStorage.addFilm(filmBuilder.build());
        filmStorage.addFilm(filmBuilder.releaseDate(LocalDate.of(2001, 1, 1)).build());
        userStorage.addUser(userBuilder.build());

        topFilms = filmStorage.listTopFilmsByYear(2000);
        assertNotNull(topFilms);
        assertEquals(topFilms.size(), 1);
        assertEquals(topFilms.get(0).getId(), 1);

        Film updatedFilm = filmBuilder.id(2).releaseDate(testReleaseDate).build();
        filmStorage.updateFilm(updatedFilm);

        topFilms = filmStorage.listTopFilmsByYear(2000);
        assertNotNull(topFilms);
        assertEquals(topFilms.size(), 2);
        assertEquals(topFilms.get(0).getId(), 1);
    }

    @Test
    public void testAddGenreToFilm() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        filmStorage.addGenreToFilm(1, 1);

        List<Genre> genreId = genreStorage.getGenresByFilm(1);
        assertNotNull(genreId);
        assertEquals(genreId.size(), 1);
        assertEquals(genreId.get(0).getId(), 1);

        filmStorage.addGenreToFilm(1, 2);
        genreId = genreStorage.getGenresByFilm(1);
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

        List<Genre> genres = genreStorage.getGenresByFilm(1);
        assertNotNull(genres);
        assertEquals(genres.size(), 1);
        assertEquals(genres.get(0).getId(), 1);

        filmStorage.deleteGenreFromFilm(1, 1);

        genres = genreStorage.getGenresByFilm(1);
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

        List<Genre> genreId = genreStorage.getGenresByFilm(1);
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

        friendshipStorage.addFriend(1, 2);
        List<Long> friends = friendshipStorage.getFriendsByUser(1);
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

        List<Long> friends = friendshipStorage.getFriendsByUser(1);
        assertThat(friends)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);

        friendshipStorage.addFriend(1, 2);
        friends = friendshipStorage.getFriendsByUser(1);
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
        friendshipStorage.addFriend(1, 2);

        friendshipStorage.updateFriend(2, 1, true);
        List<Long> friends = friendshipStorage.getFriendsByUser(2);
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
        friendshipStorage.addFriend(1, 2);
        friendshipStorage.deleteFriend(2, 1);

        List<Long> friends = friendshipStorage.getFriendsByUser(1);
        assertThat(friends)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void testAddMark() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        User user = userBuilder.build();
        userStorage.addUser(user);

        filmStorage.addMark(1, 1, 6);
        Map<Long, Integer> marks = filmStorage.getMarksByFilm(1);
        assertNotNull(marks);
        assertEquals(marks.size(), 1);
        assertEquals(marks.get(1), 6);

        filmStorage.addFilm(film);
        filmStorage.addFilm(film);
        filmStorage.addMark(3, 1, 8);
        marks = filmStorage.getMarksByFilm(3);
        assertNotNull(marks);
        assertEquals(1, marks.size());
    }

    @Test
    public void testGetLikesByFilm() {
        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        Set<Long> marks = filmStorage.getMarksByFilm(1).keySet();
        assertThat(marks)
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
        filmStorage.addMark(1, 1, 6);
        filmStorage.addMark(1, 2,8);

        filmStorage.deleteMark(1, 2);
        List<Long> marks = (List<Long>) filmStorage.getMarksByFilm(1).keySet();
        assertNotNull(marks);
        assertEquals(marks.size(), 1);
        assertEquals(marks.get(0), 1);

        filmStorage.deleteMark(1, 1);
        marks = (List<Long>) filmStorage.getMarksByFilm(1).keySet();
        assertThat(marks)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    public void testGetMpas() {
        List<Mpa> mpas = mpaStorage.getMpas();
        assertNotNull(mpas);
        assertEquals(mpas.size(), 5);
        assertThat(mpas.get(0))
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "G");
    }

    @Test
    public void testFindMpaById() {
        Mpa mpa = mpaStorage.findMpaById(1);
        assertThat(mpa)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "G")
                .hasFieldOrPropertyWithValue("description", "У фильма нет возрастных ограничений");

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> mpaStorage.findMpaById(-1)
        );
        assertEquals("Mpa с id -1 не найден", ex.getMessage());

        ex = assertThrows(
                NotFoundException.class,
                () -> mpaStorage.findMpaById(999)
        );
        assertEquals("Mpa с id 999 не найден", ex.getMessage());
    }

    @Test
    public void testFindGenreById() {
        Genre genre = genreStorage.findGenreById(1);
        assertThat(genre)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "Комедия");

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> genreStorage.findGenreById(-1)
        );
        assertEquals("Жанр с id -1 не найден", ex.getMessage());

        ex = assertThrows(
                NotFoundException.class,
                () -> genreStorage.findGenreById(999)
        );
        assertEquals("Жанр с id 999 не найден", ex.getMessage());
    }

    @Test
    public void testGetGenres() {
        List<Genre> genres = genreStorage.getGenres();
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
        List<Genre> genres = genreStorage.getGenresByFilm(1);
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
        Map<Long, Set<Genre>> genresByFilmList = genreStorage.getGenresByFilmList(idList);
        assertThat(genresByFilmList)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_MAP);

        filmStorage.addFilm(film);
        filmStorage.addGenreToFilm(1L, 1);

        genresByFilmList = genreStorage.getGenresByFilmList(idList);
        assertNotNull(genresByFilmList);
        assertEquals(genresByFilmList.get(1L).size(), 1);

        filmStorage.addGenreToFilm(1L, 2);
        genresByFilmList = genreStorage.getGenresByFilmList(idList);
        assertNotNull(genresByFilmList);
        assertEquals(genresByFilmList.get(1L).size(), 2);

        filmStorage.addFilm(film);
        filmStorage.addGenreToFilm(2L, 1);
        idList.add(2L);
        genresByFilmList = genreStorage.getGenresByFilmList(idList);
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

    @Test
    public void testAddDirector() {
        Director director = directorBuilder.build();
        Director addedDirector = directorStorage.addDirector(director);
        assertThat(addedDirector)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    public void testFindDirectorById() {
        Director director = directorBuilder.build();
        Director addedDirector = directorStorage.addDirector(director);
        Director directorFound = directorStorage.findDirectorById(addedDirector.getId());
        assertThat(directorFound)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .isEqualTo(addedDirector);

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> directorStorage.findDirectorById(-1L)
        );
        assertEquals("Режиссёр с id -1 не найден.", ex.getMessage());

        ex = assertThrows(
                NotFoundException.class,
                () -> directorStorage.findDirectorById(999L)
        );
        assertEquals("Режиссёр с id 999 не найден.", ex.getMessage());
    }

    @Test
    public void testListDirectors() {
        List<Director> directors = directorStorage.listDirectors();
        assertThat(directors)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);

        Director director = directorBuilder.build();
        directorStorage.addDirector(director);
        directors = directorStorage.listDirectors();
        assertNotNull(directors);
        assertEquals(directors.size(), 1);
        assertEquals(directors.get(0).getId(), 1);
    }

    @Test
    public void testUpdateDirector() {
        Director director = directorBuilder.build();
        directorStorage.addDirector(director);
        Director directorToUpdate = directorBuilder.id(1L).name("Name Updated").build();
        Director directorUpdated = directorStorage.updateDirector(directorToUpdate);
        assertThat(directorUpdated)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Name Updated");

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> directorStorage.updateDirector(directorBuilder.id(-1L).build())
        );
        assertEquals("Режиссёр с id -1 не найден", ex.getMessage());

        ex = assertThrows(
                NotFoundException.class,
                () -> directorStorage.updateDirector(directorBuilder.id(999L).build())
        );
        assertEquals("Режиссёр с id 999 не найден", ex.getMessage());
    }

    @Test
    public void testAddDirectorToFilm() {
        Film film = filmBuilder.build();
        Film filmAdded = filmStorage.addFilm(film);

        Director director = directorBuilder.build();
        Director addedDirector = directorStorage.addDirector(director);

        filmStorage.addDirectorToFilm(filmAdded.getId(), addedDirector.getId());

        Film filmWithDirector = filmService.findFilmById(filmAdded.getId());
        Set<Director> directors = new HashSet<>();
        directors.add(addedDirector);

        assertThat(filmWithDirector)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrProperty("directors");

        assertEquals(directors.toString(), filmWithDirector.getDirectors().toString());
    }

    @Test
    public void testDeleteDirectorToFilm() {
        Film film = filmBuilder.build();
        Film filmAdded = filmStorage.addFilm(film);

        Director director = directorBuilder.build();
        Director addedDirector = directorStorage.addDirector(director);

        filmStorage.addDirectorToFilm(filmAdded.getId(), addedDirector.getId());

        directorStorage.deleteDirector(addedDirector.getId());
        Film filmWithoutDirector = filmStorage.findFilmById(filmAdded.getId());

        assertThat(filmWithoutDirector)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrProperty("directors");

        assertEquals(new ArrayList<>(), filmWithoutDirector.getDirectors());
    }

    @Test
    public void testFindFilmIdsByTitleQuery() {
        List<Long> filmIds = filmStorage.findFilmIdsByTitleQuery("a");
        assertNotNull(filmIds);
        assertEquals(0, filmIds.size());

        Film film = filmBuilder.build();
        filmStorage.addFilm(film);

        filmIds = filmStorage.findFilmIdsByTitleQuery("a");
        assertNotNull(filmIds);
        assertEquals(1, filmIds.size());

        filmIds = filmStorage.findFilmIdsByTitleQuery("AME");
        assertNotNull(filmIds);
        assertEquals(1, filmIds.size());
    }

    @Test
    public void testFindFilmIdsByDirectorQuery() {
        List<Long> filmIds = filmStorage.findFilmIdsByDirectorQuery("a");
        assertNotNull(filmIds);
        assertEquals(0, filmIds.size());

        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        Director director = directorBuilder.build();
        directorStorage.addDirector(director);
        filmStorage.addDirectorToFilm(1, 1);

        filmIds = filmStorage.findFilmIdsByDirectorQuery("a");
        assertNotNull(filmIds);
        assertEquals(1, filmIds.size());

        filmIds = filmStorage.findFilmIdsByDirectorQuery("AME");
        assertNotNull(filmIds);
        assertEquals(1, filmIds.size());
    }

    @Test
    public void testFindCommonFilmIds() {
        User user = userBuilder.build();
        userStorage.addUser(user);
        userStorage.addUser(user);

        List<Long> filmIds = filmStorage.findCommonFilmIds(1L, 2L);
        assertNotNull(filmIds);
        assertEquals(0, filmIds.size());

        Film film = filmBuilder.build();
        filmStorage.addFilm(film);
        filmStorage.addMark(1, 1, 5);

        filmIds = filmStorage.findCommonFilmIds(1L, 2L);
        assertNotNull(filmIds);
        assertEquals(0, filmIds.size());

        filmStorage.addMark(1, 2, 8);
        filmIds = filmStorage.findCommonFilmIds(1L, 2L);
        assertNotNull(filmIds);
        assertEquals(1, filmIds.size());
    }

    @Test
    public void testAddReview() {
        userStorage.addUser(userBuilder.build());
        filmStorage.addFilm(filmBuilder.build());
        Review review = reviewBuilder.build();
        review.setIsPositive(false);
        Review reviewAdded = reviewStorage.addReview(review);
        assertThat(reviewAdded)
                .isNotNull()
                .hasFieldOrPropertyWithValue("reviewId", 1L)
                .hasFieldOrPropertyWithValue("isPositive", false);
    }

    @Test
    public void testUpdateReview() {
        userStorage.addUser(userBuilder.build());
        filmStorage.addFilm(filmBuilder.build());
        Review review = reviewBuilder.build();
        review = reviewStorage.addReview(review);
        review.setIsPositive(false);
        Review reviewUpdated = reviewStorage.updateReview(review);
        assertThat(reviewUpdated)
                .isNotNull()
                .hasFieldOrPropertyWithValue("reviewId", 1L)
                .hasFieldOrPropertyWithValue("isPositive", false);

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> reviewStorage.updateReview(reviewBuilder.reviewId(-1L).build())
        );
        assertEquals("Отзыв с id -1 не найден", ex.getMessage());

        ex = assertThrows(
                NotFoundException.class,
                () -> reviewStorage.updateReview(reviewBuilder.reviewId(999L).build())
        );
        assertEquals("Отзыв с id 999 не найден", ex.getMessage());
    }

    @Test
    public void testFindReviewById() {
        userStorage.addUser(userBuilder.build());
        filmStorage.addFilm(filmBuilder.build());
        reviewStorage.addReview(reviewBuilder.build());

        Review reviewFound = reviewStorage.findReviewById(1);
        assertThat(reviewFound)
                .isNotNull()
                .hasFieldOrPropertyWithValue("reviewId", 1L)
                .hasFieldOrPropertyWithValue("isPositive", true);
    }

    @Test
    public void testDeleteReview() {
        userStorage.addUser(userBuilder.build());
        filmStorage.addFilm(filmBuilder.build());
        reviewStorage.addReview(reviewBuilder.build());
        reviewStorage.deleteReviewById(1L);

        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> reviewStorage.updateReview(reviewBuilder.reviewId(1L).build())
        );
        assertEquals("Отзыв с id 1 не найден", ex.getMessage());
    }

    @Test
    public void testFindTopReviews() {
        userStorage.addUser(userBuilder.build());
        filmStorage.addFilm(filmBuilder.build());

        List<Review> reviews = reviewStorage.findTopReviews(10);
        assertNotNull(reviews);
        assertEquals(0, reviews.size());

        reviewStorage.addReview(reviewBuilder.build());

        reviews = reviewStorage.findTopReviews(10);
        assertNotNull(reviews);
        assertEquals(1, reviews.size());

    }

    @Test
    public void testFindTopReviewsByFilmId() {
        userStorage.addUser(userBuilder.build());
        filmStorage.addFilm(filmBuilder.build());

        List<Review> reviews = reviewStorage.findTopReviewsByFilmId(1L, 10);
        assertNotNull(reviews);
        assertEquals(0, reviews.size());

        reviewStorage.addReview(reviewBuilder.build());

        reviews = reviewStorage.findTopReviewsByFilmId(1L, 10);
        assertNotNull(reviews);
        assertEquals(1, reviews.size());
    }

    @Test
    public void testAddLikeReview() {
        userStorage.addUser(userBuilder.build());
        filmStorage.addFilm(filmBuilder.build());
        reviewStorage.addReview(reviewBuilder.build());
        reviewLikesStorage.addDislikeReview(1L, 1L);
        reviewLikesStorage.addLikeReview(1L, 1L);
        Review review = reviewStorage.findReviewById(1L);
        assertEquals(1, review.getUseful());
    }

    @Test
    public void testAddDislikeReview() {
        userStorage.addUser(userBuilder.build());
        filmStorage.addFilm(filmBuilder.build());
        reviewStorage.addReview(reviewBuilder.build());
        reviewLikesStorage.addDislikeReview(1L, 1L);

        Review review = reviewStorage.findReviewById(1L);
        assertEquals(-1, review.getUseful());
    }

    @Test
    public void testDeleteLikeReview() {
        userStorage.addUser(userBuilder.build());
        filmStorage.addFilm(filmBuilder.build());
        reviewStorage.addReview(reviewBuilder.build());
        reviewLikesStorage.addLikeReview(1L, 1L);
        reviewLikesStorage.deleteLikeReview(1L, 1L);

        Review review = reviewStorage.findReviewById(1L);
        assertEquals(0, review.getUseful());
    }

    @Test
    public void testDeleteDislikeReview() {
        userStorage.addUser(userBuilder.build());
        filmStorage.addFilm(filmBuilder.build());
        reviewStorage.addReview(reviewBuilder.build());
        reviewLikesStorage.addDislikeReview(1L, 1L);
        reviewLikesStorage.deleteDislikeReview(1L, 1L);

        Review review = reviewStorage.findReviewById(1L);
        assertEquals(0, review.getUseful());
    }

    @Test
    public void testGetUsefulByReviewList() {
        userStorage.addUser(userBuilder.build());
        filmStorage.addFilm(filmBuilder.build());
        reviewStorage.addReview(reviewBuilder.build());

        Map<Long, Long> reviewsWithUseful = reviewLikesStorage.getUsefulByReviewList(List.of(1L));
        assertNotNull(reviewsWithUseful);
        assertEquals(0, reviewsWithUseful.size());

        reviewLikesStorage.addLikeReview(1L, 1L);
        reviewsWithUseful = reviewLikesStorage.getUsefulByReviewList(List.of(1L));
        assertNotNull(reviewsWithUseful);
        assertEquals(1, reviewsWithUseful.size());
        assertEquals(1, reviewsWithUseful.get(1L));
    }

    @Test
    public void testRecommendations() {
        List<Film> recommendations = userService.recommendations(1L);
        assertThat(recommendations)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);

        userStorage.addUser(userBuilder.build());
        userStorage.addUser(userBuilder.build());
        filmStorage.addFilm(filmBuilder.build());
        filmStorage.addFilm(filmBuilder.build());
        filmStorage.addFilm(filmBuilder.build());

        filmStorage.addMark(1L, 1L, 5);
        filmStorage.addMark(1L, 2L, 6);
        filmStorage.addMark(2L, 2L, 7);

        recommendations = userService.recommendations(1L);
        assertNotNull(recommendations);
        assertEquals(1, recommendations.size());
        assertEquals(2L, recommendations.get(0).getId());

        filmStorage.addFilm(filmBuilder.build());
        filmStorage.addMark(3L, 2L, 9);

        recommendations = userService.recommendations(1L);
        assertNotNull(recommendations);
        assertEquals(2, recommendations.size());
        assertEquals(3L, recommendations.get(1).getId());
    }

    @Test
    public void testGetUserIdsLikedFilmIds() {
        Map<Long, List<Long>> usersWithMarkFilmIds = filmStorage.getUserIdsMarkFilmIds();
        assertThat(usersWithMarkFilmIds)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_MAP);

        userStorage.addUser(userBuilder.build());
        userStorage.addUser(userBuilder.build());
        filmStorage.addFilm(filmBuilder.build());
        filmStorage.addFilm(filmBuilder.build());
        filmStorage.addFilm(filmBuilder.build());

        filmStorage.addMark(1L, 1L, 6);

        usersWithMarkFilmIds = filmStorage.getUserIdsMarkFilmIds();
        assertNotNull(usersWithMarkFilmIds);
        assertEquals(1, usersWithMarkFilmIds.size());

        filmStorage.addMark(1L, 2L, 7);
        usersWithMarkFilmIds = filmStorage.getUserIdsMarkFilmIds();
        assertNotNull(usersWithMarkFilmIds);
        assertEquals(2, usersWithMarkFilmIds.size());

        filmStorage.addMark(2L, 2L, 8);
        usersWithMarkFilmIds = filmStorage.getUserIdsMarkFilmIds();
        assertNotNull(usersWithMarkFilmIds);
        assertEquals(2, usersWithMarkFilmIds.size());
        assertEquals(2, usersWithMarkFilmIds.get(2L).size());
    }

    @Test
    public void testFeed() {
        userStorage.addUser(userBuilder.build());
        List<Feed> feed = feedStorage.findByUserId(1L);
        assertThat(feed)
                .isNotNull()
                .isEqualTo(Collections.EMPTY_LIST);

        filmStorage.addFilm(filmBuilder.build());
        filmService.addMark(1L, 1L, 9);
        feed = feedStorage.findByUserId(1L);
        assertEquals(1, feed.size());
    }
}