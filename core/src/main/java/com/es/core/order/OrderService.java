package com.es.core.order;

import com.es.core.cart.Cart;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Cart cart);
    void placeOrder(Order order);
    List<Order> findAll();
    Optional<Order> getOrder(Long id);
    void updateStatus(Order order, OrderStatus orderStatus);
}
