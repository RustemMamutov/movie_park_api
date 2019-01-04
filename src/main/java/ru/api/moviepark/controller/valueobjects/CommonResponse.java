package ru.api.moviepark.controller.valueobjects;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CommonResponse {
    DATA_UPDATED(200, "All data updated"),
    QUERIES_DELETED(200, "All queries deleted"),
    ERROR(400, "Error");

    private int code;
    private String message;
}
