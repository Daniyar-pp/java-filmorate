package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public Collection<Film> getAllFilms() {
        log.info("Запрошен список всех фильмов. Текущее количество: {}", films.size());
        return films.values();
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        log.debug("Запрос фильма по id: {}", id);
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Film createFilm(Film film) {
        log.debug("Попытка создания фильма: {}", film);
        validateFilm(film);
        film.setId(nextId++);
        films.put(film.getId(), film);
        log.info("Фильм успешно создан: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("Попытка обновления фильма: {}", film);

        if (!films.containsKey(film.getId())) {
            log.warn("Ошибка обновления: фильм с id={} не найден", film.getId());
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }

        validateFilm(film);
        films.put(film.getId(), film);
        log.info("Фильм успешно обновлен: id={}, name={}", film.getId(), film.getName());
        return film;
    }

    @Override
    public void deleteFilm(int id) {
        log.debug("Попытка удаления фильма с id: {}", id);
        if (!films.containsKey(id)) {
            log.warn("Ошибка удаления: фильм с id={} не найден", id);
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        films.remove(id);
        log.info("Фильм с id {} успешно удален", id);
    }

    @Override
    public boolean filmExists(int id) {
        return films.containsKey(id);
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
}