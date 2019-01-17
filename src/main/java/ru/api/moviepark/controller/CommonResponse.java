package ru.api.moviepark.controller;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CommonResponse {
    SEANCE_ADDED(200, "Seance added."),
    PLACE_BLOCKED(200, "The place blocked successfully."),
    PLACE_UNBLOCKED(200, "The place unblocked successfully."),
    TABLE_FILLED(200, "Table filled for this date."),
    VALID_DATA(200, ""),
    INVALID_DATE(400, "the date can't be before today."),
    INVALID_TIME_PERIOD(400, "invalid time period."),
    INVALID_HALL(400, "the hall doesn't exist."),
    INVALID_MOVIE(400, "the movie doesn't exist."),
    INVALID_PRICE(400, "the price can't be less or equals 0."),
    ERROR(400, "Error");

    private int code;
    private String message;
}
