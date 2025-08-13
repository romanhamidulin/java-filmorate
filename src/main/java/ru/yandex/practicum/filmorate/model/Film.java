package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.controller.DurationDeserializer;
import ru.yandex.practicum.filmorate.controller.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class Film {

    private Long id;

    @NotBlank
    private String name;

    @Size
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @NotNull
    //@Positive
    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration duration;

    private final Set<Long> likes = new HashSet<>();

    private Mpa mpa;

    private Set<Genre> genres = new LinkedHashSet<>();

}

