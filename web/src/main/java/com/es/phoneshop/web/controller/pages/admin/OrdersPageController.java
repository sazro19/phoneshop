package com.es.phoneshop.web.controller.pages.admin;

import com.es.core.exceptions.OrderNotFoundException;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderStatus;
import com.es.core.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(value = "/admin/orders")
public class OrdersPageController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String orderList(Model model) {
        List<Order> orderList = orderService.findAllOrders();

        model.addAttribute("orderList", orderList);

        return "orderList";
    }

    @GetMapping("/{orderId}")
    public String order(@PathVariable String orderId,
                        Model model) {
        Order order = getOrderWithId(orderId);

        model.addAttribute("order", order);

        return "orderDetails";
    }

    @PostMapping("/{orderId}")
    public String updateStatus(@PathVariable String orderId,
                               @RequestParam(name = "status") String status,
                               Model model) {
        Order order = getOrderWithId(orderId);

        orderService.updateStatus(order, OrderStatus.valueOf(status));

        model.addAttribute("order", order);

        return "redirect:/admin/orders/" + orderId;
    }

    private Order getOrderWithId(String orderId) {
        long id;
        try {
            id = Long.parseLong(orderId);
        } catch (NumberFormatException e) {
            throw new OrderNotFoundException();
        }

        return orderService.getOrder(id).orElseThrow(OrderNotFoundException::new);
    }
}
