package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Duration;

public class DurationDeserializer extends JsonDeserializer<Duration> {
    @Override
    public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        int minutes = p.getIntValue();
        if (minutes <= 0) {
            throw new IllegalArgumentException("Продолжительность должна быть положительным числом");
        }
        return Duration.ofMinutes(minutes);
    }
}