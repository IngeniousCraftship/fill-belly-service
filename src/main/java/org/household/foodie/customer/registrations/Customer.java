package org.household.foodie.customer.registrations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "customers")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonIgnore
    private Long id;

    private String firstName, lastName;

    @Column(unique = true)
    private String email;

    private Long phoneNumber;
    private Long houseNumber;
}
