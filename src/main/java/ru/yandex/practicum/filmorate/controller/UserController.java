package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> usersAll() {
        log.info("Запрошен список всех пользователей. Текущее количество: {}", users.size());
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        log.debug("Попытка создания пользователя: {}", user);

        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации при создании пользователя: email не прошел проверку - {}", user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @;");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Ошибка валидации при создании пользователя: логин пустой или состоит из пробелов");
            throw new ValidationException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации при создании пользователя: логин содержит пробелы - '{}'", user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пользователя пустое, будет использован логин: {}", user.getLogin());
            user.setName(user.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации при создании пользователя: дата рождения в будущем - {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан: id={}, login={}, email={}", user.getId(), user.getLogin(), user.getEmail());
        return user;
    }

    private int getNextId() {
        int nextId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0) + 1;
        log.debug("Сгенерирован новый ID: {}", nextId);
        return nextId;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        log.debug("Попытка обновления пользователя: {}", user);

        if (!users.containsKey(user.getId())) {
            log.warn("Ошибка обновления: пользователь с id={} не найден", user.getId());
            throw new ValidationException("Пользователь " + user.getId() + " не найден");
        }

        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации при обновлении пользователя id={}: email не прошел проверку - {}", user.getId(), user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @;");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Ошибка валидации при обновлении пользователя id={}: логин пустой или состоит из пробелов", user.getId());
            throw new ValidationException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации при обновлении пользователя id={}: логин содержит пробелы - '{}'", user.getId(), user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пользователя id={} пустое, будет использован логин: {}", user.getId(), user.getLogin());
            user.setName(user.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации при обновлении пользователя id={}: дата рождения в будущем - {}", user.getId(), user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }

        users.put(user.getId(), user);
        log.info("Пользователь успешно обновлен: id={}, login={}, email={}", user.getId(), user.getLogin(), user.getEmail());
        return user;
    }
}