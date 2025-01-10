package ru.yandex.practicum.filmorate.DbTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
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
import java.util.List;

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
public class FilmGenreStorageTest {
    private final FilmGenreStorage genreStorage;
    private final FilmDbStorage dbStorage;

    @BeforeEach
    public void setUp() {
        Mpa mpa = new Mpa();
        mpa.setId(1);
        mpa.setName("G");

        Film film = new Film();
        film.setName("TestFilm");
        film.setDescription("TestDescription");
        film.setReleaseDate(LocalDate.now());
        film.setMpa(mpa);
        film.setDuration(100);
        dbStorage.addFilm(film);
    }

    @Test
    public void shouldAddFilmGenre() {
        FilmGenre genre = new FilmGenre();
        int filmId = dbStorage.findAll().get(0).getId();
        genre.setFilmId(filmId);
        genre.setGenreId(2);

        genreStorage.addGenre(genre);

        List<FilmGenre> genres = genreStorage.findAll();

        assertThat(genres)
                .hasSize(1)
                .extracting(FilmGenre::getFilmId)
                .contains(filmId);
    }

    @Test
    public void shouldFindAllFilmGenres() {
        FilmGenre genre = new FilmGenre();
        int filmId = dbStorage.findAll().get(0).getId();

        genre.setFilmId(filmId);
        genre.setGenreId(2);

        genreStorage.addGenre(genre);

        List<FilmGenre> genres = genreStorage.findAll();

        assertThat(genres)
                .hasSize(1)
                .extracting(FilmGenre::getGenreId)
                .contains(2);
    }

    @Test
    public void shouldFindGenresByFilmId() {
        FilmGenre genre = new FilmGenre();
        int filmId = dbStorage.findAll().get(0).getId();

        genre.setFilmId(filmId);
        genre.setGenreId(2);

        genreStorage.addGenre(genre);

        List<FilmGenre> foundGenres = genreStorage.findGenresByFilmId(filmId);

        assertThat(foundGenres)
                .hasSize(1)
                .extracting(FilmGenre::getGenreId)
                .contains(2);
    }
}
