package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.Cart;
import com.es.core.cart.CartService;
import com.es.core.exceptions.NotEnoughStockException;
import com.es.core.model.order.Order;
import com.es.core.order.OrderService;
import com.es.phoneshop.web.controller.dto.CustomerInfoDto;
import com.es.phoneshop.web.controller.validation.CustomerInfoValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/order")
public class OrderPageController {
    @Resource
    private OrderService orderService;

    @Resource
    private CartService cartService;

    @Resource
    private CustomerInfoValidator customerInfoValidator;

    public static final String CUSTOMER_INFO_ERRORS_ATTRIBUTE_NAME = "customerInfoErrors";
    public static final String QUANTITY_ERRORS_ATTRIBUTE_NAME = "quantityErrors";
    public static final String CUSTOMER_INFO_ATTRIBUTE_NAME = "customerInfo";
    public static final String ORDER_ATTRIBUTE_NAME = "order";

    @InitBinder("customerInfoDto")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(customerInfoValidator);
    }

    @GetMapping
    public String getOrder(Model model,
                           HttpSession session) {
        model.addAttribute("order", orderService.createOrder(cartService.getCart(session)));
        return "order";
    }

    @PostMapping
    public String placeOrder(CustomerInfoDto customerInfoDto,
                           Model model,
                           HttpSession session,
                           BindingResult bindingResult) {
        Cart cart = cartService.getCart(session);

        Map<String, String> customerInfoErrors = new HashMap<>();
        Map<Long, String> quantityErrors = new HashMap<>();

        Order order = orderService.createOrder(cart);

        customerInfoValidator.validate(customerInfoDto, bindingResult);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> customerInfoErrors.put(objectError.getCode(),
                    objectError.getDefaultMessage()));
        } else {
            setOrderFields(order, customerInfoDto);
        }

        try {
            if (customerInfoErrors.isEmpty()) {
                orderService.placeOrder(order);
            }
        } catch (NotEnoughStockException e) {
            quantityErrors = cartService.checkCartForEnoughQuantityItems(cart);
        }

        model.addAttribute(CUSTOMER_INFO_ERRORS_ATTRIBUTE_NAME, customerInfoErrors);
        model.addAttribute(QUANTITY_ERRORS_ATTRIBUTE_NAME, quantityErrors);
        model.addAttribute(CUSTOMER_INFO_ATTRIBUTE_NAME, customerInfoDto);
        model.addAttribute(ORDER_ATTRIBUTE_NAME, order);

        if (customerInfoErrors.isEmpty() && quantityErrors.isEmpty()) {
            cart.getItemList().clear();
            cartService.recalculate(cart);
            return "redirect:/orderOverview/" + order.getSecureId();
        } else {
            return "order";
        }
    }

    private void setOrderFields(Order order, CustomerInfoDto customerInfoDto) {
        order.setFirstName(customerInfoDto.getFirstName());
        order.setLastName(customerInfoDto.getLastName());
        order.setDeliveryAddress(customerInfoDto.getDeliveryAddress());
        order.setContactPhoneNo(customerInfoDto.getContactPhoneNo());
        order.setAdditionalInformation(customerInfoDto.getAdditionalInformation());
    }
}
