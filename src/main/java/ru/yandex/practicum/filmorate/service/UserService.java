package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void deleteUser(int id) {
        userStorage.deleteUser(id);
    }

    public void addFriend(int userId, int friendId) {
        log.debug("Попытка добавить в друзья: пользователь id={}, друг id={}", userId, friendId);

        if (userId == friendId) {
            log.warn("Ошибка: попытка добавить самого себя в друзья");
            throw new ValidationException("Нельзя добавить самого себя в друзья");
        }

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (user.getFriends().contains(friendId)) {
            log.warn("Пользователи уже являются друзьями: {} и {}", userId, friendId);
            throw new ValidationException("Пользователи уже являются друзьями");
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        log.info("Пользователи добавлены в друзья: {} <-> {}", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        log.debug("Попытка удалить из друзей: пользователь id={}, друг id={}", userId, friendId);

        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (!user.getFriends().contains(friendId)) {
            log.warn("Ошибка: пользователь {} не является другом {}", userId, friendId);
            throw new NotFoundException("Пользователь не является другом");
        }

        if (!friend.getFriends().contains(userId)) {
            log.warn("Ошибка: пользователь {} не является другом {}", friendId, userId);
            // Восстанавливаем согласованность данных
            user.getFriends().remove(friendId);
            throw new NotFoundException("Пользователь не является другом");
        }

        // Удаляем друг друга из списков друзей
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.info("Пользователи удалены из друзей: {} <-> {}", userId, friendId);
    }

    public Collection<User> getFriends(int userId) {
        log.debug("Запрос списка друзей для пользователя id={}", userId);

        User user = getUserById(userId);

        return user.getFriends().stream()
                .map(id -> userStorage.getUserById(id)
                        .orElseThrow(() -> new NotFoundException("Друг с id " + id + " не найден")))
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(int userId, int otherId) {
        log.debug("Запрос общих друзей для пользователей id={} и id={}", userId, otherId);

        User user = getUserById(userId);
        User other = getUserById(otherId);

        Set<Integer> commonFriendIds = user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .collect(Collectors.toSet());

        return commonFriendIds.stream()
                .map(id -> userStorage.getUserById(id)
                        .orElseThrow(() -> new NotFoundException("Друг с id " + id + " не найден")))
                .collect(Collectors.toList());
    }
}