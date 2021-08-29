package com.es.core.cart;

import com.es.core.exceptions.NotEnoughStockException;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpSession;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpSessionCartServiceTest {

    @InjectMocks
    private HttpSessionCartService cartService;

    @Mock
    private PhoneDao phoneDao;

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
    public void getCartTest() {
        when(phoneDao.get(FIRST_ID)).thenReturn(Optional.ofNullable(firstTestPhone));

        firstTestPhone.setStock(10);
        HttpSession firstSession = new MockHttpSession();
        HttpSession secondSession = new MockHttpSession();
        Cart firstCart = cartService.getCart(firstSession);
        Cart secondCart = cartService.getCart(secondSession);

        assertTrue(firstCart.getItemList().isEmpty());
        assertNotEquals(firstCart, secondCart);

        cartService.addPhone(firstCart, firstTestPhone.getId(), 2L);

        assertEquals(1, cartService.getCart(firstSession).getItemList().size());
    }

    @Test
    public void addPhoneTest() {
        when(phoneDao.get(SECOND_ID)).thenReturn(Optional.ofNullable(secondTestPhone));

        HttpSession session = new MockHttpSession();
        Cart cart = cartService.getCart(session);
        secondTestPhone.setStock(10);

        BigDecimal price = secondTestPhone.getPrice();
        long quantity = 2L;
        BigDecimal expectedPrice = price.multiply(BigDecimal.valueOf(quantity));

        cartService.addPhone(cart, secondTestPhone.getId(), quantity);

        assertEquals(1, cartService.getCart(session).getItemList().size());
        assertEquals(expectedPrice, cart.getTotalCost());
        assertEquals(quantity, cart.getTotalQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    public void notEnoughStockAddPhoneTest() {
        when(phoneDao.get(THIRD_ID)).thenReturn(Optional.ofNullable(thirdTestPhone));

        HttpSession session = new MockHttpSession();
        Cart cart = cartService.getCart(session);
        thirdTestPhone.setStock(1);

        long quantity = 10L;

        cartService.addPhone(cart, thirdTestPhone.getId(), quantity);
    }

    @Test
    public void updateTest() {
        when(phoneDao.get(FIRST_ID)).thenReturn(Optional.ofNullable(firstTestPhone));
        when(phoneDao.get(SECOND_ID)).thenReturn(Optional.ofNullable(secondTestPhone));
        when(phoneDao.get(THIRD_ID)).thenReturn(Optional.ofNullable(thirdTestPhone));

        HttpSession session = new MockHttpSession();
        Cart cart = cartService.getCart(session);
        firstTestPhone.setStock(10);
        secondTestPhone.setStock(10);
        thirdTestPhone.setStock(10);

        cartService.addPhone(cart, firstTestPhone.getId(), 1L);
        cartService.addPhone(cart, secondTestPhone.getId(), 2L);
        cartService.addPhone(cart, thirdTestPhone.getId(), 3L);

        assertEquals(1L, cart.getItemList().get(0).getQuantity());
        assertEquals(2L, cart.getItemList().get(1).getQuantity());
        assertEquals(3L, cart.getItemList().get(2).getQuantity());

        Map<Long, Long> items = new HashMap<>();
        items.put(firstTestPhone.getId(), 5L);
        items.put(secondTestPhone.getId(), 6L);
        items.put(thirdTestPhone.getId(), 7L);

        cartService.update(cart, items);

        assertEquals(5L, cart.getItemList().get(0).getQuantity());
        assertEquals(6L, cart.getItemList().get(1).getQuantity());
        assertEquals(7L, cart.getItemList().get(2).getQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    public void notEnoughStockUpdateTest() {
        when(phoneDao.get(FIRST_ID)).thenReturn(Optional.ofNullable(firstTestPhone));
        when(phoneDao.get(SECOND_ID)).thenReturn(Optional.ofNullable(secondTestPhone));
        when(phoneDao.get(THIRD_ID)).thenReturn(Optional.ofNullable(thirdTestPhone));

        HttpSession session = new MockHttpSession();
        Cart cart = cartService.getCart(session);
        firstTestPhone.setStock(3);
        secondTestPhone.setStock(10);
        thirdTestPhone.setStock(10);

        cartService.addPhone(cart, firstTestPhone.getId(), 1L);
        cartService.addPhone(cart, secondTestPhone.getId(), 2L);
        cartService.addPhone(cart, thirdTestPhone.getId(), 3L);

        assertEquals(1L, cart.getItemList().get(0).getQuantity());
        assertEquals(2L, cart.getItemList().get(1).getQuantity());
        assertEquals(3L, cart.getItemList().get(2).getQuantity());

        Map<Long, Long> items = new HashMap<>();
        items.put(firstTestPhone.getId(), 5L);
        items.put(secondTestPhone.getId(), 6L);
        items.put(thirdTestPhone.getId(), 7L);

        cartService.update(cart, items);

        assertEquals(5L, cart.getItemList().get(0).getQuantity());
        assertEquals(6L, cart.getItemList().get(1).getQuantity());
        assertEquals(7L, cart.getItemList().get(2).getQuantity());
    }

    @Test
    public void removeTest() {
        when(phoneDao.get(FIRST_ID)).thenReturn(Optional.ofNullable(firstTestPhone));

        HttpSession session = new MockHttpSession();
        Cart cart = cartService.getCart(session);
        firstTestPhone.setStock(3);

        cartService.addPhone(cart, firstTestPhone.getId(), 1L);

        assertEquals(1L, cart.getItemList().get(0).getQuantity());

        cartService.remove(cart, firstTestPhone.getId());

        assertTrue(cart.getItemList().isEmpty());
    }
}
