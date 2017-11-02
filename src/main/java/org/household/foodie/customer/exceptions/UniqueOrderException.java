package org.household.foodie.customer.exceptions;

public class UniqueOrderException extends RuntimeException {

    public UniqueOrderException(String message) {
        super(message);
    }
}
