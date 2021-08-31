package com.es.core.order;

import com.es.core.cart.Cart;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource("classpath:/config/application.properties")
public class OrderServiceImpl implements OrderService {

    @Autowired
    private Environment environment;

    @Override
    public Order createOrder(Cart cart) {
        Order order = new Order();
        order.setOrderItems(getOrderItemsFromCart(cart, order));
        setPriceInfo(cart, order);

        return order;
    }

    @Override
    public void placeOrder(Order order) {
        throw new UnsupportedOperationException("TODO");
    }

    private List<OrderItem> getOrderItemsFromCart(Cart cart, Order order) {
        List<OrderItem> orderItems = new ArrayList<>();

        cart.getItemList().forEach(cartItem -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setPhone(cartItem.getPhone());
            orderItem.setQuantity(cartItem.getQuantity());

            orderItems.add(orderItem);
        });

        return orderItems;
    }

    private void setPriceInfo(Cart cart, Order order) {
        order.setDeliveryPrice(new BigDecimal(environment.getProperty("delivery.price")));
        order.setSubtotal(cart.getTotalCost());
        order.setTotalPrice(order.getSubtotal().add(order.getDeliveryPrice()));
    }
}
