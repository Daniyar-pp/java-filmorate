package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getAllUsers();

    Optional<User> getUserById(int id);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(int id);

    boolean userExists(int id);
}