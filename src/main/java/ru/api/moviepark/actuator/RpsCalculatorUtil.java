package ru.api.moviepark.actuator;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import ru.api.moviepark.env.MovieParkEnv;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class RpsCalculatorUtil {

    private final static Map<Long, Integer> rpsCountMap = new ConcurrentSkipListMap<>();
    private static final AtomicInteger currentRps = new AtomicInteger(0);

    private static MovieParkEnv env;

    public static void startRpsTimeoutFlushProcess(MovieParkEnv env) {
        RpsCalculatorUtil.env = env;
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread th = new Thread(r);
                th.setDaemon(true);
                return th;
            }
        });

        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                log.debug("Start flushing rps statistics. Rps map size BEFORE flush: {}", rpsCountMap.size());
                long currentSecond = System.currentTimeMillis() / 1000;
                for (long timeMoment : rpsCountMap.keySet()) {
                    if (timeMoment < currentSecond - env.getRpsLifeTime()) {
                        log.debug("Remove element by second: {}", timeMoment);
                        rpsCountMap.remove(timeMoment);
                    }
                }
                log.debug("Finish flushing rps statistics. Rps map size AFTER flush: {}", rpsCountMap.size());
            }
        }, 1, env.getRpsMapFlushTimeout(), SECONDS);
    }

    public synchronized static void incrRps() {
        long currentSecond = System.currentTimeMillis() / 1000;
        if (!rpsCountMap.containsKey(currentSecond)) {
            rpsCountMap.put(currentSecond, 0);
            currentRps.set(0);
        }
        rpsCountMap.put(currentSecond, currentRps.incrementAndGet());
    }

    static int getRps() {
        long previousSecond = System.currentTimeMillis() / 1000 - 1;
        return rpsCountMap.getOrDefault(previousSecond, 0);
    }

    public static ObjectNode getRpsStatistics() {
        ObjectNode answer = JsonNodeFactory.instance.objectNode();
        long previousSecond = System.currentTimeMillis() / 1000 - 1;
        int totalCount = 0;
        for (Map.Entry<Long, Integer> entry : rpsCountMap.entrySet()) {
            if (previousSecond - env.getRpsLifeTime() <= entry.getKey() && entry.getKey() <= previousSecond) {
                answer.put(entry.getKey().toString(), entry.getValue());
                totalCount += entry.getValue();
            }
        }
        answer.put("store_size", rpsCountMap.size());
        answer.put("total_count", totalCount);
        return answer;
    }
}
