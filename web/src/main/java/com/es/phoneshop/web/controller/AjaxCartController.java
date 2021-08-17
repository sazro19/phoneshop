package com.es.phoneshop.web.controller;

import com.es.core.cart.Cart;
import com.es.core.cart.CartService;
import com.es.phoneshop.web.controller.dto.AddPhoneResponseDTO;
import com.es.phoneshop.web.controller.dto.MiniCartDTO;
import com.es.phoneshop.web.controller.validation.QuantityInputWrapper;
import com.es.phoneshop.web.controller.validation.QuantityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/miniCart")
public class AjaxCartController {
    @Autowired
    private CartService cartService;

    @Resource
    private Validator quantityValidator;

    private static final String SUCCESSFUL_MESSAGE = "Added to cart";

    @InitBinder("quantityInputWrapper")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(quantityValidator);
    }

    @GetMapping
    public MiniCartDTO getMiniCart(HttpSession httpSession) {
        Cart cart = cartService.getCart(httpSession);
        MiniCartDTO miniCartDTO = new MiniCartDTO();
        miniCartDTO.setTotalQuantity(cart.getTotalQuantity());
        miniCartDTO.setTotalCost(cart.getTotalCost());
        return miniCartDTO;
    }

    @PostMapping
    public AddPhoneResponseDTO addPhone(@RequestParam Long phoneId,
                         QuantityInputWrapper quantityInputWrapper,
                         HttpSession httpSession,
                         BindingResult bindingResult) {
        Cart cart = cartService.getCart(httpSession);

        quantityValidator.validate(quantityInputWrapper, bindingResult);
        if (bindingResult.hasErrors()) {
            return createAddPhoneResponseDTO(false, cart, bindingResult);
        }

        cartService.addPhone(cart, phoneId, Long.valueOf(quantityInputWrapper.getQuantity()));

        return createAddPhoneResponseDTO(true, cart, bindingResult);
    }

    private AddPhoneResponseDTO createAddPhoneResponseDTO(boolean isSuccessful, Cart cart, BindingResult bindingResult) {
        AddPhoneResponseDTO responseDTO = new AddPhoneResponseDTO();
        MiniCartDTO miniCartDTO = new MiniCartDTO();
        miniCartDTO.setTotalQuantity(cart.getTotalQuantity());
        miniCartDTO.setTotalCost(cart.getTotalCost());

        responseDTO.setMiniCart(miniCartDTO);
        responseDTO.setSuccessful(isSuccessful);

        String message;
        if (isSuccessful) {
            message = SUCCESSFUL_MESSAGE;
        } else {
            message = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("\n"));
        }
        responseDTO.setMessage(message);

        return responseDTO;
    }
}
