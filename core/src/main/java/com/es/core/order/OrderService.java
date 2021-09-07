package com.es.core.order;

import com.es.core.cart.Cart;
import com.es.core.model.order.Order;

import java.util.Map;

public interface OrderService {
    Order createOrder(Cart cart);
    void placeOrder(Order order, Cart cart, Map<Long, String> quantityErrors);
}
