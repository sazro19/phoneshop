package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.CartService;
import com.es.core.order.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "/order")
public class OrderPageController {
    @Resource
    private OrderService orderService;

    @Resource
    private CartService cartService;

    @GetMapping
    public String getOrder(Model model,
                           HttpSession session) {
        model.addAttribute("order", orderService.createOrder(cartService.getCart(session)));
        return "order";
    }

    @PostMapping
    public void placeOrder() {
        orderService.placeOrder(null);
    }
}
