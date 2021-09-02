package com.es.core.model;

import com.es.core.model.order.Order;
import com.es.core.model.phone.Phone;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.util.List;

import static com.es.core.model.phone.PhoneFieldConstants.PHONE_ID_FIELD;

@Configuration
public class extractorConfig {
//    @Bean
//    public ResultSetExtractor<List<Phone>> getResultSetExtractor(@Qualifier("phoneMapper") JdbcTemplateMapperFactory jdbcTemplateMapperFactory) {
//        return jdbcTemplateMapperFactory.addKeys(PHONE_ID_FIELD)
//                .newResultSetExtractor(Phone.class);
//    }
//
//    @Bean
//    @Qualifier("phoneMapper")
//    public JdbcTemplateMapperFactory getJdbcTemplateMapperFactory() {
//        return JdbcTemplateMapperFactory.newInstance();
//    }
//    @Bean
//    public ResultSetExtractor<List<Order>> getResultSetExtractor(@Qualifier("orderMapper") JdbcTemplateMapperFactory jdbcTemplateMapperFactory) {
//        return jdbcTemplateMapperFactory.addKeys("secureId", "orderItems_phone_id")
//                .newResultSetExtractor(Order.class);
//    }
//
//    @Bean
//    @Qualifier("orderMapper")
//    public JdbcTemplateMapperFactory getJdbcTemplateMapperFactory() {
//        return JdbcTemplateMapperFactory.newInstance();
//    }

}
