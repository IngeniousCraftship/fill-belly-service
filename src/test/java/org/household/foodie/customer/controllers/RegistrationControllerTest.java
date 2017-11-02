package org.household.foodie.customer.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.household.foodie.customer.exceptions.UniqueCustomerException;
import org.household.foodie.customer.registrations.Customer;
import org.household.foodie.customer.registrations.services.CustomersService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@RunWith(SpringRunner.class)
@WebMvcTest(RegistrationController.class)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CustomersService customersService;

    private static final String EMAIL = "EMAIL@EMAIL.com";

    @Test
    public void given_a_valid_request_registering_customer_should_be_successful() throws Exception {
        Customer customer = Customer.builder().email(EMAIL).firstName("firstName").lastName("lastName")
                .houseNumber(1l).phoneNumber(5105105100l).build();
        BDDMockito.given(customersService.register(customer)).willReturn(EMAIL);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(NON_NULL);
       String responseBody = mvc.perform(post("/customers/registrations").contentType
                (
                        APPLICATION_JSON)
                .content(mapper.writeValueAsString(customer))).andExpect(MockMvcResultMatchers.status().isCreated())
               .andReturn().getResponse().getContentAsString();
        assertEquals(EMAIL, responseBody);
        verify(customersService, times(1)).register(customer);
        reset(customersService);
    }

    @Test
    public void given_a_request_with_existing_customer_details_registering_customer_should_return_bad_request() throws
            Exception {
        Customer customer = Customer.builder().email(EMAIL).firstName("firstName").lastName("lastName")
                .houseNumber(1l).phoneNumber(5105105100l).build();
        final String message = "This email EMAIL@EMAIL.com is already associated to a customer, " +
                "please use another one";
        UniqueCustomerException uniqueCustomerException = new UniqueCustomerException(message);
        BDDMockito.given(customersService.register(customer)).willThrow(uniqueCustomerException);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(NON_NULL);
        String responseBody = mvc.perform(post("/customers/registrations").contentType
                (
                        APPLICATION_JSON)
                .content(mapper.writeValueAsString(customer))).andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        assertEquals(message, responseBody);
        verify(customersService, times(1)).register(customer);
        reset(customersService);
    }

}
