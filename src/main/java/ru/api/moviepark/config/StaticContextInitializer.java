package ru.api.moviepark.config;

import org.springframework.context.annotation.Configuration;
import ru.api.moviepark.actuator.RpsCalculatorUtil;
import ru.api.moviepark.data.repositories.HallsRepo;
import ru.api.moviepark.data.repositories.MainScheduleRepo;
import ru.api.moviepark.env.MovieParkEnv;
import ru.api.moviepark.service.cache.MoviesInfoTtlCache;
import ru.api.moviepark.service.cache.SeanceInfoTtlCache;
import ru.api.moviepark.service.cache.SeancePlacesTtlCache;
import ru.api.moviepark.util.CheckInputUtil;

import javax.annotation.PostConstruct;

@Configuration
public class StaticContextInitializer {

    private MovieParkEnv environment;
    private MainScheduleRepo mainScheduleRepo;
    private HallsRepo hallsRepo;

    public StaticContextInitializer(MovieParkEnv environment,
                                    MainScheduleRepo mainScheduleRepo,
                                    HallsRepo hallsRepo) {
        this.environment = environment;
        this.mainScheduleRepo = mainScheduleRepo;
        this.hallsRepo = hallsRepo;
    }

    @PostConstruct
    public void init() {
        CheckInputUtil.setMainScheduleRepo(mainScheduleRepo);
        CheckInputUtil.setHallsRepo(hallsRepo);
    }

    @PostConstruct
    public void initRpsCalculator() {
        RpsCalculatorUtil.startRpsTimeoutFlushProcess(environment);
    }

    @PostConstruct
    public void initSeancePlacesInfoTtlCache() {
        SeanceInfoTtlCache.initSeanceInfoCache();
        MoviesInfoTtlCache.initMoviesInfoCache();
        SeancePlacesTtlCache.setEnv(environment);
        SeancePlacesTtlCache.initSeancePlacesTtlCache();
    }
}
