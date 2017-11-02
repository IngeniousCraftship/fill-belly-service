package org.household.foodie.customer.controllers;

import org.household.foodie.customer.orders.Order;
import org.household.foodie.customer.orders.OrderStatus;
import org.household.foodie.customer.orders.services.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
public class OrdersController {

    private OrdersService ordersService;

    @Autowired
    public OrdersController(OrdersService ordersService) {
        this.ordersService = ordersService;
    }

    @PostMapping(path = "/customers/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public String createOrder(@RequestBody Order order) {
        return ordersService.create(order);
    }

    @PatchMapping(path = "/customers/orders")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void patchOrder(@RequestBody Order order) {
        stampPickUpTimeForCompleteOrders(order);
    }

    private void stampPickUpTimeForCompleteOrders(@RequestBody Order order) {
        if(OrderStatus.COMPLETE.name().equals(order.getOrderStatus())) {
            ordersService.updateOrderPickUpDateTime(order);
        }
    }

    @GetMapping(path = "/customers/orders/{orderNumber}")
    @ResponseStatus(HttpStatus.OK)
    public Order identifyOrder(@PathVariable(name = "orderNumber") String orderNumber) {
        return ordersService.findOrder(orderNumber);
    }

    @GetMapping(path = "/customers/orders", params = {"orderDate","status"})
    @ResponseStatus(HttpStatus.OK)
    public List<Order> identifyOrders(@RequestParam(name = "orderDate")
                                                @DateTimeFormat(pattern = "yyyy-MM-dd") Date orderDate,
                                            @RequestParam(name = "status") String status) {
        return ordersService.findOrder(orderDate, status);
    }

    @GetMapping(path = "/customers/orders", params = {"orderDate"})
    @ResponseStatus(HttpStatus.OK)
    public List<Order> identifyOrders(@RequestParam(name = "orderDate")
                                          @DateTimeFormat(pattern = "yyyy-MM-dd") Date orderDate) {
        return ordersService.findOrder(orderDate);
    }

    @GetMapping(path = "/customers/orders", params = {"status"})
    @ResponseStatus(HttpStatus.OK)
    public List<Order> getOrders(@RequestParam(name = "status")
                                      String status) {
        return ordersService.findOrderByStatus(status);
    }




}
