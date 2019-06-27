package ru.api.moviepark.data.remote_db.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.api.moviepark.data.valueobjects.AllSeancesView;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AllSeancesViewRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int row) throws SQLException {
        return AllSeancesView.builder()
                .seanceId(rs.getInt("seance_id"))
                .seanceDate(rs.getDate("seance_date"))
                .startTime(rs.getTime("start_time"))
                .endTime(rs.getTime("end_time"))
                .movieName(rs.getString("movie_name"))
                .hallId(rs.getInt("hall_id"))
                .build();
    }
}
