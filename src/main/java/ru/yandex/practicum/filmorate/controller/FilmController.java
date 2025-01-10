package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.dto.UpdateFilmRequest;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService service;

    @GetMapping
    public List<FilmDto> getAllFilms() {
        return service.getFilms();
    }

    @GetMapping("/{filmId}")
    public FilmDto getFilm(@PathVariable Integer filmId) {
        return service.getFilmById(filmId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto addNewFilm(@Valid @RequestBody NewFilmRequest filmRequest) {
        return service.addFilm(filmRequest);
    }

    @PutMapping
    public FilmDto updateExistingFilm(@Valid @RequestBody UpdateFilmRequest updatedFilm) {
        return service.updateFilm(updatedFilm);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public FilmDto addLikeToFilm(@PathVariable Integer filmId, @PathVariable Integer userId) {
        return service.likeFilm(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public FilmDto removeLikeFromFilm(@PathVariable Integer filmId, @PathVariable Integer userId) {
        return service.deleteLikeFromFilm(filmId, userId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getMostPopularFilms(@RequestParam(defaultValue = "10") int limit) {
        return service.getPopularFilms(limit);
    }
}
