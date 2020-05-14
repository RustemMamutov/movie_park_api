package ru.api.moviepark.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class RPSHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        int rps = RpsCalculatorUtil.getRps();
        if (rps > 150) {
            return Health.down()
                    .withDetail("RPS current: ", rps)
                    .build();
        } else {
            return Health.up()
                    .withDetail("RPS current: ", rps)
                    .build();
        }
    }
}