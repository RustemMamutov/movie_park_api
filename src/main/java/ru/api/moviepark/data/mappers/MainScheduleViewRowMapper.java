package ru.api.moviepark.data.mappers;

import org.springframework.jdbc.core.RowMapper;
import ru.api.moviepark.data.valueobjects.MainScheduleViewEntity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MainScheduleViewRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int row) throws SQLException {
        return MainScheduleViewEntity.builder()
                .seanceId(rs.getInt("seance_id"))
                .seanceDate(rs.getDate("seance_date").toLocalDate())
                .startTime(rs.getTime("start_time").toLocalTime())
                .endTime(rs.getTime("end_time").toLocalTime())
                .movieParkId(rs.getInt("movie_park_id"))
                .movieParkName(rs.getString("movie_park_name"))
                .movieId(rs.getInt("movie_id"))
                .movieName(rs.getString("movie_name"))
                .hallId(rs.getInt("hall_id"))
                .basePrice(rs.getInt("base_price"))
                .vipPrice(rs.getInt("vip_price"))
                .build();
    }
}
