package com.es.phoneshop.web.controller.pages;

import com.es.core.model.phone.ParamWrapper;
import com.es.core.model.phone.PhoneDao;
import com.es.core.model.sort.SortCriteria;
import com.es.core.model.sort.SortOrder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/productList")
public class ProductListPageController {
    @Resource
    private PhoneDao phoneDao;

    private static final int DEFAULT_RECORDS_LIMIT = 10;

    @RequestMapping(method = RequestMethod.GET)
    public String showProductList(@RequestParam(required = false, defaultValue = "1", value = "page") String currentPage,
                                  @RequestParam(required = false, defaultValue = "", value = "query") String query,
                                  @RequestParam(required = false, defaultValue = "BRAND", value = "sort") String sortCriteria,
                                  @RequestParam(required = false, defaultValue = "ASC", value = "order") String sortOrder,
                                  Model model) {
        SortCriteria sort = isParamEmpty(sortCriteria) ? SortCriteria.BRAND : SortCriteria.valueOf(sortCriteria.toUpperCase());
        SortOrder order = isParamEmpty(sortOrder) ? SortOrder.ASC : SortOrder.valueOf(sortOrder.toUpperCase());

        int numberOfPages = getNumberOfPages(phoneDao.getRecordsQuantity(query, sort, order), DEFAULT_RECORDS_LIMIT);
        int offset = calculateOffset(Integer.parseInt(currentPage), DEFAULT_RECORDS_LIMIT, numberOfPages);

        model.addAttribute("numberOfPages", numberOfPages);

        ParamWrapper wrapper = new ParamWrapper(query, sort, order, offset, DEFAULT_RECORDS_LIMIT);

        model.addAttribute("phones", phoneDao.findAll(wrapper));
        return "productList";
    }

    private int calculateOffset(int currentPage, int numberOfRecordsPerPage, int numberOfPages) {
        if (currentPage <= 0) {
            return 0;
        }
        if (currentPage > numberOfPages) {
            return (numberOfPages - 1) * numberOfRecordsPerPage;
        }
        return (currentPage - 1) * numberOfRecordsPerPage;
    }

    private int getNumberOfPages(int totalRecordsQuantity, int recordsLimit) {
        if (totalRecordsQuantity % recordsLimit == 0) {
            return totalRecordsQuantity / recordsLimit;
        } else
            return totalRecordsQuantity / recordsLimit + 1;
    }

    private boolean isParamEmpty(String param) {
        return param == null || param.isEmpty();
    }
}
