package ru.yandex.practicum.filmorate.DbTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dal.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.LikesFromUsersStorage;
import ru.yandex.practicum.filmorate.storage.dal.MpaStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmGenreRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.LikesFromUsersRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.MpaRowMapper;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        FilmDbStorage.class,
        FilmRowMapper.class,
        FilmGenreStorage.class,
        FilmGenreRowMapper.class,
        GenreStorage.class,
        GenreRowMapper.class,
        LikesFromUsersStorage.class,
        LikesFromUsersRowMapper.class,
        MpaStorage.class,
        MpaRowMapper.class,
})

class FilmDbTest {
    private final FilmDbStorage filmDbStorage;

    @BeforeEach
    void setUp() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        Film film = new Film();
        film.setName("TestFilm");
        film.setDescription("TestDescription");
        film.setReleaseDate(LocalDate.now());
        film.setMpa(mpa);
        film.setDuration(100);

        filmDbStorage.addFilm(film);
    }

    @Test
    void shouldFindFilmById() {
        int filmId = filmDbStorage.findAll().get(0).getId();
        Optional<Film> foundFilm = filmDbStorage.findById(filmId);

        assertThat(foundFilm)
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("id", filmId));
    }

    @Test
    void shouldFindFilmByName() {
        Optional<Film> foundFilm = filmDbStorage.findByName("TestFilm");

        assertThat(foundFilm)
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("name", "TestFilm"));
    }

    @Test
    void shouldFindAllFilms() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        Film film2 = new Film();
        film2.setName("TestFilm2");
        film2.setDescription("TestDescription2");
        film2.setReleaseDate(LocalDate.now());
        film2.setMpa(mpa);
        film2.setDuration(102);

        filmDbStorage.addFilm(film2);

        assertThat(filmDbStorage.findAll())
                .hasSize(2)
                .extracting(Film::getName)
                .contains("TestFilm", "TestFilm2");
    }

    @Test
    void shouldAddFilm() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        Film film = new Film();
        film.setName("TestFilm1");
        film.setDescription("TestDescription1");
        film.setReleaseDate(LocalDate.now());
        film.setMpa(mpa);
        film.setDuration(100);

        filmDbStorage.addFilm(film);

        Optional<Film> foundFilm = filmDbStorage.findByName("TestFilm1");

        assertThat(foundFilm)
                .isPresent()
                .hasValueSatisfying(f -> assertThat(f).hasFieldOrPropertyWithValue("name", "TestFilm1"));
    }

    @Test
    void shouldUpdateFilm() {
        Film filmToUpdate = filmDbStorage.findAll().get(0);
        filmToUpdate.setName("UpdatedName");

        filmDbStorage.addFilm(filmToUpdate);

        Optional<Film> updatedFilm = filmDbStorage.findById(filmToUpdate.getId());

        assertThat(updatedFilm)
                .isPresent()
                .hasValueSatisfying(film -> assertThat(film).hasFieldOrPropertyWithValue("name", "UpdatedName"));
    }
}
