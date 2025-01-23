package ru.yandex.practicum.filmorate.storage.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dto.FilmDto;
import ru.yandex.practicum.filmorate.storage.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.storage.dto.UpdateFilmRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FilmMapper {

    public static Film mapToFilm(NewFilmRequest request) {
        return new Film() {{
            setName(request.getName());
            setDescription(request.getDescription());
            setDuration(request.getDuration());
            setMpa(request.getMpa());
            setReleaseDate(request.getReleaseDate());
            setGenres(request.getGenres());
        }};
    }

    public static FilmDto mapToFilmDto(Film film) {
        return new FilmDto() {{
            setId(film.getId());
            setName(film.getName());
            setDescription(film.getDescription());
            setDuration(film.getDuration());
            setMpa(film.getMpa());
            setReleaseDate(film.getReleaseDate());
            setLikesFromUsers(film.getLikesFromUsers());
            setGenres(film.getGenres());
        }};
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        if (request.hasName()) film.setName(request.getName());
        if (request.hasDescription()) film.setDescription(request.getDescription());
        if (request.hasDuration()) film.setDuration(request.getDuration());
        if (request.hasReleaseDate()) film.setReleaseDate(request.getReleaseDate());
        if (request.hasMpa()) film.setMpa(request.getMpa());
        if (request.hasGenres()) film.setGenres(request.getGenres());
        if (request.hasLikesFromUsers()) film.setLikesFromUsers(request.getLikesFromUsers());

        return film;
    }
}
