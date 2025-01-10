package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class Film {
    private Integer id;

    @NotBlank
    private final String name;

    @Size(max = 200, message = "Максимальная длина описания не должна превышать 200 символов.")
    private final String description;

    @NotNull
    private final LocalDate releaseDate;  // или Instant, если нужно

    @NotNull
    @Positive(message = "Продолжительность должна быть положительным числом")
    private final Integer duration;

    @NotNull(message = "Список лайков не может быть пустым.")
    private Set<Integer> userLikesIds = new HashSet<>();

    @NotNull(message = "Список жанров не может быть пустым.")
    private Set<Genre> genres = new HashSet<>();

    private Mpa mpa;
}
