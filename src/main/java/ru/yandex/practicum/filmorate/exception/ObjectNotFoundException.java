package ru.yandex.practicum.filmorate.exception;

public class ObjectNotFoundException extends IllegalArgumentException {

    public ObjectNotFoundException(final String message) {
        super(message);
    }
}
