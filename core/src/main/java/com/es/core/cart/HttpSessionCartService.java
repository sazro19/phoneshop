package com.es.core.cart;

import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class HttpSessionCartService implements CartService {
    private static final String CART_SESSION_ATTRIBUTE = HttpSessionCartService.class.getName() + ".cart";

    @Autowired
    private PhoneDao phoneDao;

    @Override
    public Cart getCart(HttpSession session) {
        synchronized (session) {
            Cart cart = (Cart) session.getAttribute(CART_SESSION_ATTRIBUTE);
            if (cart == null) {
                cart = new Cart();
                session.setAttribute(CART_SESSION_ATTRIBUTE, cart);
            }
            return cart;
        }
    }

    @Override
    public void addPhone(Cart cart, Long phoneId, Long quantity) {
        try {
            Phone phone = phoneDao.get(phoneId).get();
            Optional<CartItem> cartItemOptional = getExistingItem(cart, phone);
            if (cartItemOptional.isPresent()) {
                CartItem cartItem = cartItemOptional.get();
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
            } else {
                cart.getItemList().add(new CartItem(phone, quantity));
            }
            recalculateCart(cart);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.valueOf(phoneId));
        }

    }

    @Override
    public void update(Map<Long, Long> items) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public void remove(Long phoneId) {
        throw new UnsupportedOperationException("TODO");
    }

    private Optional<CartItem> getExistingItem(Cart cart, Phone phone) {
        return cart.getItemList()
                .stream()
                .filter(existingItem -> phone.equals(existingItem.getPhone()))
                .findAny();
    }

    private void recalculateCart(Cart cart) {
        recalculateTotalQuantity(cart);
        recalculateTotalCost(cart);
    }

    private void recalculateTotalQuantity(Cart cart) {
        cart.setTotalQuantity(cart.getItemList()
                .stream()
                .map(CartItem::getQuantity)
                .reduce(Long::sum)
                .orElse(0L));
    }

    private void recalculateTotalCost(Cart cart) {
        cart.setDeliveryCost(calculateDeliveryCost());
        cart.setTotalCost(cart.getItemList()
                .stream()
                .map(this::calculateCartItemCost)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO));
    }

    private BigDecimal calculateDeliveryCost() {
        return new BigDecimal(0);
    }

    private BigDecimal calculateCartItemCost(CartItem cartItem) {
        BigDecimal price = cartItem.getPhone().getPrice();
        if (price != null) {
            return price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        } else {
            return BigDecimal.ZERO;
        }
    }

    public void setPhoneDao(PhoneDao phoneDao) {
        this.phoneDao = phoneDao;
    }
}
