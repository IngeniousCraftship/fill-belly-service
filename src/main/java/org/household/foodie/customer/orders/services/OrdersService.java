package org.household.foodie.customer.orders.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.household.foodie.customer.exceptions.CustomerNotFoundException;
import org.household.foodie.customer.exceptions.UniqueOrderException;
import org.household.foodie.customer.orders.EdibleLineRepository;
import org.household.foodie.customer.orders.Order;
import org.household.foodie.customer.orders.OrderStatus;
import org.household.foodie.customer.orders.OrdersRepository;
import org.household.foodie.customer.registrations.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Component
public class OrdersService {

    private OrdersRepository ordersRepository;

    private EdibleLineRepository edibleLineRepository;

    private CustomersRepository customersRepository;

    public static final int ORDER_NUMBER_LENGTH = 7;

    @Autowired
    public OrdersService(OrdersRepository ordersRepository, EdibleLineRepository edibleLineRepository,
                         CustomersRepository customersRepository) {
        this.ordersRepository = ordersRepository;
        this.edibleLineRepository = edibleLineRepository;
        this.customersRepository = customersRepository;
    }

    @Transactional
    public String create(Order order) {
        if(customersRepository.findByEmail(order.getEmail())==null) {
            throw new CustomerNotFoundException(String.format("Unable to find a registered customer with this " +
                    "email %s, cannot take order", order.getEmail()));
        }
        try {
            assignOrderNumber(order);
            order.setOrderStatus(OrderStatus.NEW.name());
            ordersRepository.save(order);
            edibleLineRepository.save(order.getEdibleLines());
            return order.getOrderNumber();
        } catch (DataIntegrityViolationException e) {
            throw new UniqueOrderException("Order already exists with this order number and email");
        }
    }

    public boolean updateOrderPickUpDateTime(Order order) {
        Date pickUpDate = order.getOrderPickUpDateTime()!=null ? order.getOrderPickUpDateTime()
                : new Date();
        return ordersRepository.updateOrderPickUpDateTime(order.getOrderNumber(), order.getEmail(), pickUpDate) == 1 ?
                true : false;
    }

    public Order findOrder(String orderNumber) {
        final Order order = ordersRepository.findByOrderNumber(orderNumber);
        if (order!=null && order.getId() != null) {
            order.setEdibleLines(edibleLineRepository.findByOrderNumber(orderNumber));
        }
        return order;
    }

    public List<Order> findOrderByStatus(String status) {
        try{ OrderStatus.valueOf(status); } catch (IllegalArgumentException e) {
            return null;
        }
        final List<Order> orders = ordersRepository.findByOrderStatus(status);
        fillOrderLineDetails(orders);
        return orders;
    }

    public List<Order> findOrder(Date orderDate) {
        final List<Order> orders = ordersRepository.findByOrderDateEquals(orderDate);
        fillOrderLineDetails(orders);
        return orders;
    }

    public List<Order> findOrder(Date orderDate, String status) {
        final List<Order> orders = ordersRepository.findByOrderDateEqualsAndOrderStatusEquals(orderDate, status);
        fillOrderLineDetails(orders);
        return orders;
    }

    private void fillOrderLineDetails(List<Order> orders) {
        if(CollectionUtils.isEmpty(orders)) {
            return;
        }
        orders.forEach(order -> order.setEdibleLines(edibleLineRepository.findByOrderNumber(order.getOrderNumber())));
    }

    private void assignOrderNumber(Order order) {
        final String orderNumber = RandomStringUtils.randomAlphanumeric(ORDER_NUMBER_LENGTH).toUpperCase().trim().intern();
        order.setOrderNumber(orderNumber);
        order.getEdibleLines().forEach(line -> line.setOrderNumber(orderNumber));
    }
}
