package ru.api.moviepark.actuator;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.api.moviepark.config.MovieParkEnvironment;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

public class RpsCalculatorUtil {

    private final static Map<Long, Integer> rpsCountMap = new ConcurrentSkipListMap<>();
    private static AtomicInteger currentRps = new AtomicInteger(0);

    private static Logger logger = LoggerFactory.getLogger(RpsCalculatorUtil.class);

    private static MovieParkEnvironment env;

    public static void startRpsTimeoutFlushProcess(MovieParkEnvironment env) {
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
                logger.debug("Start flushing rps statistics. Rps map size BEFORE flush: {}", rpsCountMap.size());
                long currentSecond = System.currentTimeMillis()/1000;
                for (long timeMoment : rpsCountMap.keySet()) {
                    if (timeMoment < currentSecond - env.getRpsLifeTime()) {
                        logger.debug("Remove element by second: {}", timeMoment);
                        rpsCountMap.remove(timeMoment);
                    }
                }
                logger.debug("Finish flushing rps statistics. Rps map size AFTER flush: {}", rpsCountMap.size());
            }
        }, 1, env.getRpsMapFlushTimeout(), SECONDS);
    }

    public synchronized static void incrRps(){
        long currentSecond = System.currentTimeMillis()/1000;
        if (!rpsCountMap.containsKey(currentSecond)){
            rpsCountMap.put(currentSecond, 0);
            currentRps.set(0);
        }
        rpsCountMap.put(currentSecond, currentRps.incrementAndGet());
    }

    static int getRps(){
        long previousSecond = System.currentTimeMillis()/1000 - 1;
        return rpsCountMap.getOrDefault(previousSecond, 0);
    }

    public static ObjectNode getRpsStatistics() {
        ObjectNode answer = JsonNodeFactory.instance.objectNode();
        long previousSecond = System.currentTimeMillis()/1000 - 1;
        int totalCount = 0;
        for (Map.Entry<Long, Integer> entry : rpsCountMap.entrySet()){
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
