package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

@Component
public class FilmMapper {

    private final JdbcTemplate jdbcTemplate;

    public FilmMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final RowMapper<Film> FILM_ROW_MAPPER = (rs, rowNum) -> {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        film.setGenres(fetchGenresByFilmId(rs.getInt("id")));

        return film;
    };

    public void addFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            int filmId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
            insertGenres(filmId, film.getGenres());
        }
    }

    public void updateFilm(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());

        deleteGenresByFilmId(film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            insertGenres(film.getId(), film.getGenres());
        }
    }

    public void deleteFilm(int id) {
        deleteGenresByFilmId(id);
        jdbcTemplate.update("DELETE FROM films WHERE id = ?", id);
    }

    public Film getFilmById(int id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        List<Film> films = jdbcTemplate.query(sql, FILM_ROW_MAPPER, id);
        return films.isEmpty() ? null : films.get(0);
    }

    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, FILM_ROW_MAPPER);
    }

    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, COUNT(l.user_id) as likes_count FROM films f " +
                "LEFT JOIN likes l ON f.id = l.film_id " +
                "GROUP BY f.id ORDER BY likes_count DESC LIMIT ?";
        return jdbcTemplate.query(sql, FILM_ROW_MAPPER, count);
    }

    private void insertGenres(int filmId, Set<String> genres) {
        String sql = "INSERT INTO film_genres (film_id, genre) VALUES (?, ?)";
        for (String genre : genres) {
            jdbcTemplate.update(sql, filmId, genre);
        }
    }

    private void deleteGenresByFilmId(int filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    private static Set<String> fetchGenresByFilmId(int filmId) {
        return Set.of();
    }
}
