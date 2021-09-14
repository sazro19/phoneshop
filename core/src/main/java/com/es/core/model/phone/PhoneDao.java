package com.es.core.model.phone;

import com.es.core.model.sort.SortCriteria;
import com.es.core.model.sort.SortOrder;

import java.util.List;
import java.util.Optional;

public interface PhoneDao {
    Optional<Phone> get(Long key);
    Optional<Phone> get(String model);
    void save(Phone phone);
    List<Phone> findAll(int offset, int limit);
    List<Phone> findAll(ParamWrapper wrapper);
    int getRecordsQuantity(String query, SortCriteria sortCriteria, SortOrder order);
}
