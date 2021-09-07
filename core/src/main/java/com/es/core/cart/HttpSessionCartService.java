package com.es.core.cart;

import com.es.core.exceptions.NotEnoughStockException;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@PropertySource("classpath:/config/errors.properties")
public class HttpSessionCartService implements CartService {
    private static final String CART_SESSION_ATTRIBUTE = HttpSessionCartService.class.getName() + ".cart";

    @Autowired
    private PhoneDao phoneDao;

    @Autowired
    private Environment environment;

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
            throw new IllegalArgumentException(environment.getProperty("error.invalidQuantity"));
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
                String message = environment.getProperty("error.notEnoughStock") + ". " +
                        (phone.getStock() - cartItemOptional.get().getQuantity()) + " available";
                throw new NotEnoughStockException(message);
            }

            CartItem cartItem = cartItemOptional.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            if (isNotEnoughStock(0L, quantity, phone.getStock())) {
                String message = environment.getProperty("error.notEnoughStock") + ". " + phone.getStock() + " available";
                throw new NotEnoughStockException(phoneId, message);
            }

            cart.getItemList().add(new CartItem(phone, quantity));
        }
        recalculate(cart);
    }

    @Override
    public void update(Cart cart, Map<Long, Long> items) {
        List<CartItem> cartItemListToUpdate = cart.getItemList().stream()
                .filter(cartItem -> items.containsKey(cartItem.getPhone().getId()))
                .collect(Collectors.toList());

        if (cartItemListToUpdate.isEmpty()) {
            throw new IllegalArgumentException(environment.getProperty("error.nothingToUpdate"));
        }

        cartItemListToUpdate.forEach(cartItem -> {
            long quantity = items.get(cartItem.getPhone().getId());

            if (isNotEnoughStock(0L, quantity, cartItem.getPhone().getStock())) {
                recalculate(cart);
                String message = environment.getProperty("error.notEnoughStock") + ". " + cartItem.getPhone().getStock() + " available";
                throw new NotEnoughStockException(cartItem.getPhone().getId(), message);
            }

            cartItem.setQuantity(quantity);
        });
        recalculate(cart);
    }

    @Override
    public void remove(Cart cart, Long phoneId) {
        Optional<Phone> phoneOptional = phoneDao.get(phoneId);
        phoneOptional.ifPresent(phone ->
                cart.getItemList().removeIf(cartItem ->
                        phone.equals(cartItem.getPhone())));
        recalculate(cart);
    }

    @Override
    public void recalculate(Cart cart) {
        recalculateTotalQuantity(cart);
        recalculateTotalCost(cart);
    }

    @Override
    public void setActualQuantityAndErrors(CartItem cartItem, Map<Long, String> quantityErrors) {
        phoneDao.get(cartItem.getPhone().getId())
                .ifPresent(phone -> {
                    long actualQuantity = phone.getStock();
                    if (actualQuantity < cartItem.getQuantity()) {
                        cartItem.setQuantity(actualQuantity);
                        quantityErrors.put(cartItem.getPhone().getId(), environment.getProperty("error.changedQuantity"));
                    }
                });
    }

    private Optional<CartItem> getExistingItem(Cart cart, Phone phone) {
        return cart.getItemList()
                .stream()
                .filter(existingItem -> phone.equals(existingItem.getPhone()))
                .findAny();
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
