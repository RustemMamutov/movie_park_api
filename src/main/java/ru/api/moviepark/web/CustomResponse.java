package ru.api.moviepark.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CustomResponse {
    PLACES_BLOCKED("The places blocked successfully."),
    PLACES_UNBLOCKED("The places unblocked successfully."),
    TABLE_FILLED("Table filled for this date."),
    INVALID_SEANCE_ID("The seance id doesn't exist."),
    INVALID_DATE("The date can't be before today."),
    INVALID_START_END_TIME("Invalid time period. End time is before start time"),
    INVALID_TIME_PERIOD("Invalid time period."),
    INVALID_HALL("The hall doesn't exist."),
    INVALID_MOVIE("The movie doesn't exist."),
    INVALID_PRICE("The price can't be less or equals 0."),
    ERROR("Error");

    private final String message;

    public ResponseEntity<CustomResponse> entity(HttpStatus status) {
        return ResponseEntity.status(status).body(this);
    }
}
