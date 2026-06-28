package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> getAllFilms();

    Optional<Film> getFilmById(int id);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(int id);

    boolean filmExists(int id);
}