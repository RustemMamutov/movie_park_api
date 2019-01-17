package ru.api.moviepark;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.api.moviepark.entities_valueobjects.CreateSeanceInput;
import ru.api.moviepark.services.DBPostgreService;

import java.time.LocalDate;
import java.time.LocalTime;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MovieParkApplicationTests {

	@Autowired
	private DBPostgreService service;

	@Test
	public void test(){
		service.getSeanceFullInfo(81);
		System.out.println();
	}

//	@Test
	public void test2(){
		CreateSeanceInput inputJson = CreateSeanceInput.builder()
				.date(LocalDate.of(2019, 1, 5))
				.startTime(LocalTime.of(7, 0))
				.endTime(LocalTime.of(8, 50))
				.movieId(1)
				.hallId(1)
				.basePrice(100)
				.build();
		service.addSeance(inputJson);
	}
}

