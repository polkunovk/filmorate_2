package ru.yandex.practicum.filmorate.DbTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dal.GenreStorage;
import ru.yandex.practicum.filmorate.storage.dal.mappers.GenreRowMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreStorage.class, GenreRowMapper.class})
public class GenreStorageTest {
    private final GenreStorage genreStorage;

    @Test
    public void shouldFindGenreById() {
        Genre genre = genreStorage.findById(1).orElse(null);

        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(1);
    }

    @Test
    public void shouldFindAllGenres() {
        List<Genre> genres = genreStorage.findAll();
        Genre genre = genres.size() > 3 ? genres.get(3) : null;

        assertThat(genre).isNotNull();
        assertThat(genre.getId()).isEqualTo(4);
    }
}
