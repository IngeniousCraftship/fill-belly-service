package org.household.foodie.customer.controllers;

import org.household.foodie.customer.exceptions.UniqueOrderException;
import org.household.foodie.customer.exceptions.UniqueCustomerException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class CustomersControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> dataErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        return new ResponseEntity<>("Problem dealing with data "+e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UniqueCustomerException.class)
    public ResponseEntity<String> invalidCustomerDataErrorHandler(HttpServletRequest req, Exception e) throws
            Exception {
        return badRequest(e);
    }

    private ResponseEntity<String> badRequest(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UniqueOrderException.class)
    public ResponseEntity<String> invalidOrderDataErrorHandler(HttpServletRequest req, Exception e) throws Exception {
        return badRequest(e);
    }

}
