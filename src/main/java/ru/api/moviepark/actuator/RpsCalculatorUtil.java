package ru.api.moviepark.actuator;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RpsCalculatorUtil {

    private final static Map<Long, Integer> rpsCountMap = new ConcurrentSkipListMap<>();
    private static AtomicInteger currentRps = new AtomicInteger(0);

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
            if (previousSecond - 120 <= entry.getKey() && entry.getKey() <= previousSecond) {
                answer.put(entry.getKey().toString(), entry.getValue());
                totalCount += entry.getValue();
            }
        }
        answer.put("total_count", totalCount);
        return answer;
    }
}
