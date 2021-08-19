package com.es.phoneshop.web.controller.pages;

import javax.annotation.Resource;

import com.es.core.model.searchCriteria.SearchCriteria;
import com.es.core.model.sort.SortCriteria;
import com.es.core.model.sort.SortOrder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.es.core.model.phone.PhoneDao;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/productList")
public class ProductListPageController {
    @Resource
    private PhoneDao phoneDao;

    private static final int DEFAULT_RECORDS_LIMIT = 10;

    @RequestMapping(method = RequestMethod.GET)
    public String showProductList(@RequestParam(required = false, defaultValue = "1", value = "page") String currentPage,
                                  @RequestParam(required = false, defaultValue = "", value = "query") String query,
                                  @RequestParam(required = false, defaultValue = "MODEL", value = "searchCriteria") String searchCriteria,
                                  @RequestParam(required = false, defaultValue = "BRAND", value = "sort") String sortCriteria,
                                  @RequestParam(required = false, defaultValue = "ASC", value = "order") String sortOrder,
                                  Model model) {
        int numberOfPages = getNumberOfPages(phoneDao.getRecordsQuantity(), DEFAULT_RECORDS_LIMIT);
        int offset = calculateOffset(Integer.parseInt(currentPage), DEFAULT_RECORDS_LIMIT, numberOfPages);

        model.addAttribute("numberOfPages", numberOfPages);

        SearchCriteria search = isParamEmpty(searchCriteria) ? SearchCriteria.MODEL : SearchCriteria.valueOf(searchCriteria.toUpperCase());
        SortCriteria sort = isParamEmpty(sortCriteria) ? SortCriteria.BRAND : SortCriteria.valueOf(sortCriteria.toUpperCase());
        SortOrder order = isParamEmpty(sortOrder) ? SortOrder.ASC : SortOrder.valueOf(sortOrder.toUpperCase());

        model.addAttribute("phones", phoneDao.findAll(query, search, sort, order, offset, DEFAULT_RECORDS_LIMIT));
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
