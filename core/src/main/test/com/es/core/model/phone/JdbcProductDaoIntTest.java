package com.es.core.model.phone;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;
import java.util.*;


@ContextConfiguration(classes = JdbcProductDaoIntTestConfig.class)
@ExtendWith(SpringExtension.class)
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
    private final static String SELECT_CORRESPONDING_COLORS = "SELECT colors.id, colors.code FROM " +
            "(SELECT * FROM phone2color WHERE phoneId = ?) AS corresponding_phone2color " +
            "JOIN colors " +
            "ON corresponding_phone2color.colorId = colors.id";

    private final List<Long> idList = Arrays.asList(1000L, 1001L, 1002L, 1003L, 1004L);
    private Map<Long, Phone> idPhoneMap;

    @BeforeEach
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
        for (long id : idList) {
            Phone expected = idPhoneMap.get(id);
            Phone actual = jdbcPhoneDao.get(id).get();
            Assertions.assertEquals(expected, actual);
        }
    }

    @Test
    public void findAllTest() {
        List<Phone> expected = new ArrayList<>(idPhoneMap.values());
        List<Phone> actual = jdbcPhoneDao.findAll(0, idPhoneMap.size());
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void saveNewPhoneWithColorsTest() {
        String query = "SELECT * FROM phones WHERE brand = 'expectedBrand' and model = 'expectedModel'";

        Phone expectedPhone = new Phone();
        Color color1 = new Color();
        color1.setId(1000L);
        color1.setCode("Black");
        Color color2 = new Color();
        color2.setId(1001L);
        color2.setCode("White");

        expectedPhone.setBrand("expectedBrand");
        expectedPhone.setModel("expectedModel");
        expectedPhone.setColors(Set.of(color1, color2));

        jdbcPhoneDao.save(expectedPhone);

        Phone actualPhone = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Phone.class));
        List<Color> actualColors = jdbcTemplate.query(SELECT_CORRESPONDING_COLORS, new Object[]{expectedPhone.getId()},
                new BeanPropertyRowMapper<>(Color.class));
        actualPhone.setColors(Set.copyOf(actualColors));

        Assertions.assertEquals(expectedPhone, actualPhone);
    }

    @Test
    public void saveNewPhoneTest() {
        Phone actualPhone = new Phone();
        actualPhone.setBrand("expectedBrand");
        actualPhone.setModel("expectedModel");
        int quantityBeforeSaving = JdbcTestUtils.countRowsInTable(jdbcTemplate, "phones");
        jdbcPhoneDao.save(actualPhone);
        int quantityAfterAdding = JdbcTestUtils.countRowsInTable(jdbcTemplate, "phones");
        Assertions.assertEquals(quantityBeforeSaving, quantityAfterAdding - 1);
    }

    @Test
    public void saveExistingPhoneTest() {
        String query = "SELECT * FROM phones WHERE phones.id = ?";

        Phone expectedPhone = idPhoneMap.get(1000L);
        Phone actualPhone = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Phone.class), 1000L);
        List<Color> actualColors = jdbcTemplate.query(SELECT_CORRESPONDING_COLORS, new Object[]{expectedPhone.getId()},
                new BeanPropertyRowMapper<>(Color.class));
        actualPhone.setColors(Set.copyOf(actualColors));

        Assertions.assertEquals(expectedPhone, actualPhone);

        String updateTestString = "updated";
        expectedPhone.setDescription(updateTestString);

        Assertions.assertNotEquals(expectedPhone.getDescription(), actualPhone.getDescription());

        int quantityBeforeSaving = JdbcTestUtils.countRowsInTable(jdbcTemplate, "phones");
        jdbcPhoneDao.save(expectedPhone);
        int quantityAfterAdding = JdbcTestUtils.countRowsInTable(jdbcTemplate, "phones");
        Assertions.assertEquals(quantityBeforeSaving, quantityAfterAdding);

        actualPhone = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Phone.class), 1000L);
        actualColors = jdbcTemplate.query(SELECT_CORRESPONDING_COLORS, new Object[]{expectedPhone.getId()},
                new BeanPropertyRowMapper<>(Color.class));
        actualPhone.setColors(Set.copyOf(actualColors));
        Assertions.assertEquals(expectedPhone, actualPhone);
    }
}
