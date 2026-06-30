package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;
    private String name;
    private String description;
    private String releaseDate;
    private int duration;
    private Set<Integer> likes = new HashSet<>();
}