package com.es.core.cart;

import com.es.core.exceptions.NotEnoughStockException;
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

    private static final String INVALID_QUANTITY_MESSAGE = "Invalid quantity";

    private static final String NOT_ENOUGH_STOCK_MESSAGE = "Not enough stock";

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
        if (quantity <= 0) {
            throw new IllegalArgumentException(INVALID_QUANTITY_MESSAGE);
        }

        Phone phone;
        try {
            phone = phoneDao.get(phoneId).get();
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.valueOf(phoneId));
        }

        Optional<CartItem> cartItemOptional = getExistingItem(cart, phone);
        if (cartItemOptional.isPresent()) {
            if (!isEnoughStock(cartItemOptional.get().getQuantity(), quantity, phone.getStock())) {
                String message = NOT_ENOUGH_STOCK_MESSAGE + ". " +
                        (phone.getStock() - cartItemOptional.get().getQuantity()) + " available";
                throw new NotEnoughStockException(message);
            }

            CartItem cartItem = cartItemOptional.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            if (!isEnoughStock(0L, quantity, phone.getStock())) {
                String message = NOT_ENOUGH_STOCK_MESSAGE + ". " + phone.getStock() + " available";
                throw new NotEnoughStockException(message);
            }

            cart.getItemList().add(new CartItem(phone, quantity));
        }
        recalculateCart(cart);

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

    private boolean isEnoughStock(long cartItemQuantity, long quantityToAdd, Integer stock) {
        return cartItemQuantity + quantityToAdd <= stock;
    }

    public void setPhoneDao(PhoneDao phoneDao) {
        this.phoneDao = phoneDao;
    }
}
