package com.es.core.cart;

import com.es.core.exceptions.NotEnoughStockException;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HttpSessionCartService implements CartService {
    private static final String CART_SESSION_ATTRIBUTE = HttpSessionCartService.class.getName() + ".cart";

    private static final String INVALID_QUANTITY_MESSAGE = "Invalid quantity";

    private static final String NOT_ENOUGH_STOCK_MESSAGE = "Not enough stock";

    private static final String NOTHING_TO_UPDATE_MESSAGE = "Nothing to update";

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
            if (isNotEnoughStock(cartItemOptional.get().getQuantity(), quantity, phone.getStock())) {
                String message = NOT_ENOUGH_STOCK_MESSAGE + ". " +
                        (phone.getStock() - cartItemOptional.get().getQuantity()) + " available";
                throw new NotEnoughStockException(message);
            }

            CartItem cartItem = cartItemOptional.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            if (isNotEnoughStock(0L, quantity, phone.getStock())) {
                String message = NOT_ENOUGH_STOCK_MESSAGE + ". " + phone.getStock() + " available";
                throw new NotEnoughStockException(phoneId, message);
            }

            cart.getItemList().add(new CartItem(phone, quantity));
        }
        recalculateCart(cart);

    }

    @Override
    public void update(Cart cart, Map<Long, Long> items) {
        List<CartItem> cartItemListToUpdate = cart.getItemList().stream()
                .filter(cartItem -> items.containsKey(cartItem.getPhone().getId()))
                .collect(Collectors.toList());

        if (cartItemListToUpdate.isEmpty()) {
            throw new IllegalArgumentException(NOTHING_TO_UPDATE_MESSAGE);
        }

        cartItemListToUpdate.forEach(cartItem -> {
            long quantity = items.get(cartItem.getPhone().getId());

            if (isNotEnoughStock(0L, quantity, cartItem.getPhone().getStock())) {
                recalculateCart(cart);
                String message = NOT_ENOUGH_STOCK_MESSAGE + ". " + cartItem.getPhone().getStock() + " available";
                throw new NotEnoughStockException(cartItem.getPhone().getId(), message);
            }

            cartItem.setQuantity(quantity);
        });
        recalculateCart(cart);
    }

    @Override
    public void remove(Cart cart, Long phoneId) {
        Optional<Phone> phoneOptional = phoneDao.get(phoneId);
        phoneOptional.ifPresent(phone ->
                cart.getItemList().removeIf(cartItem ->
                        phone.equals(cartItem.getPhone())));
        recalculateCart(cart);
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
        cart.setTotalCost(cart.getItemList()
                .stream()
                .map(this::calculateCartItemCost)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO));
    }

    private BigDecimal calculateCartItemCost(CartItem cartItem) {
        BigDecimal price = cartItem.getPhone().getPrice();
        if (price != null) {
            return price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        } else {
            return BigDecimal.ZERO;
        }
    }

    private boolean isNotEnoughStock(long cartItemQuantity, long quantityToAdd, Integer stock) {
        return cartItemQuantity + quantityToAdd > stock;
    }

    public void setPhoneDao(PhoneDao phoneDao) {
        this.phoneDao = phoneDao;
    }
}
