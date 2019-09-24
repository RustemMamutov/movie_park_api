package ru.api.moviepark.config;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.api.moviepark.actuator.RpsCalculatorUtil;
import ru.api.moviepark.data.repositories.HallsRepo;
import ru.api.moviepark.data.repositories.MainScheduleRepo;
import ru.api.moviepark.service.cache.SeanceInfoTtlCache;
import ru.api.moviepark.service.cache.SeancePlacesTtlCache;
import ru.api.moviepark.util.CheckInputUtil;

import javax.annotation.PostConstruct;

@Component
public class StaticContextInitializer {

    private MovieParkEnvironment environment;
    private MainScheduleRepo mainScheduleRepo;
    private HallsRepo hallsRepo;
    private ApplicationContext context;

    public StaticContextInitializer(MovieParkEnvironment environment,
                                    MainScheduleRepo mainScheduleRepo,
                                    HallsRepo hallsRepo,
                                    ApplicationContext context) {
        this.environment = environment;
        this.mainScheduleRepo = mainScheduleRepo;
        this.hallsRepo = hallsRepo;
        this.context = context;
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
        SeanceInfoTtlCache.initSeanceInfoCache(environment);
        SeancePlacesTtlCache.initSeancePlacesTtlCache(environment);
    }
}
