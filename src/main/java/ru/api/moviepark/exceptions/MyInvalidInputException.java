package ru.api.moviepark.exceptions;

import ru.api.moviepark.web.CustomResponse;

public class MyInvalidInputException extends RuntimeException {
    public MyInvalidInputException(String message) {
        super(message);
    }

    public MyInvalidInputException(CustomResponse response) {
        super(response.getMessage());
    }
}
