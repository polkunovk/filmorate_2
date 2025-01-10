package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.LikesFromUsers;
import ru.yandex.practicum.filmorate.storage.dal.*;
import ru.yandex.practicum.filmorate.storage.dto.*;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final LikesFromUsersStorage likesFromUsersStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public FilmDto addFilm(NewFilmRequest request) {
        validateFilmRequest(request);

        String mpaName = mpaStorage.findById(request.getMpa().getId()).get().getName();
        request.getMpa().setName(mpaName);

        if (request.getGenres() != null) {
            processGenres(request);
        }

        Film film = FilmMapper.mapToFilm(request);
        film = filmDbStorage.addFilm(film);

        if (film.getGenres() != null) {
            addFilmGenres(film);
        }

        log.info("Добавлен новый фильм {} с id: {}", film.getName(), film.getId());
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto updateFilm(UpdateFilmRequest request) {
        Film film = filmDbStorage.findById(request.getId()).orElseThrow(() -> new NotFoundException("Фильм не найден"));
        validateReleaseDate(request.getReleaseDate());

        Film updatedFilm = FilmMapper.updateFilmFields(film, request);
        updatedFilm = filmDbStorage.updateFilm(updatedFilm);

        log.info("Фильм {} был обновлен.", updatedFilm.getName());
        return FilmMapper.mapToFilmDto(updatedFilm);
    }

    public List<FilmDto> getFilms() {
        log.info("Получен список фильмов.");
        return filmDbStorage.findAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto getFilmById(Integer filmId) {
        return FilmMapper.mapToFilmDto(filmDbStorage.findById(filmId).orElseThrow(() -> new NotFoundException("Фильм не найден")));
    }

    public FilmDto likeFilm(Integer filmId, Integer userId) {
        validateFilmAndUserExist(filmId, userId);

        LikesFromUsers like = likesFromUsersStorage.addLike(filmId, userId);
        FilmDto filmDto = getFilmById(filmId);
        filmDto.getLikesFromUsers().add(like.getUserId());

        log.info("Пользователь с id {} лайкнул фильм с id {}.", userId, filmId);
        return filmDto;
    }

    public FilmDto deleteLikeFromFilm(Integer filmId, Integer userId) {
        validateFilmAndUserExist(filmId, userId);

        Film film = filmDbStorage.findById(filmId).get();
        Set<Integer> likes = film.getLikesFromUsers();
        if (!likes.contains(userId)) {
            log.error("Пользователь с id: {} еще не лайкал фильм с id: {}.", userId, filmId);
            throw new NotFoundException("Данный пользователь еще не лайкал этот фильм.");
        }

        likesFromUsersStorage.deleteLike(filmId, userId);
        log.info("Лайк пользователя с id {} был удален у фильма с id {}.", userId, filmId);

        return getFilmById(filmId);
    }

    public List<FilmDto> getPopularFilms(int count) {
        List<Film> films = filmDbStorage.findAll();
        return films.stream()
                .sorted(Comparator.comparingInt(film -> film.getLikesFromUsers().size()))
                .limit(count)
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    private void validateFilmRequest(NewFilmRequest request) {
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new ConditionsNotMetException("Название фильма должно быть указано");
        }

        if (request.getMpa().getId() < 1 || request.getMpa().getId() > 5) {
            throw new ValidationException("У рейтинга id от 1 до 5");
        }

        if (request.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
    }

    private void processGenres(NewFilmRequest request) {
        request.getGenres().forEach(genre -> {
            if (genre.getId() < 1 || genre.getId() > 6) {
                throw new ValidationException("У жанров id от 1 до 6");
            }
        });

        Set<Integer> uniqueGenreIds = request.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        List<Genre> uniqueGenres = uniqueGenreIds.stream()
                .map(id -> genreStorage.findById(id).orElseThrow(() -> new ValidationException("Жанр с id " + id + " не найден")))
                .collect(Collectors.toList());

        uniqueGenres.forEach(genre -> genre.setName(genreStorage.findById(genre.getId()).get().getName()));
        request.setGenres(uniqueGenres);
    }

    private void addFilmGenres(Film film) {
        film.getGenres().forEach(genre -> {
            FilmGenre filmGenre = new FilmGenre();
            filmGenre.setFilmId(film.getId());
            filmGenre.setGenreId(genre.getId());
            filmGenreStorage.addGenre(filmGenre);
        });
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
    }

    private void validateFilmAndUserExist(Integer filmId, Integer userId) {
        if (filmDbStorage.findById(filmId).isEmpty()) {
            log.error("Фильм с id: {} не существует.", filmId);
            throw new NotFoundException("Фильм с данным id не существует.");
        }

        if (userDbStorage.findById(userId).isEmpty()) {
            log.error("Пользователь с id: {} не существует.", userId);
            throw new NotFoundException("Пользователь с данным id не существует.");
        }
    }
}
