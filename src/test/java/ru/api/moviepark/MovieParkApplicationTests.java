package ru.api.moviepark;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.api.moviepark.controller.valueobjects.CreateSeanceInputJson;
import ru.api.moviepark.services.DBPostgreService;

import java.time.LocalDate;
import java.time.LocalTime;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MovieParkApplicationTests {

	@Autowired
	private DBPostgreService service;

//	@Test
	public void test(){
		service.createTablesForAllMissingSeancesTodayAndTomorrow();
	}

//	@Test
	public void test1(){
		service.deleteOldSeanceTablesBeforeToday();
	}

	@Test
	public void test2(){
		CreateSeanceInputJson inputJson = CreateSeanceInputJson.builder()
				.date(LocalDate.of(2019, 01, 05))
				.basePrice(100)
				.startTime(LocalTime.of(7, 00))
				.endTime(LocalTime.of(8, 50))
				.hallId(1)
				.movieId(1)
				.build();
		service.addSeance(inputJson);
	}
}

