package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        log.info("Запрос на получение пользователя с id {}", id);
        return userService.getUserById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Запрос на создание пользователя: {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Запрос на обновление пользователя: {}", user);
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        log.info("Запрос на удаление пользователя с id {}", id);
        userService.deleteUser(id);
    }

    // PUT /users/{id}/friends/{friendId} - добавить в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Запрос на добавление в друзья: пользователь id={}, друг id={}", id, friendId);
        userService.addFriend(id, friendId);
    }

    // DELETE /users/{id}/friends/{friendId} - удалить из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Запрос на удаление из друзей: пользователь id={}, друг id={}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    // GET /users/{id}/friends - список друзей
    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable int id) {
        log.info("Запрос на получение списка друзей пользователя id={}", id);
        return userService.getFriends(id);
    }

    // GET /users/{id}/friends/common/{otherId} - общие друзья
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Запрос на получение общих друзей пользователей id={} и id={}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}