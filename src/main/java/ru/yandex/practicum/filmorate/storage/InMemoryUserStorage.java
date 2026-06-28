package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @Override
    public Collection<User> getAllUsers() {
        log.info("Запрошен список всех пользователей. Текущее количество: {}", users.size());
        return users.values();
    }

    @Override
    public Optional<User> getUserById(int id) {
        log.debug("Запрос пользователя по id: {}", id);
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User createUser(User user) {
        log.debug("Попытка создания пользователя: {}", user);
        validateUser(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан: id={}, login={}, email={}",
                user.getId(), user.getLogin(), user.getEmail());
        return user;
    }

    @Override
    public User updateUser(User user) {
        log.debug("Попытка обновления пользователя: {}", user);

        if (!users.containsKey(user.getId())) {
            log.warn("Ошибка обновления: пользователь с id={} не найден", user.getId());
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }

        validateUser(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        users.put(user.getId(), user);
        log.info("Пользователь успешно обновлен: id={}, login={}, email={}",
                user.getId(), user.getLogin(), user.getEmail());
        return user;
    }

    @Override
    public void deleteUser(int id) {
        log.debug("Попытка удаления пользователя с id: {}", id);
        if (!users.containsKey(id)) {
            log.warn("Ошибка удаления: пользователь с id={} не найден", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        users.remove(id);
        log.info("Пользователь с id {} успешно удален", id);
    }

    @Override
    public boolean userExists(int id) {
        return users.containsKey(id);
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации: email не прошел проверку - {}", user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank()) {
            log.warn("Ошибка валидации: логин пустой или состоит из пробелов");
            throw new ValidationException("Логин не может быть пустым");
        }

        if (user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации: логин содержит пробелы - '{}'", user.getLogin());
            throw new ValidationException("Логин не может содержать пробелы");
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации: дата рождения в будущем - {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}