package com.es.core.cart;

import com.es.core.config.TestConfig;
import com.es.core.model.phone.Color;
import com.es.core.model.phone.Phone;
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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class HttpSessionCartServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;

    @Autowired
    private CartService cartService;

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
    public void getCartTest() {
        HttpSession firstSession = new MockHttpSession();
        HttpSession secondSession = new MockHttpSession();
        Cart firstCart = cartService.getCart(firstSession);
        Cart secondCart = cartService.getCart(secondSession);

        assertTrue(firstCart.getItemList().isEmpty());
        assertNotEquals(firstCart, secondCart);

        Phone phone = idPhoneMap.get(idList.get(3));
        cartService.addPhone(firstCart, phone.getId(), 2L);

        assertEquals(1, cartService.getCart(firstSession).getItemList().size());
    }

    @Test
    public void addPhoneTest() {
        HttpSession session = new MockHttpSession();
        Cart cart = cartService.getCart(session);

        Phone phone = idPhoneMap.get(idList.get(3));
        BigDecimal price = phone.getPrice();
        long quantity = 2L;
        BigDecimal expectedPrice = price.multiply(BigDecimal.valueOf(quantity));

        cartService.addPhone(cart, phone.getId(), quantity);

        assertEquals(1, cartService.getCart(session).getItemList().size());
        assertEquals(expectedPrice, cart.getTotalCost());
        assertEquals(quantity, cart.getTotalQuantity());
    }
}
