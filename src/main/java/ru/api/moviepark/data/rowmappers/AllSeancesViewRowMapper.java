package ru.api.moviepark.data.rowmappers;

import org.springframework.jdbc.core.RowMapper;
import ru.api.moviepark.data.valueobjects.AllSeancesView;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AllSeancesViewRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int row) throws SQLException {
        return AllSeancesView.builder()
                .id(rs.getInt("id"))
                .date(rs.getDate("date"))
                .startTime(rs.getTime("start_time"))
                .endTime(rs.getTime("end_time"))
                .name(rs.getString("name"))
                .hallId(rs.getInt("hall_id"))
                .build();
    }
}
