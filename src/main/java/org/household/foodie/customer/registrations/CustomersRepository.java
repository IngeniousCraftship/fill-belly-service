package org.household.foodie.customer.registrations;

import org.springframework.data.repository.CrudRepository;

public interface CustomersRepository extends CrudRepository<Customer, Long> {

    Customer findByEmail(String email);

    Customer findByPhoneNumber(Long phoneNumber);


}
