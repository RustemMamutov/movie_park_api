package ru.api.moviepark.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import static ru.api.moviepark.env.Constants.SCHEMA_NAME;
import static ru.api.moviepark.env.Constants.USER_CREDENTIALS_TABLE_NAME;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
@Table(name = USER_CREDENTIALS_TABLE_NAME, schema = SCHEMA_NAME)
public class UserCredentialEntity {
    @Id
    @Column(nullable = false)
    private String email;
    private String password;

    @ManyToOne
    @JoinColumn(name = "role", referencedColumnName = "id")
    private RolesEntity rolesEntity;
}
