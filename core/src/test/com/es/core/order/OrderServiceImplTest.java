package com.es.core.order;

import com.es.core.cart.Cart;
import com.es.core.cart.CartItem;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderDao;
import com.es.core.model.order.OrderStatus;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import com.es.core.model.phone.stock.Stock;
import com.es.core.model.phone.stock.StockDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private PhoneDao phoneDao;
    @Mock
    private StockDao stockDao;
    @Mock
    private Environment environment;
    @Mock
    private OrderDao orderDao;

    private Phone firstTestPhone;
    private Phone secondTestPhone;
    private Phone thirdTestPhone;

    private static final Long FIRST_ID = 1L;
    private static final Long SECOND_ID = 2L;
    private static final Long THIRD_ID = 3L;
    private static final BigDecimal FIRST_PRICE = new BigDecimal(100);
    private static final BigDecimal SECOND_PRICE = new BigDecimal(200);
    private static final BigDecimal THIRD_PRICE = new BigDecimal(300);

    @Before
    public void setup() {
        setupTestPhones();
    }

    private void setupTestPhones() {
        firstTestPhone = new Phone();
        firstTestPhone.setId(FIRST_ID);
        firstTestPhone.setPrice(FIRST_PRICE);

        secondTestPhone = new Phone();
        secondTestPhone.setId(SECOND_ID);
        secondTestPhone.setPrice(SECOND_PRICE);

        thirdTestPhone = new Phone();
        thirdTestPhone.setId(THIRD_ID);
        thirdTestPhone.setPrice(THIRD_PRICE);
    }

    @Test
    public void createOrderTest() {
        String deliveryPriceProperty = "5";
        when(environment.getProperty("delivery.price")).thenReturn(deliveryPriceProperty);

        Cart cart = new Cart();
        List<CartItem> cartItemList = new ArrayList<>(Arrays.asList(new CartItem(firstTestPhone, 1),
                new CartItem(secondTestPhone, 1)));

        cart.setItemList(cartItemList);
        cart.setTotalQuantity(2);
        cart.setTotalCost(firstTestPhone.getPrice().add(secondTestPhone.getPrice()));

        Order order = orderService.createOrder(cart);

        AtomicInteger i = new AtomicInteger();
        order.getOrderItems().forEach(orderItem -> assertEquals(cartItemList.get(i.getAndIncrement()).getPhone(),
                orderItem.getPhone()));
        BigDecimal deliveryPrice = new BigDecimal(deliveryPriceProperty);
        assertEquals(deliveryPrice, order.getDeliveryPrice());
        assertEquals(deliveryPrice.add(order.getSubtotal()), order.getTotalPrice());
    }

    @Test
    public void placeOrderTest() {
        Stock stock1 = new Stock();
        stock1.setStock(5);
        stock1.setReserved(5);
        stock1.setPhoneId(FIRST_ID);
        when(stockDao.get(FIRST_ID)).thenReturn(Optional.of(stock1));

        Stock stock2 = new Stock();
        stock2.setStock(3);
        stock2.setReserved(5);
        stock2.setPhoneId(SECOND_ID);
        when(stockDao.get(SECOND_ID)).thenReturn(Optional.of(stock2));

        String deliveryPriceProperty = "5";
        when(environment.getProperty("delivery.price")).thenReturn(deliveryPriceProperty);

        firstTestPhone.setStock(5);
        secondTestPhone.setStock(5);
        when(phoneDao.get(FIRST_ID)).thenReturn(Optional.of(firstTestPhone));
        when(phoneDao.get(SECOND_ID)).thenReturn(Optional.of(secondTestPhone));

        Cart cart = new Cart();
        List<CartItem> cartItemList = new ArrayList<>(Arrays.asList(new CartItem(firstTestPhone, 1),
                new CartItem(secondTestPhone, 1)));

        cart.setItemList(cartItemList);
        cart.setTotalQuantity(2);
        cart.setTotalCost(firstTestPhone.getPrice().add(secondTestPhone.getPrice()));

        Order order = orderService.createOrder(cart);

        orderService.placeOrder(order);

        assertEquals(4, stock1.getStock().intValue());
        assertEquals(2, stock2.getStock().intValue());
        assertFalse(order.getSecureId().isEmpty());
    }

    @Test
    public void findAllOrdersTest() {
        Order firstOrder =  new Order();
        Order secondOrder = new Order();
        Order thirdOrder = new Order();

        List<Order> expected = Arrays.asList(firstOrder, secondOrder, thirdOrder);

        when(orderDao.findAll()).thenReturn(expected);

        assertEquals(expected, orderService.findAll());
    }

    @Test
    public void getOrderTest() {
        Order order = new Order();
        order.setId(1L);

        Optional<Order> optionalOrder = Optional.of(order);

        when(orderDao.get(order.getId())).thenReturn(optionalOrder);

        assertEquals(order, optionalOrder.orElse(new Order()));
    }

    @Test
    public void updateStatusTest() {
        Order order = new Order();
        order.setStatus(OrderStatus.NEW);

        orderService.updateStatus(order, OrderStatus.DELIVERED);

        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }
}
