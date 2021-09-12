package com.es.core.model.order;

import java.util.List;
import java.util.Optional;

public interface OrderDao {
    Optional<Order> get(String secureId);
    Optional<Order> get(Long id);
    void save(Order order);
    List<Order> findAll();
    void updateStatus(Order order);
}
