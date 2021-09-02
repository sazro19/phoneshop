package com.es.phoneshop.web.controller.pages;

import com.es.core.model.order.Order;
import com.es.core.model.order.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/orderOverview")
public class OrderOverviewPageController {

    @Autowired
    private OrderDao orderDao;

    @GetMapping("/{orderId}")
    public String orderOverview(@PathVariable String orderId,
                                Model model) {
        Order order = orderDao.get(orderId).get();

        model.addAttribute("order", order);
        return "orderOverview";
    }
}
