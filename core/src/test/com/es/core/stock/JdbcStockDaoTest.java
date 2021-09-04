package com.es.core.stock;

import com.es.core.config.TestConfig;
import com.es.core.model.phone.stock.Stock;
import com.es.core.model.phone.stock.StockDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class JdbcStockDaoTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private StockDao stockDao;

    private final List<Long> idList = Arrays.asList(1000L, 1001L, 1002L, 1003L, 1004L, 1005L, 1006L);

    @Before
    public void refresh() {
        Resource initSchema = new ClassPathResource("db/schema.sql");
        Resource testData = new ClassPathResource("db/test-demodata.sql");
        DatabasePopulator databasePopulator = new ResourceDatabasePopulator(initSchema, testData);
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);
    }

    @Test
    public void getTest() {
        Stock stock = stockDao.get(idList.get(0)).orElse(new Stock());

        assertEquals(0, stock.getStock().intValue());
        assertEquals(0, stock.getReserved().intValue());
    }

    @Test
    public void updateTest() {
        Stock stock = stockDao.get(idList.get(1)).orElse(new Stock());
        stock.setStock(5);

        stockDao.update(stock);

        stock = stockDao.get(idList.get(1)).orElse(new Stock());

        assertEquals(5, stock.getStock().intValue());

        stock.setStock(0);

        stockDao.update(stock);

        assertEquals(1, stock.getStock().intValue());
        assertEquals(0, stock.getReserved().intValue());
    }


}
