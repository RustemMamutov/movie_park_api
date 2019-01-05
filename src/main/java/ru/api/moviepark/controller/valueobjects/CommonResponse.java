package ru.api.moviepark.controller.valueobjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CommonResponse {
    SEANCE_ADDED(200, "Seance added."),
    PLACE_BLOCKED(200, "The place blocked successfully."),
    TABLES_UPDATED(200, "All tables updated."),
    ERROR(400, "Error");

    private int code;
    private String message;
}
