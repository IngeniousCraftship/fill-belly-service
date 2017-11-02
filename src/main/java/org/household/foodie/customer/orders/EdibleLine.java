package org.household.foodie.customer.orders;

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
@Table(name = "order_lines")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class EdibleLine {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonIgnore
    private Long id;

    @Column(nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private Integer quantity;

    private String itemName;

    @Column(nullable = false)
    private Double unitPrice;

    private Double lineTotal;

}
