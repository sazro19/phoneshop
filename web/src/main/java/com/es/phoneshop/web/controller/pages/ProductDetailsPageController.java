package com.es.phoneshop.web.controller.pages;

import com.es.core.exceptions.ProductNotFoundException;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.Optional;

@Controller
@RequestMapping(value = "/productDetails")
public class ProductDetailsPageController {
    @Resource
    private PhoneDao phoneDao;

    @GetMapping
    public String showProductDetails(@RequestParam String id,
                                     Model model) {
        long phoneId;
        try {
            phoneId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new ProductNotFoundException();
        }

        Optional<Phone> phoneOptional = phoneDao.get(phoneId);
            if (!phoneOptional.isPresent()) {
                throw new ProductNotFoundException();
            }
            model.addAttribute("phone", phoneOptional.get());

        return "productDetails";
    }
}
