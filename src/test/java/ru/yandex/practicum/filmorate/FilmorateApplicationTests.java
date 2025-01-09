package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.UserDbStorage;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmorateApplicationTests {

    @Autowired
	private UserDbStorage userStorage;

	private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	private final Validator validator = factory.getValidator();

	private Film film;
	private User user;

    FilmorateApplicationTests(MockMvc ignoredMockMvc, FilmController ignoredFilmController, UserController ignoredUserController) {
    }

    @BeforeEach
	void setUp() {
		film = new Film();
		user = new User();

        User validUser = new User();
		validUser.setEmail("example@mail.com");
		validUser.setLogin("validLogin");
		validUser.setBirthday(LocalDate.of(2000, 1, 1));
	}

	@Test
	void filmValidationTest() {
		film.setName("");
		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Название фильма должно быть заполнено.");

		film.setName("Название");
		film.setDescription("A".repeat(201));
		violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Описание фильма не может превышать 200 символов.");

		film.setDescription("Описание");
		film.setReleaseDate(LocalDate.of(3000, 1, 1));
		violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Дата релиза не должна быть в будущем.");

		film.setReleaseDate(LocalDate.of(2020, 1, 1));
		film.setDuration(-10);
		violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Продолжительность фильма должна быть положительным числом.");

		film.setDuration(120);
		film.setMpa(MpaRating.G);
		violations = validator.validate(film);
		assertTrue(violations.isEmpty(), "Фильм должен быть валидным.");

		film.setName("Valid Film");
		film.setDescription("Valid Description");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);
		film.setMpa(MpaRating.G);
		violations = validator.validate(film);
		assertTrue(violations.isEmpty(), "Фильм должен быть валидным.");

		film.setDuration(0);
		violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Продолжительность фильма должна быть положительным числом.");
	}

	@Test
	void userValidationTest() {
		user.setEmail("");
		user.setLogin("validLogin");
		user.setBirthday(LocalDate.of(2000, 1, 1));
		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Электронная почта не может быть пустой.");

		user.setEmail("invalid");
		violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Электронная почта должна быть валидной.");

		user.setEmail("user@user.com");
		user.setLogin("");
		violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Логин должен быть заполнен.");

		user.setLogin("invalid login");
		violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Логин не должен содержать пробелы.");

		user.setLogin("validLogin");
		user.setBirthday(LocalDate.of(3000, 1, 1));
		violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Дата рождения не может быть в будущем.");

		user.setBirthday(LocalDate.of(2000, 1, 1));
		violations = validator.validate(user);
		assertTrue(violations.isEmpty(), "Пользователь должен быть валидным.");
	}

	@Test
	void testFindUserById() {
		User newUser = new User();
		newUser.setEmail("test@mail.com");
		newUser.setLogin("testUser");
		newUser.setBirthday(LocalDate.of(1990, 1, 1));
		User savedUser = userStorage.addUser(newUser);

		User retrievedUser = userStorage.getUserById(savedUser.getId());

		assertNotNull(retrievedUser, "Пользователь должен быть найден.");
		assertEquals(savedUser.getId(), retrievedUser.getId(), "ID пользователя должен совпадать.");
		assertEquals(savedUser.getEmail(), retrievedUser.getEmail(), "Email пользователя должен совпадать.");
	}
}
