package org.household.foodie.customer.orders.services;

import org.household.foodie.customer.exceptions.CustomerNotFoundException;
import org.household.foodie.customer.exceptions.UniqueOrderException;
import org.household.foodie.customer.orders.EdibleLine;
import org.household.foodie.customer.orders.EdibleLineRepository;
import org.household.foodie.customer.orders.Order;
import org.household.foodie.customer.orders.OrderStatus;
import org.household.foodie.customer.orders.OrdersRepository;
import org.household.foodie.customer.registrations.Customer;
import org.household.foodie.customer.registrations.CustomersRepository;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrdersServiceTest {

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private EdibleLineRepository edibleLineRepository;

    @Mock
    private CustomersRepository customersRepository;

    @InjectMocks
    private OrdersService ordersService;

    private static final String EMAIL = "email@email.com";

    private static final String ORDER_NUMBER = "A";

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    @Test
    public void shouldCreateOrderSuccessfullyForAnExistingCustomer() {
        when(customersRepository.findByEmail(EMAIL)).thenReturn(Customer.builder().build());
        EdibleLine edibleLine = EdibleLine.builder().build();
        final List<EdibleLine> edibleLines = Collections.singletonList(edibleLine);
        Order order = Order.builder().email(EMAIL).edibleLines(edibleLines).build();
        when(ordersRepository.save(order)).thenReturn(order);
        when(edibleLineRepository.save(edibleLines)).thenReturn(edibleLines);
        final String orderNumber = ordersService.create(order);
        assertEquals(orderNumber.length(), OrdersService.ORDER_NUMBER_LENGTH);
        assertEquals(orderNumber, order.getOrderNumber());
        assertEquals(OrderStatus.NEW.name(), order.getOrderStatus());
        assertEquals(orderNumber, edibleLine.getOrderNumber());
    }

    @Test
    public void shouldNotCreateOrderForANonExistingCustomer() {
        when(customersRepository.findByEmail(EMAIL)).thenReturn(null);
        Order order = Order.builder().email(EMAIL).build();
        thrown.expect(CustomerNotFoundException.class);
        thrown.expectMessage(String.format("Unable to find a registered customer with this " +
                "email %s, cannot take order", EMAIL));
        ordersService.create(order);
    }

    @Test
    public void shouldNotCreateOrderForAnExistingDataMatch() {
        when(customersRepository.findByEmail(EMAIL)).thenReturn(Customer.builder().build());
        EdibleLine edibleLine = EdibleLine.builder().build();
        final List<EdibleLine> edibleLines = Collections.singletonList(edibleLine);
        Order order = Order.builder().email(EMAIL).edibleLines(edibleLines).build();
        when(ordersRepository.save(order)).thenThrow(DataIntegrityViolationException.class);
        thrown.expect(UniqueOrderException.class);
        thrown.expectMessage("Order already exists with this order number and email");
        ordersService.create(order);
    }

    @Test
    public void shouldUpdateOrderPickUpDateTimeForAValidOrder() {
        Date date = new Date();
        Order order = Order.builder().email(EMAIL).orderNumber(ORDER_NUMBER).orderPickUpDateTime(date).build();
        when(ordersRepository.updateOrderPickUpDateTime(ORDER_NUMBER,EMAIL, date)).thenReturn(1);
        Assert.assertTrue(ordersService.updateOrderPickUpDateTime(order));
    }

    @Test
    public void shouldUpdateOrderPickUpDateTimeForAInValidOrder() {
        Order order = Order.builder().email(EMAIL).orderNumber(ORDER_NUMBER).build();
        when(ordersRepository.updateOrderPickUpDateTime(eq(ORDER_NUMBER),eq(EMAIL), any(Date.class))).thenReturn
                (0);
        Assert.assertFalse(ordersService.updateOrderPickUpDateTime(order));
    }

    @Test
    public void shouldReturnValidDataForExistingOrderNumber() {
        EdibleLine edibleLine = EdibleLine.builder().orderNumber(ORDER_NUMBER).build();
        final List<EdibleLine> edibleLines = Collections.singletonList(edibleLine);
        Order order = Order.builder().email(EMAIL).id(1l).orderNumber(ORDER_NUMBER).build();
        when(ordersRepository.findByOrderNumber(ORDER_NUMBER)).thenReturn(order);
        when(edibleLineRepository.findByOrderNumber(ORDER_NUMBER)).thenReturn(edibleLines);
        Order actualOrder = ordersService.findOrder(ORDER_NUMBER);
        Assert.assertEquals(order.getEdibleLines(), actualOrder.getEdibleLines());
    }

    @Test
    public void shouldReturnNoDataForNonExistingOrderNumber() {
        when(ordersRepository.findByOrderNumber(ORDER_NUMBER)).thenReturn(null);
        Order actualOrder = ordersService.findOrder(ORDER_NUMBER);
        Assert.assertNull(actualOrder);
        Mockito.verifyZeroInteractions(edibleLineRepository);
    }

    @Test
    public void shouldReturnOrdersForExistingOrderStatus() {
        EdibleLine edibleLine = EdibleLine.builder().orderNumber(ORDER_NUMBER).build();
        final List<EdibleLine> edibleLines = Collections.singletonList(edibleLine);
        Order order = Order.builder().email(EMAIL).id(1l).orderNumber(ORDER_NUMBER).orderStatus(OrderStatus.NEW.name())
                .build();
        final List<Order> orders = Collections.singletonList(order);
        when(ordersRepository.findByOrderStatus(OrderStatus.NEW.name())).thenReturn(orders);
        when(edibleLineRepository.findByOrderNumber(ORDER_NUMBER)).thenReturn(edibleLines);
        List<Order> actualOrdres = ordersService.findOrderByStatus(OrderStatus.NEW.name());
        Assert.assertEquals(orders, actualOrdres);
        verify(ordersRepository, times(1)).findByOrderStatus(OrderStatus.NEW.name());
        verify(edibleLineRepository, times(1)).findByOrderNumber(ORDER_NUMBER);
    }

    @Test
    public void shouldReturnNullForInvalidOrderStatus() {
        List<Order> actualOrdres = ordersService.findOrderByStatus("DUMMY");
        Assert.assertNull(actualOrdres);
    }

    @Test
    public void shouldReturnNullForCaseWhereNoOrdersExistsForAValidStatus() {
        when(ordersRepository.findByOrderStatus(OrderStatus.NEW.name())).thenReturn(null);
        List<Order> actualOrdres = ordersService.findOrderByStatus(OrderStatus.NEW.name());
        Assert.assertNull(actualOrdres);
    }

    @Test
    public void shouldReturnOrdersForAGivenDate() {
        Date date = new Date();
        EdibleLine edibleLine = EdibleLine.builder().orderNumber(ORDER_NUMBER).build();
        final List<EdibleLine> edibleLines = Collections.singletonList(edibleLine);
        Order order = Order.builder().email(EMAIL).id(1l).orderNumber(ORDER_NUMBER).orderDate(date).orderStatus
                (OrderStatus.NEW
                .name())
                .build();
        final List<Order> orders = Collections.singletonList(order);
        when(ordersRepository.findByOrderDateEquals(date)).thenReturn(orders);
        when(edibleLineRepository.findByOrderNumber(ORDER_NUMBER)).thenReturn(edibleLines);
        List<Order> actualOrdres = ordersService.findOrder(date);
        Assert.assertEquals(orders, actualOrdres);
        verify(ordersRepository, times(1)).findByOrderDateEquals(date);
        verify(edibleLineRepository, times(1)).findByOrderNumber(ORDER_NUMBER);
    }

    @Test
    public void shouldReturnOrdersForAGivenDateAndStatus() {
        Date date = new Date();
        EdibleLine edibleLine = EdibleLine.builder().orderNumber(ORDER_NUMBER).build();
        final List<EdibleLine> edibleLines = Collections.singletonList(edibleLine);
        Order order = Order.builder().email(EMAIL).id(1l).orderNumber(ORDER_NUMBER).orderDate(date).orderStatus
                (OrderStatus.NEW
                        .name())
                .build();
        final List<Order> orders = Collections.singletonList(order);
        when(ordersRepository.findByOrderDateEqualsAndOrderStatusEquals(date, OrderStatus.NEW.name())).thenReturn(orders);
        when(edibleLineRepository.findByOrderNumber(ORDER_NUMBER)).thenReturn(edibleLines);
        List<Order> actualOrdres = ordersService.findOrder(date, OrderStatus.NEW.name());
        Assert.assertEquals(orders, actualOrdres);
        verify(ordersRepository, times(1)).findByOrderDateEqualsAndOrderStatusEquals(date, OrderStatus.NEW.name());
        verify(edibleLineRepository, times(1)).findByOrderNumber(ORDER_NUMBER);
    }
}

