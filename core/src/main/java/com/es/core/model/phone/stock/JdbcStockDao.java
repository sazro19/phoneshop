package com.es.core.model.phone.stock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Component
public class JdbcStockDao implements StockDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SELECT_STOCK_BY_PHONE_ID_QUERY = "SELECT * FROM stocks WHERE phoneId = ?";

    private static final String UPDATE_STOCK_QUERY = "UPDATE stocks SET stock=:stock, reserved=:reserved " +
            "WHERE stocks.phoneId=:phoneId";

    @Override
    public Optional<Stock> get(Long phoneId) {
        Optional<Stock> result = Optional.ofNullable(jdbcTemplate.queryForObject(SELECT_STOCK_BY_PHONE_ID_QUERY,
                new BeanPropertyRowMapper<>(Stock.class), phoneId));

        return result;
    }

    @Override
    public void update(final Stock stock) {
        if (stock.getPhoneId() == null) {
            throw new IllegalArgumentException();
        }

        if (stock.getStock().equals(0) && !stock.getReserved().equals(0)) {
            stock.setStock(stock.getReserved());
            stock.setReserved(0);
        }
        NamedParameterJdbcTemplate parameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        parameterJdbcTemplate.update(UPDATE_STOCK_QUERY, new BeanPropertySqlParameterSource(stock));
    }
}
