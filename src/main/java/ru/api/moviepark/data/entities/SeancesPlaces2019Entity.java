package ru.api.moviepark.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.api.moviepark.data.entities.id.SeancesPlaces2019Id;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
@Table(name = "seances_places_2019", schema = "movie_park")
public class SeancesPlaces2019Entity {
    @EmbeddedId
    SeancesPlaces2019Id id;

    @Column(name = "is_blocked")
    Boolean blocked;
}
