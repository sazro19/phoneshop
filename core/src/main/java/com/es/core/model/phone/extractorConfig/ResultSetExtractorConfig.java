package com.es.core.model.phone.extractorConfig;

import com.es.core.model.phone.Phone;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.util.List;

import static com.es.core.model.phone.PhoneFieldConstants.PHONE_ID_FIELD;

@Configuration
public class ResultSetExtractorConfig {

    @Bean
    public ResultSetExtractor<List<Phone>> getResultSetExtractor(JdbcTemplateMapperFactory jdbcTemplateMapperFactory) {
        return jdbcTemplateMapperFactory.addKeys(PHONE_ID_FIELD)
                .newResultSetExtractor(Phone.class);
    }

    @Bean
    public JdbcTemplateMapperFactory getJdbcTemplateMapperFactory() {
        return JdbcTemplateMapperFactory.newInstance();
    }
}
