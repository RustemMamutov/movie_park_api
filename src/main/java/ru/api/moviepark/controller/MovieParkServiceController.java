package ru.api.moviepark.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.api.moviepark.data.dbclient.DatabaseClient;
import ru.api.moviepark.data.dbclient.RemoteDatabaseClientImpl;

import static ru.api.moviepark.controller.CommonResponse.ERROR;
import static ru.api.moviepark.controller.CommonResponse.VALID_DATA;

@Controller
@Slf4j
@RequestMapping("/movie_park_service")
public class MovieParkServiceController {

    private DatabaseClient databaseClient;

    public MovieParkServiceController(RemoteDatabaseClientImpl databaseClient) {
        this.databaseClient = databaseClient;
    }

    @GetMapping("/change_cache_ttl/{ttl}")
    @ResponseBody
    public CommonResponse changeCacheTtl(@PathVariable String ttl) {
        try {
            databaseClient.changeCacheLifeTime(Integer.parseInt(ttl));
            return VALID_DATA;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ERROR;
        }
    }
}
