package ru.t1.java.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "client_id", nullable = false, unique = true)
    private String clientId = String.valueOf(UUID.randomUUID());

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Client client)) return false;
        return Objects.equals(id, client.id) && Objects.equals(firstName, client.firstName) && Objects.equals(lastName, client.lastName) && Objects.equals(middleName, client.middleName) && Objects.equals(clientId, client.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, middleName, clientId);
    }
}