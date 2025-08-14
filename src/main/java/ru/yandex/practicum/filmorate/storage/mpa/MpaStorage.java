package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class MpaStorage extends BaseStorage<Mpa> implements MpaDao {

    private static final String FIND_ALL_QUERY = "select * from film_mpa order by id";
    private static final String FIND_BY_ID_QUERY = "select * from film_mpa where id = ? order by id";

    public MpaStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<Mpa> getMpaById(int mpaId) {
        return findOne(FIND_BY_ID_QUERY, mpaId);
    }

    @Override
    public List<Mpa> getMpaList() {
        log.debug("список рейтингов()");
        List<Mpa> mpaList = findMany(FIND_ALL_QUERY);
        log.trace("Весь список рейтингов: {}", mpaList);
        return mpaList;
    }


}