package ru.api.moviepark.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.api.moviepark.data.remote.repositories.HallsRepo;
import ru.api.moviepark.data.remote.repositories.MainScheduleRepo;
import ru.api.moviepark.data.remote.repositories.MoviesRepo;
import ru.api.moviepark.util.CheckInputUtil;

import javax.annotation.PostConstruct;

@Component
public class StaticContextInitializer {

    @Autowired
    private MainScheduleRepo mainScheduleRepo;

    @Autowired
    private MoviesRepo moviesRepo;

    @Autowired
    private HallsRepo hallsRepo;

    @Autowired
    private ApplicationContext context;

    @PostConstruct
    public void init() {
        CheckInputUtil.setMainScheduleRepo(mainScheduleRepo);
        CheckInputUtil.setMoviesRepo(moviesRepo);
        CheckInputUtil.setHallsRepo(hallsRepo);
    }
}
