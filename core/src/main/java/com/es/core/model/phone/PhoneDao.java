package com.es.core.model.phone;

import com.es.core.model.searchCriteria.SearchCriteria;

import java.util.List;
import java.util.Optional;

public interface PhoneDao {
    Optional<Phone> get(Long key);
    void save(Phone phone);
    List<Phone> findAll(int offset, int limit);
    List<Phone> findAll(String query, SearchCriteria criteria, int offset, int limit);
    int getRecordsQuantity();
}
