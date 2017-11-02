package org.household.foodie.customer.registrations.services;

import org.household.foodie.customer.registrations.Customer;
import org.household.foodie.customer.registrations.CustomersRepository;
import org.household.foodie.customer.exceptions.UniqueCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class CustomersService {

    private CustomersRepository customersRepository;

    @Autowired
    public CustomersService(CustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
    }

    public String register(Customer customer) {
        try {
            customer.setId(null);
            customersRepository.save(customer);
            return customer.getEmail();
        } catch (DataIntegrityViolationException e) {
            throw new UniqueCustomerException(String.format("This email %s is already associated to a customer, " +
                    "please use another one",customer.getEmail()));
        }
    }
}
