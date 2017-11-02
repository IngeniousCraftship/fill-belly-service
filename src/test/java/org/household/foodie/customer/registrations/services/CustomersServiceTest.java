package org.household.foodie.customer.registrations.services;

import org.household.foodie.customer.exceptions.UniqueCustomerException;
import org.household.foodie.customer.registrations.Customer;
import org.household.foodie.customer.registrations.CustomersRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;

@RunWith(MockitoJUnitRunner.class)
public class CustomersServiceTest {

    @Mock
    private CustomersRepository customersRepository;

    @InjectMocks
    private CustomersService customersService;

    @Test
    public void shouldRegisterCustomerSuccessfullyForValidData() {
        Customer customer = Customer.builder().email("email@email.com").firstName("firstName").lastName("lastName")
                .houseNumber(1l).phoneNumber(5105105100l).build();
        Mockito.when(customersRepository.save(customer)).thenReturn(customer);
        Assert.assertTrue("email@email.com".equals(customersService.register(customer)));
    }

    @Test
    public void shouldRaiseUniqueCustomerExceptionForIntegrityViolationExceptionsForExistingCustomer() {
        Mockito.doThrow(DataIntegrityViolationException.class).when(customersRepository).save(Mockito.any(Customer
                .class));
        try {
            customersService.register(Customer.builder().email("email@email.com").build());
            Assert.fail();
        } catch (UniqueCustomerException e) {
            Assert.assertEquals("This email email@email.com is already associated to a customer, " +
                    "please use another one", e.getMessage());
        }

    }
}
