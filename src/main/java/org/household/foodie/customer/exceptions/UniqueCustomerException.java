package org.household.foodie.customer.exceptions;

public class UniqueCustomerException extends RuntimeException {

    public UniqueCustomerException(String message) {
        super(message);
    }
}
