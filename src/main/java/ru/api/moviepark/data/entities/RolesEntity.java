package ru.api.moviepark.data.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import static ru.api.moviepark.env.Constants.ROLES_TABLE_NAME;
import static ru.api.moviepark.env.Constants.SCHEMA_NAME;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = ROLES_TABLE_NAME, schema = SCHEMA_NAME)
public class RolesEntity {

    @Id
    private Integer id;

    private String description;

    private String permissions;
}
