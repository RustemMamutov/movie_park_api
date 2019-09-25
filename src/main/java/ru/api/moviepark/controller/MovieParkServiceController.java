package ru.api.moviepark.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.api.moviepark.actuator.RpsCalculatorUtil;
import ru.api.moviepark.service.dbclient.DatabaseClient;
import ru.api.moviepark.service.dbclient.RemoteDatabaseClientImpl;

import static ru.api.moviepark.controller.CommonResponse.ERROR;
import static ru.api.moviepark.controller.CommonResponse.VALID_DATA;

@Controller
@Slf4j
@RequestMapping("/movie-park-service")
public class MovieParkServiceController {

    private DatabaseClient databaseClient;

    public MovieParkServiceController(RemoteDatabaseClientImpl databaseClient) {
        this.databaseClient = databaseClient;
    }

    @GetMapping("/change-cache-ttl/{ttl}")
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

    @GetMapping("/get-rps-statistics")
    @ResponseBody
    public ObjectNode getRpsStatistics() {
        return RpsCalculatorUtil.getRpsStatistics();
    }
}
