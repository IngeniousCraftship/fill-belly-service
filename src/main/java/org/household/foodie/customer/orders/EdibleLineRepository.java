package org.household.foodie.customer.orders;

import org.springframework.data.repository.CrudRepository;

import java.util.List;


public interface EdibleLineRepository  extends CrudRepository<EdibleLine, Long> {
    List<EdibleLine> findByOrderNumber(String orderNumber);
}
