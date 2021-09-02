package com.es.core.model.order;

import java.util.Optional;

public interface OrderDao {
    Optional<Order> get(String secureId);
    void save(Order order);
}
