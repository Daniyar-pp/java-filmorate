package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> filmsAll() {
        log.info("Запрошен список всех фильмов. Текущее количество: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.debug("Попытка создания фильма: {}", film);
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм успешно создан: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.debug("Попытка обновления фильма: {}", film);

        if (!films.containsKey(film.getId())) {
            log.warn("Ошибка обновления: фильм с id={} не найден", film.getId());
            throw new ValidationException("Фильм с id " + film.getId() + " не найден");
        }

        validateFilm(film);
        films.put(film.getId(), film);
        log.info("Фильм успешно обновлен: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    private void validateFilm(Film film) {

        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Ошибка валидации: название фильма пустое или состоит из пробелов");
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Ошибка валидации: описание фильма слишком длинное ({} символов, максимум 200)",
                    film.getDescription().length());
            throw new ValidationException("Описание не может быть длиннее 200 символов");
        }

        if (film.getReleaseDate() == null || film.getReleaseDate().isBlank()) {
            log.warn("Ошибка валидации: дата релиза пустая");
            throw new ValidationException("Дата релиза не может быть пустой");
        }

        LocalDate releaseDate;
        try {
            releaseDate = LocalDate.parse(film.getReleaseDate());
        } catch (Exception e) {
            log.warn("Ошибка валидации: неверный формат даты - {}", film.getReleaseDate());
            throw new ValidationException("Неверный формат даты. Используйте формат yyyy-MM-dd");
        }

        if (releaseDate.isBefore(MIN_RELEASE_DATE)) {
            log.warn("Ошибка валидации: дата релиза {} раньше допустимой {}",
                    releaseDate, MIN_RELEASE_DATE);
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

        if (film.getDuration() <= 0) {
            log.warn("Ошибка валидации: продолжительность {} <= 0", film.getDuration());
            throw new ValidationException("Продолжительность должна быть положительным числом");
        }
    }

    private int getNextId() {
        int nextId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0) + 1;
        log.debug("Сгенерирован новый ID для фильма: {}", nextId);
        return nextId;
    }
}