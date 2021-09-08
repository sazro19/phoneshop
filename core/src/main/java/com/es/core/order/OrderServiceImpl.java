package com.es.core.order;

import com.es.core.cart.Cart;
import com.es.core.exceptions.NotEnoughStockException;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderDao;
import com.es.core.model.order.OrderItem;
import com.es.core.model.order.OrderStatus;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import com.es.core.model.phone.stock.Stock;
import com.es.core.model.phone.stock.StockDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@PropertySource("classpath:/config/application.properties")
public class OrderServiceImpl implements OrderService {

    @Autowired
    private Environment environment;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private PhoneDao phoneDao;

    @Autowired
    private StockDao stockDao;

    @Override
    public Order createOrder(Cart cart) {
        Order order = new Order();
        order.setOrderItems(getOrderItemsFromCart(cart, order));
        setPriceInfo(cart, order);

        order.setStatus(OrderStatus.NEW);
        order.setDateOfCreation(LocalDateTime.now());

        return order;
    }

    @Override
    @Transactional(rollbackFor = DataAccessException.class)
    public void placeOrder(Order order) {
        order.getOrderItems().forEach(orderItem -> {
            if (!isInStock(orderItem)) {
                throw new NotEnoughStockException();
            }
        });
        order.setSecureId(UUID.randomUUID().toString());
        orderDao.save(order);

        updateStock(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findAllOrders() {
        List<Order> result = orderDao.findAll();

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Order> getOrder(Long id) {
        return orderDao.get(id);
    }

    @Override
    public void updateStatus(Order order, OrderStatus orderStatus) {
        order.setStatus(orderStatus);

        orderDao.save(order);
    }

    private List<OrderItem> getOrderItemsFromCart(Cart cart, Order order) {
        List<OrderItem> orderItems = cart.getItemList().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setPhone(cartItem.getPhone());
                    orderItem.setQuantity(cartItem.getQuantity());
                    return orderItem;
                }).collect(Collectors.toList());

        return orderItems;
    }

    private void setPriceInfo(Cart cart, Order order) {
        order.setDeliveryPrice(new BigDecimal(environment.getProperty("delivery.price")));
        order.setSubtotal(cart.getTotalCost());
        order.setTotalPrice(order.getSubtotal().add(order.getDeliveryPrice()));
    }

    private boolean isInStock(OrderItem orderItem) {
        return phoneDao.get(orderItem.getPhone().getId())
                .filter(phone -> phone.getStock() >= orderItem.getQuantity())
                .isPresent();
    }

    private void updateStock(Order order) {
        order.getOrderItems().forEach(orderItem -> {
            Phone phone = orderItem.getPhone();
            Stock stock = stockDao.get(phone.getId()).orElse(new Stock());
            long actualStock = stock.getStock() - orderItem.getQuantity();

            stock.setStock((int) actualStock);
            stockDao.update(stock);
        });
    }
}
