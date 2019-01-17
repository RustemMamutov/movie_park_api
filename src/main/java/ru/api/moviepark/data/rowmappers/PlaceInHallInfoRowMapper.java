package ru.api.moviepark.data.rowmappers;

import org.springframework.jdbc.core.RowMapper;
import ru.api.moviepark.data.valueobjects.PlaceInHallInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlaceInHallInfoRowMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet rs, int row) throws SQLException {
        return PlaceInHallInfo.builder()
                .id(rs.getInt("id"))
                .line(rs.getInt("line"))
                .place(rs.getInt("place"))
                .isVip(rs.getBoolean("isvip"))
                .price(rs.getInt("price"))
                .isBlocked(rs.getBoolean("isblocked"))
                .build();
    }
}
