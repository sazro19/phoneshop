package com.es.core.order;

import com.es.core.config.TestConfig;
import com.es.core.model.JdbcInsertClass;
import com.es.core.model.order.Order;
import com.es.core.model.order.OrderDao;
import com.es.core.model.order.OrderItem;
import com.es.core.model.order.OrderStatus;
import com.es.core.model.phone.Phone;
import com.es.core.model.phone.PhoneDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class JdbcOrderDaoTest {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private PhoneDao phoneDao;
    @Autowired
    private JdbcInsertClass jdbcInsertClass;

    private final List<Long> idList = Arrays.asList(1000L, 1001L, 1002L, 1003L, 1004L, 1005L, 1006L);

    private Order savedOrder = new Order();

    @Before
    public void refresh() {
        Resource initSchema = new ClassPathResource("db/schema.sql");
        Resource testData = new ClassPathResource("db/test-demodata.sql");
        DatabasePopulator databasePopulator = new ResourceDatabasePopulator(initSchema, testData);
        DatabasePopulatorUtils.execute(databasePopulator, dataSource);

        savedOrder = new Order();
        savedOrder.setFirstName("firstName");
        savedOrder.setLastName("lastName");
        savedOrder.setDeliveryAddress("address");
        savedOrder.setContactPhoneNo("+375(77)7777777");
        savedOrder.setAdditionalInformation("info");
        savedOrder.setStatus(OrderStatus.NEW);
        savedOrder.setCreationDate(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        idList.forEach(id -> {
            Phone testPhone = phoneDao.get(id).get();
            if (testPhone.getStock() > 0) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(savedOrder);
                orderItem.setPhone(testPhone);
                orderItem.setQuantity(testPhone.getStock().longValue());

                orderItems.add(orderItem);
            }
        });
        savedOrder.setOrderItems(orderItems);
        savedOrder.setSecureId(UUID.randomUUID().toString());
        savedOrder.setDeliveryPrice(new BigDecimal(5));
        savedOrder.setSubtotal(new BigDecimal(100));
        savedOrder.setTotalPrice(savedOrder.getSubtotal().add(savedOrder.getDeliveryPrice()));

        Number newId = jdbcInsertClass.insertAndReturnGeneratedKey("orders",
                new BeanPropertySqlParameterSource(savedOrder), "id");
        savedOrder.setId(newId.longValue());
        savedOrder.getOrderItems().forEach(orderItem -> {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            simpleJdbcInsert.withTableName("orderItems")
                    .execute(new MapSqlParameterSource()
                            .addValue("orderId", savedOrder.getId())
                            .addValue("phoneId", orderItem.getPhone().getId())
                            .addValue("quantity", orderItem.getQuantity())
                            .addValue("contactPhoneNo", savedOrder.getContactPhoneNo()));
        });
    }

    @Test
    public void getTest() {
        Order order = orderDao.get(savedOrder.getSecureId()).orElse(new Order());

        assertEquals(savedOrder.getId(), order.getId());
        assertEquals(savedOrder.getSecureId(), order.getSecureId());

        order = orderDao.get(savedOrder.getId()).orElse(new Order());

        assertEquals(savedOrder.getId(), order.getId());
        assertEquals(savedOrder.getSecureId(), order.getSecureId());
    }

    @Test
    public void saveTest() {
        Order order = orderDao.get(savedOrder.getSecureId()).orElse(new Order());

        savedOrder.setId(null);
        savedOrder.setSecureId(UUID.randomUUID().toString());

        orderDao.save(savedOrder);

        assertNotEquals(savedOrder.getId(), order.getId());
        assertNotEquals(savedOrder.getSecureId(), order.getSecureId());

        order = orderDao.get(savedOrder.getSecureId()).orElse(new Order());

        assertEquals(savedOrder.getId(), order.getId());
        assertEquals(savedOrder.getSecureId(), order.getSecureId());
    }

    @Test
    public void findAllTest() {
        assertEquals(Collections.singletonList(savedOrder), orderDao.findAll());

        Order order = new Order();
        order.setFirstName("firstName");
        order.setLastName("lastName");
        order.setDeliveryAddress("address");
        order.setContactPhoneNo("+375(77)7777777");
        order.setAdditionalInformation("info");
        order.setStatus(OrderStatus.NEW);
        order.setCreationDate(LocalDateTime.now());
        order.setSecureId(UUID.randomUUID().toString());
        order.setOrderItems(savedOrder.getOrderItems());

        orderDao.save(order);

        assertEquals(Arrays.asList(savedOrder, order), orderDao.findAll());
    }
}
