package ru.api.moviepark.data.remote;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.api.moviepark.data.RemoteDatabaseClient;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;

import java.time.LocalDate;
import java.time.LocalTime;

@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
public class RemoteDatabaseClientTest {

    @Autowired
    private RemoteDatabaseClient remoteDatabaseClient;

    public RemoteDatabaseClientTest() {
    }

    @Test
    public void getAllSeances() {
    }

    @Test
    public void getAllSeancesForDate() {
    }

    @Transactional
    @Test
    public void createNewSeance() {
        CreateSeanceInput input = CreateSeanceInput.builder()
                .date(LocalDate.now().plusDays(365))
                .startTime(LocalTime.of(0, 0))
                .endTime(LocalTime.of(0, 0))
                .movieId(1)
                .hallId(1)
                .basePrice(100)
                .build();
        remoteDatabaseClient.createNewSeance(input);
    }

    @Test
    public void fillScheduleTableForDate() {
    }

    @Test
    public void getSeanceFullInfo() {
    }

    @Test
    public void blockOrUnblockPlaceOnSeance() {
    }
}