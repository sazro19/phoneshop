package com.es.phoneshop.web.controller.pages;

import com.es.core.cart.Cart;
import com.es.core.cart.CartService;
import com.es.core.exceptions.NotEnoughStockException;
import com.es.core.exceptions.ProductNotFoundException;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import com.es.phoneshop.web.controller.dto.quickOrder.QuickOrderDto;
import com.es.phoneshop.web.controller.dto.quickOrder.QuickOrderRow;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequestMapping(value = "/quickOrder")
public class QuickOrderPageController {
    @Resource
    private CartService cartService;

    @Resource
    private PhoneDao phoneDao;

    @Resource
    private Validator quickOrderValidator;

    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found";

    private static final String QUICK_ORDER_DTO_ATTRIBUTE = "quickOrderDto";

    private static final String ADDED_PRODUCTS_ATTRIBUTE = "addedProducts";

    private static final String ERRORS_ATTRIBUTE = "errors";

    @InitBinder("quickOrderDto")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(quickOrderValidator);
    }

    @GetMapping
    public String quickOrder(Model model) {
        model.addAttribute(QUICK_ORDER_DTO_ATTRIBUTE, new QuickOrderDto());
        return "quickOrder";
    }

    @PostMapping
    public String makeQuickOrder(QuickOrderDto quickOrderDto,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 Model model) {
        Cart cart = cartService.getCart(session);
        List<String> addedProducts = new ArrayList<>();

        quickOrderValidator.validate(quickOrderDto, bindingResult);

        AtomicInteger i = new AtomicInteger(0);
        quickOrderDto.getRows().forEach(quickOrderRow -> {
            loopLogic(quickOrderDto, quickOrderRow, bindingResult, addedProducts, i, cart);
        });

        model.addAttribute(QUICK_ORDER_DTO_ATTRIBUTE, quickOrderDto);
        model.addAttribute(ADDED_PRODUCTS_ATTRIBUTE, addedProducts);
        model.addAttribute(ERRORS_ATTRIBUTE, bindingResult);
        return "quickOrder";
    }

    private void loopLogic(QuickOrderDto quickOrderDto,
                           QuickOrderRow quickOrderRow,
                           BindingResult bindingResult,
                           List<String> addedProducts,
                           AtomicInteger i,
                           Cart cart) {
        if (!bindingResult.hasFieldErrors("rows[" + i.intValue() + "].phoneModel") &&
                !bindingResult.hasFieldErrors("rows[" + i.intValue() + "].quantity")) {
            String phoneModel = quickOrderRow.getPhoneModel();
            Long quantity = quickOrderRow.getQuantity();

            if (isEmptyString(phoneModel) && quantity == null) {
                i.getAndIncrement();
                return;
            }

            Optional<Phone> phoneOptional = phoneDao.get(phoneModel);

            try {
                Phone phone = phoneOptional.orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE));
                cartService.addPhone(cart, phone.getId(), quantity);
                addedProducts.add(phoneModel);
                quickOrderDto.getRows().set(i.intValue(), new QuickOrderRow());
            } catch (ProductNotFoundException | NotEnoughStockException | IllegalArgumentException e) {
                if (e.getClass().equals(ProductNotFoundException.class)) {
                    bindingResult.rejectValue("rows[" + i.intValue() + "].phoneModel", "notFound", e.getMessage());
                } else {
                    bindingResult.rejectValue("rows[" + i.intValue() + "].quantity", "notEnoughStock", e.getMessage());
                }
            }
        }
        i.getAndIncrement();
    }

    private boolean isEmptyString(String string) {
        return string == null || string.trim().isEmpty();
    }
}
