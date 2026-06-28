package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public void deleteFilm(int id) {
        filmStorage.deleteFilm(id);
    }

    // Добавить лайк
    public void addLike(int filmId, int userId) {
        log.debug("Попытка добавить лайк: фильм id={}, пользователь id={}", filmId, userId);

        Film film = getFilmById(filmId);

        if (!userStorage.userExists(userId)) {
            log.warn("Ошибка: пользователь с id={} не найден", userId);
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        if (film.getLikes().contains(userId)) {
            log.warn("Пользователь {} уже поставил лайк фильму {}", userId, filmId);
            throw new ValidationException("Пользователь уже поставил лайк этому фильму");
        }

        film.getLikes().add(userId);
        log.info("Лайк добавлен: фильм id={}, пользователь id={}", filmId, userId);
    }

    // Удалить лайк
    public void removeLike(int filmId, int userId) {
        log.debug("Попытка удалить лайк: фильм id={}, пользователь id={}", filmId, userId);

        Film film = getFilmById(filmId);

        if (!film.getLikes().contains(userId)) {
            log.warn("Ошибка: пользователь {} не ставил лайк фильму {}", userId, filmId);
            throw new NotFoundException("Пользователь не ставил лайк этому фильму");
        }

        film.getLikes().remove(userId);
        log.info("Лайк удален: фильм id={}, пользователь id={}", filmId, userId);
    }

    // Получить популярные фильмы
    public List<Film> getPopularFilms(int count) {
        log.debug("Запрос на получение {} популярных фильмов", count);

        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}