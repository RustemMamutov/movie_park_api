package ru.api.moviepark;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.api.moviepark.services.DBPostgreService;
import ru.api.moviepark.services.valueobjects.PlaceInfo;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MovieParkApplicationTests {

	@Autowired
	private DBPostgreService service;

	@Test
	public void test(){
		service.createTablesForAllMissingSeancesTodayAndTomorrow();
	}

//	@Test
	public void test1(){
		service.deleteOldSeanceTablesBeforeToday();
	}


}

