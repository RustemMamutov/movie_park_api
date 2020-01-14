package ru.api.moviepark.config;

import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class Constants {

    public static MovieParkEnvironment env;

    public Constants(MovieParkEnvironment environment) {
        env = environment;
    }

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final String SCHEMA_NAME = "movie_park";

    public static final String MAIN_SCHEDULE_TABLE_NAME = "main_schedule";

    public static final String HALLS_TABLE_NAME = "halls";

    public static final String MOVIES_TABLE_NAME = "movies";

    public static final String MOVIE_PARKS_TABLE_NAME = "movie_parks";

    public static final String SEANCE_PLACES_TABLE_NAME = "seances_places";

    public static final String MAIN_SCHEDULE_VIEW_NAME = "main_schedule_view";

    public static final String MAIN_SCHEDULE_VIEW_FULL =
            String.format("%s.%s", SCHEMA_NAME, MAIN_SCHEDULE_VIEW_NAME);
}
