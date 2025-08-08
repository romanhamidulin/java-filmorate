package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.enums.Genres;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    @NotNull
    private Integer id;
    @NotNull
    private Genres name;

}