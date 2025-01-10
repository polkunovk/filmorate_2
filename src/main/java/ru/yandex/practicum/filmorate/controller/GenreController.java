package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/genres")
public class GenreController {

    private final GenreService service;

    @GetMapping
    public List<Genre> fetchAllGenres() {
        return service.findAll();
    }

    @GetMapping("/{genreId}")
    public Genre fetchGenreById(@PathVariable("genreId") Integer genreId) {
        return service.findById(genreId);
    }
}
