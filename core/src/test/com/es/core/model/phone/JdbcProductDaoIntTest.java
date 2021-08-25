package com.es.core.model.phone;

import com.es.core.config.TestConfig;
import com.es.core.model.sort.SortCriteria;
import com.es.core.model.sort.SortOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class JdbcProductDaoIntTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;

    @Autowired
    private PhoneDao jdbcPhoneDao;

    private static final String SELECT_BY_ID = "SELECT * FROM phones WHERE phones.id = ?";
    private static final String SELECT_COLORS_BY_PHONE_ID = "SELECT colors.id, colors.code FROM phone2color " +
            "JOIN colors ON phone2color.colorId = colors.id " +
            "WHERE phoneId = ?";

    private final List<Long> idList = Arrays.asList(1000L, 1001L, 1002L, 1003L, 1004L, 1005L, 1006L);
    private Map<Long, Phone> idPhoneMap;

    @Before
    public void refresh() {
        Resource initSchema = new ClassPathResource("db/schema.sql");
        Resource testData = new ClassPathResource("db/test-demodata.sql");
        DatabasePopulator databasePopulator = new ResourceDatabasePopulator(initSchema, testData);
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);

        idPhoneMap = new LinkedHashMap<>();
        for (long id : idList) {
            Phone testPhone = jdbcTemplate.queryForObject(SELECT_BY_ID,
                    new BeanPropertyRowMapper<>(Phone.class), id);
            List<Color> testPhoneColors = jdbcTemplate.query(SELECT_COLORS_BY_PHONE_ID, new Object[]{id},
                    new BeanPropertyRowMapper<>(Color.class));
            testPhone.setColors(new HashSet<>(testPhoneColors));
            idPhoneMap.put(id, testPhone);
        }
    }

    @Test
    public void getByIdTest() {
        long testId = idList.get(0);

        Phone expected = idPhoneMap.get(testId);

        Phone actual = jdbcPhoneDao.get(testId).get();

        assertEquals(expected, actual);
        assertEquals(expected.getColors(), actual.getColors());
    }

    @Test
    public void findAllTest() {
        List<Phone> actual = jdbcPhoneDao.findAll(0, idPhoneMap.size());

        assertEquals(3, actual.size());
        actual.forEach(phone -> assertNotNull(phone.getPrice()));
    }

    @Test
    public void findAllWithQueryTest() {
        String query = "ARCHOS";
        SortCriteria sortCriteria = SortCriteria.PRICE;
        SortOrder sortOrder = SortOrder.ASC;
        int offset = 0;
        int limit = idPhoneMap.size();

        List<Phone> actual = jdbcPhoneDao.findAll(new ParamWrapper(query, sortCriteria, sortOrder, offset, limit));

        assertEquals(BigDecimal.valueOf(123.), actual.get(0).getPrice());
        assertEquals(BigDecimal.valueOf(228.), actual.get(1).getPrice());
        assertEquals(BigDecimal.valueOf(322.), actual.get(2).getPrice());
    }

    @Test
    public void saveExistingPhoneTest() {
        Phone testPhone = jdbcPhoneDao.get(1000L).get();
        testPhone.setDescription("newDescription");
        Color color = new Color();
        color.setId(1000L);
        color.setCode("Black");
        Set<Color> colorSet = new HashSet<>();
        colorSet.add(color);
        testPhone.setColors(colorSet);

        int rowsNumberBeforeSaving = JdbcTestUtils.countRowsInTable(jdbcTemplate, "phones");

        jdbcPhoneDao.save(testPhone);

        int rowsNumberAfterSaving = JdbcTestUtils.countRowsInTable(jdbcTemplate, "phones");

        assertEquals(rowsNumberBeforeSaving, rowsNumberAfterSaving);
        assertEquals(testPhone, jdbcPhoneDao.get(1000L).get());
    }

    @Test
    public void saveNewPhoneTest() {
        Phone testPhone = new Phone();
        testPhone.setBrand("expectedBrand");
        testPhone.setModel("expectedModel");

        int quantityBeforeSaving = JdbcTestUtils.countRowsInTable(jdbcTemplate, "phones");

        jdbcPhoneDao.save(testPhone);

        int quantityAfterAdding = JdbcTestUtils.countRowsInTable(jdbcTemplate, "phones");

        assertEquals(quantityBeforeSaving, quantityAfterAdding - 1);
    }

    @Test
    public void getRecordsQuantityTest() {
        assertEquals(3, jdbcPhoneDao.getRecordsQuantity("ARCHOS", SortCriteria.BRAND, SortOrder.ASC));
    }
}
