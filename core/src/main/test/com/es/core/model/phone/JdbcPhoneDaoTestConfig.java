package com.es.core.model.phone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
@ComponentScan("com.es.core.model.phone")
@PropertySource("classpath:/test-config/test.properties")
public class JdbcPhoneDaoTestConfig {

    @Autowired
    private Environment environment;

    @Qualifier("test")
    @Bean
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getProperty("db.driver"));
        dataSource.setUrl(environment.getProperty("db.url"));
        dataSource.setUsername(environment.getProperty("db.user"));
        dataSource.setPassword(environment.getProperty("db.password"));

        Resource initSchema = new ClassPathResource("db/schema.sql");
        Resource testData = new ClassPathResource("db/test-demodata.sql");
        DatabasePopulator databasePopulator = new ResourceDatabasePopulator(initSchema, testData);
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);
        return dataSource;
    }

    @Bean
    public JdbcTemplate getJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public JdbcPhoneDao jdbcPhoneDao(@Qualifier("test") DataSource testDataSource) {
        JdbcPhoneDao jdbcPhoneDao = new JdbcPhoneDao();
        jdbcPhoneDao.setDataSource(testDataSource);
        return jdbcPhoneDao;
    }
}
