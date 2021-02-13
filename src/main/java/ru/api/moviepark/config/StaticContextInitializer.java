package ru.api.moviepark.config;

import org.springframework.context.annotation.Configuration;
import ru.api.moviepark.actuator.RpsCalculatorUtil;
import ru.api.moviepark.data.repositories.HallsRepo;
import ru.api.moviepark.data.repositories.MainScheduleRepo;
import ru.api.moviepark.env.MovieParkEnv;
import ru.api.moviepark.util.InputPreconditionsUtil;

import javax.annotation.PostConstruct;

@Configuration
public class StaticContextInitializer {

    private final MovieParkEnv environment;
    private final MainScheduleRepo mainScheduleRepo;
    private final HallsRepo hallsRepo;

    public StaticContextInitializer(MovieParkEnv environment,
                                    MainScheduleRepo mainScheduleRepo,
                                    HallsRepo hallsRepo) {
        this.environment = environment;
        this.mainScheduleRepo = mainScheduleRepo;
        this.hallsRepo = hallsRepo;
    }

    @PostConstruct
    public void initCaches() {
        InputPreconditionsUtil.setMainScheduleRepo(mainScheduleRepo);
        InputPreconditionsUtil.setHallsRepo(hallsRepo);
    }

    @PostConstruct
    public void initRpsCalculator() {
        RpsCalculatorUtil.startRpsTimeoutFlushProcess(environment);
    }
}
