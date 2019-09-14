package ru.api.moviepark.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CommonResponse {
    SEANCE_ADDED(200, "Seance added."),
    PLACE_BLOCKED(200, "The places blocked successfully."),
    PLACE_UNBLOCKED(200, "The places unblocked successfully."),
    TABLE_FILLED(200, "Table filled for this date."),
    VALID_DATA(200, ""),
    INVALID_DATE(400, "The date can't be before today."),
    INVALID_TIME_PERIOD(400, "Invalid time period."),
    INVALID_HALL(400, "The hall doesn't exist."),
    INVALID_MOVIE(400, "The movie doesn't exist."),
    INVALID_PRICE(400, "The price can't be less or equals 0."),
    ERROR(400, "Error");

    private int code;
    private String message;
}
