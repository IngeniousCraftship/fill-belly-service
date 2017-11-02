package org.household.foodie.customer.orders;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

public interface OrdersRepository extends CrudRepository<Order, Long> {

    Order findByOrderNumber(String orderNumber);

    List<Order> findByEmail(String email);

    List<Order> findByOrderDateEquals(@Param("orderDate") @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                 Date
            orderDate);

    List<Order> findByOrderDateEqualsAndOrderStatusEquals(@Param("orderDate")
//                                                          @DateTimeFormat(pattern = "yyyy-MM-dd")
//                                                            @Temporal(TemporalType.DATE)
                                                          Date orderDate,
                                                          @Param("orderStatus")  String orderStatus);

    //List<Order> findByOrderPickUpDateTimeIsNull();

    List<Order> findByOrderStatus(String status);

    //List<Order> findByOrderPickUpDateTimeIsNullAndOrderCreationDateEquals(Date orderCreationDate);

    @Modifying
    @Query("update Order set orderPickUpDateTime = :orderPickUpDateTime where orderNumber=:orderNumber and email = " +
            ":email")
    Integer updateOrderPickUpDateTime(@Param("orderNumber") String orderNumber, @Param("email") String email,
                                      @DateTimeFormat(pattern = "yyyy-MM-dd")
                                      @Param("orderPickUpDateTime") Date orderPickUpDateTime);

}
