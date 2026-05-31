package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController filmController;
    private Film validFilm;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        validFilm = new Film();
        validFilm.setName("Valid Film");
        validFilm.setDescription("This is a valid description");
        validFilm.setReleaseDate("2020-01-01");
        validFilm.setDuration(120);
    }


    @Test
    void createFilm_ShouldSucceed_WhenNameIsValid() {
        assertDoesNotThrow(() -> filmController.create(validFilm));
    }

    @Test
    void createFilm_ShouldThrowException_WhenNameIsNull() {
        validFilm.setName(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.create(validFilm));

        assertTrue(exception.getMessage().contains("Название не может быть пустым"));
    }

    @Test
    void createFilm_ShouldThrowException_WhenNameIsBlank() {
        validFilm.setName("   ");

        assertThrows(ValidationException.class,
                () -> filmController.create(validFilm));
    }


    @Test
    void createFilm_ShouldSucceed_WhenDescriptionIsNull() {
        validFilm.setDescription(null);

        assertDoesNotThrow(() -> filmController.create(validFilm));
    }

    @Test
    void createFilm_ShouldSucceed_WhenDescriptionIsExactly200Chars() {
        String description200 = "a".repeat(200);
        validFilm.setDescription(description200);

        assertDoesNotThrow(() -> filmController.create(validFilm));
    }

    @Test
    void createFilm_ShouldThrowException_WhenDescriptionIsLongerThan200() {
        String description201 = "a".repeat(201);
        validFilm.setDescription(description201);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.create(validFilm));

        assertTrue(exception.getMessage().contains("Описание не может быть длиннее 200 символов"));
    }

    @Test
    void createFilm_ShouldThrowException_WhenReleaseDateIsNull() {
        validFilm.setReleaseDate(null);

        assertThrows(ValidationException.class,
                () -> filmController.create(validFilm));
    }

    @Test
    void createFilm_ShouldThrowException_WhenReleaseDateIsBlank() {
        validFilm.setReleaseDate("   ");

        assertThrows(ValidationException.class,
                () -> filmController.create(validFilm));
    }


    @Test
    void createFilm_ShouldHandleAllBoundaryValuesTogether() {
        validFilm.setDescription("a".repeat(200));
        validFilm.setReleaseDate("1895-12-28");
        validFilm.setDuration(1);

        assertDoesNotThrow(() -> filmController.create(validFilm));
    }
}