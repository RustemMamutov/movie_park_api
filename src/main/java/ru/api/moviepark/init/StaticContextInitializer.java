package ru.api.moviepark.init;

import org.springframework.context.annotation.Configuration;
import ru.api.moviepark.actuator.RpsCalculatorUtil;
import ru.api.moviepark.cache.HallsTtlCache;
import ru.api.moviepark.cache.MoviesInfoTtlCache;
import ru.api.moviepark.cache.SeanceInfoTtlCache;
import ru.api.moviepark.cache.SeancePlacesTtlCache;
import ru.api.moviepark.cache.UserCredentialsTtlCache;
import ru.api.moviepark.data.repositories.HallsRepo;
import ru.api.moviepark.data.repositories.MainScheduleRepo;
import ru.api.moviepark.data.repositories.UserCredentialRepo;
import ru.api.moviepark.env.MovieParkEnv;
import ru.api.moviepark.util.CheckInputUtil;

import javax.annotation.PostConstruct;

@Configuration
public class StaticContextInitializer {

    private final MovieParkEnv environment;
    private final MainScheduleRepo mainScheduleRepo;
    private final HallsRepo hallsRepo;
    private final UserCredentialRepo userCredentialRepo;

    public StaticContextInitializer(MovieParkEnv environment,
                                    MainScheduleRepo mainScheduleRepo,
                                    HallsRepo hallsRepo,
                                    UserCredentialRepo userCredentialRepo) {
        this.environment = environment;
        this.mainScheduleRepo = mainScheduleRepo;
        this.hallsRepo = hallsRepo;
        this.userCredentialRepo = userCredentialRepo;
    }

    @PostConstruct
    public void initCaches() {
        CheckInputUtil.setMainScheduleRepo(mainScheduleRepo);
        CheckInputUtil.setHallsRepo(hallsRepo);

        SeanceInfoTtlCache.init();

        MoviesInfoTtlCache.init();

        SeancePlacesTtlCache.setEnv(environment);
        SeancePlacesTtlCache.init();

        UserCredentialsTtlCache.setUserCredentialRepo(userCredentialRepo);
        UserCredentialsTtlCache.init();

        HallsTtlCache.setHallsRepo(hallsRepo);
        HallsTtlCache.init();
    }

    @PostConstruct
    public void initRpsCalculator() {
        RpsCalculatorUtil.startRpsTimeoutFlushProcess(environment);
    }
}
