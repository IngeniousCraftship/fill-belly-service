package org.household.foodie.customer.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.household.foodie.customer.exceptions.UniqueOrderException;
import org.household.foodie.customer.orders.EdibleLine;
import org.household.foodie.customer.orders.Order;
import org.household.foodie.customer.orders.OrderStatus;
import org.household.foodie.customer.orders.services.OrdersService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.household.foodie.customer.orders.OrderStatus.NEW;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(OrdersController.class)
public class OrdersControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private OrdersService ordersService;

    private static final String ORDER_NUMBER = "A";

    @Test
    public void should_process_a_valid_order_request() throws Exception {
        EdibleLine edibleLine = EdibleLine.builder().build();
        final List<EdibleLine> edibleLines = Collections.singletonList(edibleLine);
        Order order = Order.builder().email("email@email.com").build();
        given(ordersService.create(order)).willReturn(ORDER_NUMBER);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(NON_NULL);
        String responseBody = mvc.perform(post("/customers/orders").contentType
                (
                        APPLICATION_JSON)
                .content(mapper.writeValueAsString(order))).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        assertEquals(ORDER_NUMBER, responseBody);
        verify(ordersService, times(1)).create(order);
        reset(ordersService);
    }

    @Test
    public void should_return_bad_request_for_an_existing_order_creation() throws Exception {
        EdibleLine edibleLine = EdibleLine.builder().build();
        final List<EdibleLine> edibleLines = Collections.singletonList(edibleLine);
        Order order = Order.builder().email("email@email.com").build();
        final String message = "Order already exists with this order number and email";
        UniqueOrderException uniqueOrderException = new UniqueOrderException(message);
        given(ordersService.create(order)).willThrow(uniqueOrderException);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(NON_NULL);
        String responseBody = mvc.perform(post("/customers/orders").contentType
                (
                        APPLICATION_JSON)
                .content(mapper.writeValueAsString(order))).andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
        assertEquals(message, responseBody);
        verify(ordersService, times(1)).create(order);
        reset(ordersService);
    }

    @Test
    public void should_mark_order_as_complete_for_a_valid_request() throws Exception {
        Order order = Order.builder().email("email@email.com").orderStatus(OrderStatus.COMPLETE.name()).build();
        given(ordersService.updateOrderPickUpDateTime(order)).willReturn(Boolean.TRUE);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(NON_NULL);
        String responseBody = mvc.perform(patch("/customers/orders").contentType
                (
                        APPLICATION_JSON)
                .content(mapper.writeValueAsString(order))).andExpect(status().isAccepted())
                .andReturn().getResponse().getContentAsString();
        Assert.assertTrue(responseBody.isEmpty());
        verify(ordersService, times(1)).updateOrderPickUpDateTime(order);
        reset(ordersService);
    }

    @Test
    public void should_do_nothing_for_order_status_is_not_complete_in_a_request() throws Exception {
        Order order = Order.builder().email("email@email.com").orderStatus(NEW.name()).build();
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(NON_NULL);
        String responseBody = mvc.perform(patch("/customers/orders").contentType
                (
                        APPLICATION_JSON)
                .content(mapper.writeValueAsString(order))).andExpect(status().isAccepted())
                .andReturn().getResponse().getContentAsString();
        Assert.assertTrue(responseBody.isEmpty());
        verify(ordersService, times(0)).updateOrderPickUpDateTime(order);
        reset(ordersService);
    }

    @Test
    public void should_give_order_details_for_a_valid_orderNumber() throws
            Exception {
        Order order = Order.builder().email("email@email.com").orderStatus(NEW.name()).build();
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(NON_NULL);
        given(ordersService.findOrder("A")).willReturn(order);
        String responseBody = mvc.perform(get("/customers/orders/A").contentType
                (APPLICATION_JSON)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Assert.assertEquals(order, mapper.readValue(responseBody, Order.class));
        verify(ordersService, times(1)).findOrder("A");
        reset(ordersService);
    }

    @Test
    public void should_give_order_details_for_a_valid_orderStatus_And_orderDate() throws
            Exception {
        Order order = Order.builder().email("email@email.com").orderStatus(NEW.name()).build();
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(NON_NULL);
        List<Order> orders = Collections.singletonList(order);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
        given(ordersService.findOrder(date, NEW.name())).willReturn(orders);
        String responseBody = mvc.perform(get("/customers/orders").
                param("orderDate", simpleDateFormat.format(date))
                .param("status", NEW.name())
                .contentType(APPLICATION_JSON)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Order> actualOrders = mapper.readValue(responseBody, mapper.getTypeFactory().constructCollectionType
                (List.class, Order.class));
        Assert.assertEquals(1, actualOrders.size());
        Assert.assertEquals(orders, actualOrders);
        verify(ordersService, times(1)).findOrder(date, NEW.name());
        reset(ordersService);
    }

    @Test
    public void should_give_order_details_for_a_valid_orderDate() throws
            Exception {
        Order order = Order.builder().email("email@email.com").orderStatus(NEW.name()).build();
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(NON_NULL);
        List<Order> orders = Collections.singletonList(order);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
        given(ordersService.findOrder(date)).willReturn(orders);
        String responseBody = mvc.perform(get("/customers/orders").
                param("orderDate", simpleDateFormat.format(date))
                .contentType(APPLICATION_JSON)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Order> actualOrders = mapper.readValue(responseBody, mapper.getTypeFactory().constructCollectionType
                (List.class, Order.class));
        Assert.assertEquals(1, actualOrders.size());
        Assert.assertEquals(orders, actualOrders);
        verify(ordersService, times(1)).findOrder(date);
        reset(ordersService);
    }

    @Test
    public void should_give_order_details_for_a_valid_orderStatus() throws
            Exception {
        Order order = Order.builder().email("email@email.com").orderStatus(NEW.name()).build();
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(NON_NULL);
        List<Order> orders = Collections.singletonList(order);
        given(ordersService.findOrderByStatus(NEW.name())).willReturn(orders);
        String responseBody = mvc.perform(get("/customers/orders")
                .param("status", NEW.name())
                .contentType(APPLICATION_JSON)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        List<Order> actualOrders = mapper.readValue(responseBody, mapper.getTypeFactory().constructCollectionType
                (List.class, Order.class));
        Assert.assertEquals(1, actualOrders.size());
        Assert.assertEquals(orders, actualOrders);
        verify(ordersService, times(1)).findOrderByStatus(NEW.name());
        reset(ordersService);
    }




}
