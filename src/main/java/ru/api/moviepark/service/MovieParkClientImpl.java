package ru.api.moviepark.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.api.moviepark.data.dto.MainScheduleDTO;
import ru.api.moviepark.data.entities.HallsEntity;
import ru.api.moviepark.data.entities.MainScheduleEntity;
import ru.api.moviepark.data.entities.SeancePlacesEntity;
import ru.api.moviepark.data.repositories.HallsRepo;
import ru.api.moviepark.data.repositories.MainScheduleRepo;
import ru.api.moviepark.data.repositories.SeancesPlacesRepo;
import ru.api.moviepark.data.valueobjects.CreateSeanceInput;
import ru.api.moviepark.service.cache.MoviesInfoTtlCache;
import ru.api.moviepark.service.cache.SeanceInfoTtlCache;
import ru.api.moviepark.service.cache.SeancePlacesTtlCache;
import ru.api.moviepark.util.CheckInputUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.api.moviepark.data.entities.MainScheduleEntity.createMainScheduleEntity;
import static ru.api.moviepark.env.Constants.SCHEMA_NAME;
import static ru.api.moviepark.util.CheckInputUtil.checkCreateSeanceInput;

@Service
@Slf4j
public class MovieParkClientImpl implements MovieParkClient {

    private final JdbcTemplate jdbcTemplate;
    private final HallsRepo hallsRepo;
    private final MainScheduleRepo mainScheduleRepo;
    private final SeancesPlacesRepo seancesPlacesRepo;

    public MovieParkClientImpl(JdbcTemplate jdbcTemplate,
                               HallsRepo hallsRepo,
                               MainScheduleRepo mainScheduleRepo,
                               SeancesPlacesRepo seancesPlacesRepo) {
        this.jdbcTemplate = jdbcTemplate;
        this.hallsRepo = hallsRepo;
        this.mainScheduleRepo = mainScheduleRepo;
        this.seancesPlacesRepo = seancesPlacesRepo;
    }

    @Override
    public MainScheduleDTO getSeanceById(int seanceId) {
        CheckInputUtil.checkSeanceIdExist(seanceId);
        MainScheduleDTO result = SeanceInfoTtlCache.getSeanceByIdFromCache(seanceId);
        if (result == null) {
            List<MainScheduleEntity> entities = mainScheduleRepo.findAllSeancesInTheSameDate(seanceId);
            SeanceInfoTtlCache.convertToDtoAndAddToCache(entities);
            result = SeanceInfoTtlCache.getSeanceByIdFromCache(seanceId);
        }
        return result;
    }

    @Override
    public List<MainScheduleDTO> getAllSeancesByDate(LocalDate date) {
        if (SeanceInfoTtlCache.checkCacheContainsElementByDate(date)) {
            return new ArrayList<>(SeanceInfoTtlCache.getSeancesMapByDateFromCache(date).values());
        }

        List<MainScheduleEntity> entities = mainScheduleRepo.findAllBySeanceDate(date);
        List<MainScheduleDTO> result = MainScheduleEntity.convertToDtoList(entities);

        SeanceInfoTtlCache.addSeanceInfoToCache(result);
        return result;
    }

    @Override
    public List<MainScheduleDTO> getAllSeancesByPeriod(LocalDate periodStart, LocalDate periodEnd) {
        List<MainScheduleDTO> result = new ArrayList<>();
        while (periodStart.isBefore(periodEnd.plusDays(1))) {
            result.addAll(getAllSeancesByDate(periodStart));
            periodStart = periodStart.plusDays(1);
        }

        return result;
    }

    @Override
    public Map<Integer, String> getAllMoviesByDate(LocalDate date) {
        if (MoviesInfoTtlCache.containsElement(date)) {
            return MoviesInfoTtlCache.getElementByDateFromCache(date);
        }

        List<MainScheduleDTO> entities = getAllSeancesByDate(date);
        Map<Integer, String> result = new HashMap<>();
        entities.forEach(dto -> result.putIfAbsent(dto.getMovieId(), dto.getMovieName()));

        if (result.size() > 0) {
            MoviesInfoTtlCache.addElementToCache(date, result);
        }
        return result;
    }

    @Override
    public Map<LocalDate, Map<Integer, String>> getAllMoviesByPeriod(LocalDate periodStart, LocalDate periodEnd) {
        Map<LocalDate, Map<Integer, String>> result = new HashMap<>();
        while (periodStart.isBefore(periodEnd.plusDays(1))) {
            result.put(periodStart, getAllMoviesByDate(periodStart));
            periodStart = periodStart.plusDays(1);
        }
        return result;
    }

    @Override
    public Map<String, List<MainScheduleDTO>> getAllSeancesByMovieAndDateGroupByMoviePark(
            int movieId, LocalDate date) {
        Map<String, List<MainScheduleDTO>> result = new HashMap<>();

        List<MainScheduleDTO> seancesByPeriod = getAllSeancesByPeriod(date, date.plusDays(1));
        seancesByPeriod.stream()
                .filter(dto -> dto.getSeanceDate().equals(date)
                        && dto.getStartTime().isAfter(LocalTime.of(6, 0)) ||
                        dto.getSeanceDate().equals(date.plusDays(1))
                                && dto.getStartTime().isBefore(LocalTime.of(6, 0)))
                .filter(dto -> dto.getMovieId() == movieId)
                .forEach(dto -> {
                    String movieParkName = dto.getMovieParkName();
                    result.putIfAbsent(movieParkName, new ArrayList<>());
                    result.get(movieParkName).add(dto);
                });

        return result;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public synchronized void createNewSeance(CreateSeanceInput inputJson) {
        checkCreateSeanceInput(inputJson);

        MainScheduleEntity newSeanceEntity = createMainScheduleEntity(inputJson);
        SeanceInfoTtlCache.clearCacheByDate(inputJson.getDate());
        mainScheduleRepo.save(newSeanceEntity);
    }

    @Transactional
    public void updateScheduleTable(int days) {
        try {
            jdbcTemplate.execute(String.format("select %s.create_new_seances(%s);", SCHEMA_NAME, days));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    public List<HallsEntity> getHallPlacesInfo(int hallId) {
        return hallsRepo.findAllByHallId(hallId).orElse(null);
    }

    public List<SeancePlacesEntity> getSeancePlacesInfo(int seanceId) {
        log.info("Getting full info for seance id = " + seanceId);
        if (SeancePlacesTtlCache.checkCacheContainsElement(seanceId)) {
            return SeancePlacesTtlCache.getSeancePlacesInfoByIdFromCache(seanceId);
        } else {
            List<SeancePlacesEntity> seanceFullInfo = seancesPlacesRepo.findAllBySeanceId(seanceId);
            SeancePlacesTtlCache.addSeancePlacesInfoToCache(seanceId, seanceFullInfo);
            return seanceFullInfo;
        }
    }

    @Transactional
    @Override
    public synchronized void blockOrUnblockPlaceOnSeance(int seanceId, List<Integer> placeList, boolean blocked){
        CheckInputUtil.checkSeanceIdExist(seanceId);
        log.info("Updating places {} in seance {}. Set blocked value: {}", placeList, seanceId, blocked);
        seancesPlacesRepo.blockOrUnblockThePlace(seanceId, placeList, blocked);
        SeancePlacesTtlCache.removeElementFromCache(seanceId);
    }
}
