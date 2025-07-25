package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film saveFilm(@RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping
    public void deleteFirm(@RequestBody int filmId) {
        filmService.deleteFilm(filmId);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable int id) {
        log.info("Запрос на получение фильма по id: {}", id);
        Film film = filmService.getFilmById(id);
        log.info("Вернули фильм: {}", film);
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(
            @PathVariable int id,
            @PathVariable int userId) {
        log.info("Запрос на добавление лайка фильму: filmId={}, userId={}", id, userId);
        return filmService.addLike(id, userId);

    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(
            @PathVariable int id,
            @PathVariable int userId) {
        log.info("Запрос на удаление лайка у фильма: filmId={}, userId={}", id, userId);
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(defaultValue = "10") int count) {
        log.info("Запрос на получение топа популярных фильмов {} ", count);
        List<Film> popularFilms = filmService.getPopularFilms(count);
        log.info("Вернули топ популярных фильмов {}", popularFilms.size());
        return popularFilms;
    }
}

