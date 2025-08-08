package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.enums.Mpas;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mpa {

    @Positive
    private Integer id;

    @NotNull
    @NotBlank
    private Mpas name;

}
