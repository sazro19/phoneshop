package com.es.core.model.order.extractorConfig;

import com.es.core.model.order.Order;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.util.List;

@Configuration
public class OrderExtractorConfig {

    @Bean
    public ResultSetExtractor<List<Order>> orderExtractor(JdbcTemplateMapperFactory jdbcTemplateMapperFactory) {
        return jdbcTemplateMapperFactory.addKeys("secureId", "orderItems_phone_id")
                .newResultSetExtractor(Order.class);
    }
}
