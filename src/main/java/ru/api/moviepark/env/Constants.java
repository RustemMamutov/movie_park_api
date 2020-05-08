package ru.api.moviepark.env;

import java.time.format.DateTimeFormatter;

public class Constants {

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final String SCHEMA_NAME = "movie_park";

    public static final String MAIN_SCHEDULE_TABLE_NAME = "main_schedule";

    public static final String HALLS_TABLE_NAME = "halls";

    public static final String MOVIES_TABLE_NAME = "movies";

    public static final String MOVIE_PARKS_TABLE_NAME = "movie_parks";

    public static final String SEANCE_PLACES_TABLE_NAME = "seances_places";
}
