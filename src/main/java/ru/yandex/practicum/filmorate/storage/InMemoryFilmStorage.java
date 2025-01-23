package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.IdGenerator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage extends IdGenerator implements FilmStorage {
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final Map<Integer, Film> films = new HashMap<>();

    public Map<Integer, Film> getFilmsMap() {
        return films;
    }

    public Collection<Film> getFilms() {
        log.info("Получен список фильмов.");
        return films.values();
    }

    public Film addFilm(Film film) {
        validateFilm(film);

        if (film.getId() == null) {
            film.setId(getNextId(films));
        }

        films.put(film.getId(), film);
        log.info("Добавлен новый фильм {} с id: {}", film.getName(), film.getId());

        return film;
    }

    public Film updateFilm(Film updatedFilm) {
        if (updatedFilm.getId() == null) {
            log.error("id должен быть указан.");
            throw new ValidationException("id должен быть указан.");
        }

        Film existingFilm = films.get(updatedFilm.getId());
        if (existingFilm != null) {
            validateFilm(updatedFilm);
            existingFilm = updateExistingFilm(existingFilm, updatedFilm);
            log.info("Фильм {} был обновлен.", existingFilm.getName());
            return existingFilm;
        }

        log.error("Фильм с id = {} не найден", updatedFilm.getId());
        throw new NotFoundException("Фильм с id = " + updatedFilm.getId() + " не найден");
    }

    private void validateFilm(Film film) {
        films.values().stream()
                .filter(f -> f.getName().equals(film.getName()))
                .findFirst()
                .ifPresent(f -> {
                    throw new ValidationException("Фильм с таким названием уже есть в списке.");
                });

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
    }

    private Film updateExistingFilm(Film oldFilm, Film newFilm) {
        if (newFilm.getDescription() != null) {
            oldFilm.setDescription(newFilm.getDescription());
        }
        oldFilm.setName(newFilm.getName());
        oldFilm.setReleaseDate(newFilm.getReleaseDate());
        oldFilm.setDuration(newFilm.getDuration());

        return oldFilm;
    }
}
