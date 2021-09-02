package com.es.core.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

@Component
public class JdbcInsertClass {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Number insertAndReturnGeneratedKey(String tableName, SqlParameterSource parameters, String generatedColumnName) {
        return new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(tableName)
                .usingGeneratedKeyColumns(generatedColumnName)
                .executeAndReturnKey(parameters);
    }
}
