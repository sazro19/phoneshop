package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.Cart;
import com.es.core.cart.CartService;
import com.es.core.exceptions.NotEnoughStockException;
import com.es.phoneshop.web.controller.validation.QuantityInputWrapper;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/cart")
public class CartPageController {
    @Resource
    private CartService cartService;

    @Resource
    private Validator quantityValidator;

    @InitBinder("quantityInputWrapper")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(quantityValidator);
    }

    @GetMapping
    public String getCart(HttpSession session, Model model) {
        model.addAttribute("cart", cartService.getCart(session));
        return "cart";
    }

    @PutMapping
    public String updateCart(@RequestParam("phoneId") List<Long> phoneIds,
                           @RequestParam("quantity") List<String> quantities,
                           HttpSession session,
                           Model model) {
        Cart cart = cartService.getCart(session);

        Map<Long, String> errors = new HashMap<>();
        Map<Long, Long> items = new HashMap<>();

        AtomicInteger i = new AtomicInteger();
        phoneIds.forEach(phoneId -> {
            QuantityInputWrapper wrapper = new QuantityInputWrapper(quantities.get(i.get()));
            BindingResult bindingResult = validate(wrapper, quantityValidator);

            if (bindingResult.hasErrors()) {
                errors.put(phoneId, bindingResult.getAllErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.joining(". ")));
            } else {
                items.put(phoneId, Long.valueOf(quantities.get(i.get())));
            }

            i.getAndIncrement();
        });

        model.addAttribute("updated", true);
        try {
            cartService.update(cart, items);
        } catch (IllegalArgumentException | NotEnoughStockException e) {
            if (e.getClass().equals(NotEnoughStockException.class)) {
                errors.put(((NotEnoughStockException) e).getId(), e.getMessage());
            }
        }
        model.addAttribute("errors", errors);
        model.addAttribute("cart", cart);
        return "cart";
    }

    @DeleteMapping("/{phoneId}")
    public String deletePhone(@PathVariable String phoneId,
                              HttpSession session) {
        Cart cart = cartService.getCart(session);
        cartService.remove(cart, Long.valueOf(phoneId));
        return "redirect:/cart";
    }

    private BindingResult validate(Object target, Validator validator) {
        DataBinder dataBinder = new DataBinder(target);
        dataBinder.setValidator(validator);
        dataBinder.validate();
        return dataBinder.getBindingResult();
    }
}
