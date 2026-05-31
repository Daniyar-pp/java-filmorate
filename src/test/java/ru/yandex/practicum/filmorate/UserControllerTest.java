package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private UserController userController;
    private User validUser;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        validUser = new User();
        validUser.setEmail("user@example.com");
        validUser.setLogin("validLogin");
        validUser.setName("Valid Name");
        validUser.setBirthday(LocalDate.of(1990, 1, 1));
    }


    @Test
    void createUser_ShouldSucceed_WhenEmailIsValid() {
        assertDoesNotThrow(() -> userController.create(validUser));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailIsNull() {
        validUser.setEmail(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(validUser));

        assertTrue(exception.getMessage().contains("Электронная почта не может быть пустой"));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailDoesNotContainAtSymbol() {
        validUser.setEmail("userexample.com");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userController.create(validUser));

        assertTrue(exception.getMessage().contains("должна содержать символ @"));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailIsEmpty() {
        validUser.setEmail("");

        assertThrows(ValidationException.class,
                () -> userController.create(validUser));
    }


    @Test
    void createUser_ShouldGenerateDifferentIds() {
        User user1 = userController.create(validUser);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setLogin("login2");
        user2.setBirthday(LocalDate.of(1995, 5, 5));
        User created2 = userController.create(user2);

        assertNotEquals(user1.getId(), created2.getId());
    }
}