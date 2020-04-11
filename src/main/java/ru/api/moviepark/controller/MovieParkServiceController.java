package ru.api.moviepark.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.api.moviepark.actuator.RpsCalculatorUtil;
import ru.api.moviepark.service.cache.SeancePlacesTtlCache;

import static ru.api.moviepark.controller.CommonResponse.ERROR;
import static ru.api.moviepark.controller.CommonResponse.VALID_DATA;

@RestController
@Slf4j
@RequestMapping("/movie-park-service")
public class MovieParkServiceController {

    @GetMapping("/change-cache-ttl/{ttl}")
    public CommonResponse changeCacheTtl(@PathVariable String ttl) {
        try {
            SeancePlacesTtlCache.setCacheLifeTime(Integer.parseInt(ttl));
            return VALID_DATA;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ERROR;
        }
    }

    @GetMapping("/get-rps-statistics")
    public ObjectNode getRpsStatistics() {
        return RpsCalculatorUtil.getRpsStatistics();
    }
}
