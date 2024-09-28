package ru.yandex.practicum.filmorate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        Film createdFilm = filmService.addFilm(film);
        return new ResponseEntity<>(createdFilm, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        try {
            Film updatedFilm = filmService.updateFilm(film);
            return ResponseEntity.ok(updatedFilm);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Фильм с таким ID не найден.");
        }
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        List<Film> films = filmService.getAllFilms();
        return ResponseEntity.ok(films);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable int id) {
        Film film = filmService.getFilmById(id);
        if (film == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(film);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilm(@PathVariable int id) {
        filmService.deleteFilm(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<String> addLike(@PathVariable int id, @PathVariable Long userId) {
        log.info("Добавление лайка: фильм {} получает лайк от пользователя {}", id, userId);
        return filmService.addLike(id, userId); // Возвращаем ResponseEntity из сервиса
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<String> removeLike(@PathVariable int id, @PathVariable Long userId) {
        log.info("Удаление лайка: фильм {} удаляет лайк от пользователя {}", id, userId);
        return filmService.removeLike(id, userId); // Возвращаем ResponseEntity из сервиса
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        List<Film> films = filmService.getPopularFilms(count);
        return ResponseEntity.ok(films);
    }
}
