package com.es.phoneshop.web.controller.pages;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/login")
public class LoginPageController {

    @GetMapping
    public String login(@RequestParam(required = false) String error,
                        Authentication authentication,
                        Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/productList";
        }
        if (error != null) {
            model.addAttribute("loginError", "Wrong login or password");
        }
        return "login";
    }
}
