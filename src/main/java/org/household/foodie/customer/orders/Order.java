package org.household.foodie.customer.orders;

import com.fasterxml.jackson.annotation.JsonFormat;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonIgnore
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    @Transient
    private List<EdibleLine> edibleLines;

    @Column(nullable = false)
    private Double orderTotal;
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date orderDate;
    private Date orderPickUpDateTime;
    private String firstName;
    @Column(nullable = false)
    private String email;

    private String orderStatus;

}
