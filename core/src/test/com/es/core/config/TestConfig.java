package com.es.core.config;

import com.es.core.cart.CartService;
import com.es.core.cart.HttpSessionCartService;
import com.es.core.model.JdbcInsertClass;
import com.es.core.model.order.JdbcOrderDao;
import com.es.core.model.order.OrderDao;
import com.es.core.model.order.extractorConfig.OrderExtractorConfig;
import com.es.core.model.phone.JdbcPhoneDao;
import com.es.core.model.phone.stock.JdbcStockDao;
import com.es.core.model.phone.stock.StockDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
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
@Import(OrderExtractorConfig.class)
@ComponentScan("com.es.core.model.phone")
@PropertySource("classpath:/test-config/test.properties")
public class TestConfig {

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
    public JdbcPhoneDao jdbcPhoneDao() {
        return new JdbcPhoneDao();
    }

    @Bean
    public CartService getCartService(JdbcPhoneDao phoneDao) {
        HttpSessionCartService cartService = new HttpSessionCartService();
        cartService.setPhoneDao(phoneDao);
        return cartService;
    }

    @Bean
    public JdbcInsertClass jdbcInsertClass() {
        return new JdbcInsertClass();
    }

    @Bean
    public OrderDao orderDao() {
        return new JdbcOrderDao();
    }

    @Bean
    public StockDao stockDao() {
        return new JdbcStockDao();
    }
}
