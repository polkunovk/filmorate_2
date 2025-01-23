package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService service;

    @GetMapping
    public List<Mpa> getAllMpaRatings() {
        return service.findAll();
    }

    @GetMapping("/{ratingId}")
    public Mpa getMpaRatingById(@PathVariable("ratingId") Integer ratingId) {
        return service.findById(ratingId);
    }
}
