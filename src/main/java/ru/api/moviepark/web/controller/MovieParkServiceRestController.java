package ru.api.moviepark.web.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.api.moviepark.actuator.RpsCalculatorUtil;
import ru.api.moviepark.service.cache.SeancePlacesTtlCache;

@RestController
@Slf4j
@RequestMapping("/movie-park-service")
public class MovieParkServiceRestController {

    @GetMapping("/change-cache-ttl")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void changeCacheTtl(@RequestParam String ttl) {
        SeancePlacesTtlCache.setCacheLifeTime(Integer.parseInt(ttl));
    }

    @GetMapping("/get-rps-statistics")
    public ObjectNode getRpsStatistics() {
        return RpsCalculatorUtil.getRpsStatistics();
    }
}
