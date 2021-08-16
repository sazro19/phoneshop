package com.es.core.model.phone;

import com.es.core.model.searchCriteria.SearchCriteria;
import com.es.core.model.sort.SortCriteria;
import com.es.core.model.sort.SortOrder;

import java.util.List;
import java.util.Optional;

public interface PhoneDao {
    Optional<Phone> get(Long key);
    void save(Phone phone);
    List<Phone> findAll(int offset, int limit);
    List<Phone> findAll(String query, SearchCriteria criteria, SortCriteria sortCriteria, SortOrder sortOrder, int offset, int limit);
    int getRecordsQuantity();
}
