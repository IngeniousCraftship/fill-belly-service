package org.household.foodie.customer.controllers;

import org.household.foodie.customer.registrations.Customer;
import org.household.foodie.customer.registrations.services.CustomersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    private CustomersService customersService;

    @Autowired
    public RegistrationController(CustomersService customersService) {
        this.customersService = customersService;
    }

    @PostMapping(path = "/customers/registrations")
    @ResponseStatus(HttpStatus.CREATED)
    public String register(@RequestBody Customer customer) {
        return customersService.register(customer);
    }

}
