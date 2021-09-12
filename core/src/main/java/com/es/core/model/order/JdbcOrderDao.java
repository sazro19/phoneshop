package com.es.core.model.order;

import com.es.core.model.JdbcInsertClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@PropertySource("classpath:/config/errors.properties")
public class JdbcOrderDao implements OrderDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ResultSetExtractor<List<Order>> orderExtractor;

    @Autowired
    private JdbcInsertClass jdbcInsertClass;

    @Autowired
    private Environment environment;

    private static final String SELECT_ONE_BY_SECURE_ID_SQL_QUERY = "SELECT ordersFound.id AS id, " +
            "secureId, firstName, lastName, deliveryAddress, contactPhoneNo, additionalInformation, " +
            "subtotal, deliveryPrice, totalPrice, creationDate, status, " +
            "orderItems.id AS orderItems_id," +
            "orderItems.quantity AS orderItems_quantity, " +
            "phones.id AS orderItems_phone_id, " +
            "phones.brand AS orderItems_phone_brand, " +
            "phones.model AS orderItems_phone_model, " +
            "phones.price AS orderItems_phone_price, " +
            "phones.displaySizeInches AS orderItems_phone_displaySizeInches, " +
            "phones.weightGr AS orderItems_phone_weightGr, " +
            "phones.lengthMm AS orderItems_phone_lengthMm, " +
            "phones.widthMm AS orderItems_phone_widthMm, " +
            "phones.heightMm AS orderItems_phone_heightMm, " +
            "phones.announced AS orderItems_phone_announced, " +
            "phones.deviceType AS orderItems_phone_deviceType, " +
            "phones.os AS orderItems_phone_os, " +
            "phones.displayResolution AS orderItems_phone_displayResolution, " +
            "phones.pixelDensity AS orderItems_phone_pixelDensity, " +
            "phones.displayTechnology AS orderItems_phone_displayTechnology, " +
            "phones.backCameraMegapixels AS orderItems_phone_backCameraMegapixels, " +
            "phones.frontCameraMegapixels AS orderItems_phone_frontCameraMegapixels, " +
            "phones.ramGb AS orderItems_phone_ramGb, " +
            "phones.internalStorageGb AS orderItems_phone_internalStorageGb, " +
            "phones.batteryCapacityMah AS orderItems_phone_batteryCapacityMah, " +
            "phones.talkTimeHours AS orderItems_phone_talkTimeHours, " +
            "phones.standByTimeHours AS orderItems_phone_standByTimeHours, " +
            "phones.bluetooth AS orderItems_phone_bluetooth, " +
            "phones.positioning AS orderItems_phone_positioning, " +
            "phones.imageUrl AS orderItems_phone_imageUrl, " +
            "phones.description AS orderItems_phone_description, " +
            "colors.id AS orderItems_phone_colors_id, " +
            "colors.code AS orderItems_phone_colors_code " +
            "FROM (SELECT * FROM orders WHERE orders.secureId = ?) AS ordersFound " +
            "JOIN orderItems ON ordersFound.id = orderItems.orderId  " +
            "JOIN phones ON orderItems.phoneId = phones.id " +
            "JOIN phone2color ON phones.id = phone2color.phoneId " +
            "JOIN colors ON colors.id = phone2color.colorId";

    private static final String SELECT_ONE_BY_ID_SQL_QUERY = "SELECT ordersFound.id AS id, " +
            "secureId, firstName, lastName, deliveryAddress, contactPhoneNo, additionalInformation, " +
            "subtotal, deliveryPrice, totalPrice, creationDate, status, " +
            "orderItems.id AS orderItems_id," +
            "orderItems.quantity AS orderItems_quantity, " +
            "phones.id AS orderItems_phone_id, " +
            "phones.brand AS orderItems_phone_brand, " +
            "phones.model AS orderItems_phone_model, " +
            "phones.price AS orderItems_phone_price, " +
            "phones.displaySizeInches AS orderItems_phone_displaySizeInches, " +
            "phones.weightGr AS orderItems_phone_weightGr, " +
            "phones.lengthMm AS orderItems_phone_lengthMm, " +
            "phones.widthMm AS orderItems_phone_widthMm, " +
            "phones.heightMm AS orderItems_phone_heightMm, " +
            "phones.announced AS orderItems_phone_announced, " +
            "phones.deviceType AS orderItems_phone_deviceType, " +
            "phones.os AS orderItems_phone_os, " +
            "phones.displayResolution AS orderItems_phone_displayResolution, " +
            "phones.pixelDensity AS orderItems_phone_pixelDensity, " +
            "phones.displayTechnology AS orderItems_phone_displayTechnology, " +
            "phones.backCameraMegapixels AS orderItems_phone_backCameraMegapixels, " +
            "phones.frontCameraMegapixels AS orderItems_phone_frontCameraMegapixels, " +
            "phones.ramGb AS orderItems_phone_ramGb, " +
            "phones.internalStorageGb AS orderItems_phone_internalStorageGb, " +
            "phones.batteryCapacityMah AS orderItems_phone_batteryCapacityMah, " +
            "phones.talkTimeHours AS orderItems_phone_talkTimeHours, " +
            "phones.standByTimeHours AS orderItems_phone_standByTimeHours, " +
            "phones.bluetooth AS orderItems_phone_bluetooth, " +
            "phones.positioning AS orderItems_phone_positioning, " +
            "phones.imageUrl AS orderItems_phone_imageUrl, " +
            "phones.description AS orderItems_phone_description, " +
            "colors.id AS orderItems_phone_colors_id, " +
            "colors.code AS orderItems_phone_colors_code " +
            "FROM (SELECT * FROM orders WHERE orders.id = ?) AS ordersFound " +
            "JOIN orderItems ON ordersFound.id = orderItems.orderId  " +
            "JOIN phones ON orderItems.phoneId = phones.id " +
            "JOIN phone2color ON phones.id = phone2color.phoneId " +
            "JOIN colors ON colors.id = phone2color.colorId";

    private static final String SELECT_ALL_SQL_QUERY = "SELECT ordersFound.id AS id, " +
            "secureId, firstName, lastName, deliveryAddress, contactPhoneNo, additionalInformation, " +
            "subtotal, deliveryPrice, totalPrice, creationDate, status, " +
            "orderItems.id AS orderItems_id," +
            "orderItems.quantity AS orderItems_quantity, " +
            "phones.id AS orderItems_phone_id, " +
            "phones.brand AS orderItems_phone_brand, " +
            "phones.model AS orderItems_phone_model, " +
            "phones.price AS orderItems_phone_price, " +
            "phones.displaySizeInches AS orderItems_phone_displaySizeInches, " +
            "phones.weightGr AS orderItems_phone_weightGr, " +
            "phones.lengthMm AS orderItems_phone_lengthMm, " +
            "phones.widthMm AS orderItems_phone_widthMm, " +
            "phones.heightMm AS orderItems_phone_heightMm, " +
            "phones.announced AS orderItems_phone_announced, " +
            "phones.deviceType AS orderItems_phone_deviceType, " +
            "phones.os AS orderItems_phone_os, " +
            "phones.displayResolution AS orderItems_phone_displayResolution, " +
            "phones.pixelDensity AS orderItems_phone_pixelDensity, " +
            "phones.displayTechnology AS orderItems_phone_displayTechnology, " +
            "phones.backCameraMegapixels AS orderItems_phone_backCameraMegapixels, " +
            "phones.frontCameraMegapixels AS orderItems_phone_frontCameraMegapixels, " +
            "phones.ramGb AS orderItems_phone_ramGb, " +
            "phones.internalStorageGb AS orderItems_phone_internalStorageGb, " +
            "phones.batteryCapacityMah AS orderItems_phone_batteryCapacityMah, " +
            "phones.talkTimeHours AS orderItems_phone_talkTimeHours, " +
            "phones.standByTimeHours AS orderItems_phone_standByTimeHours, " +
            "phones.bluetooth AS orderItems_phone_bluetooth, " +
            "phones.positioning AS orderItems_phone_positioning, " +
            "phones.imageUrl AS orderItems_phone_imageUrl, " +
            "phones.description AS orderItems_phone_description, " +
            "colors.id AS orderItems_phone_colors_id, " +
            "colors.code AS orderItems_phone_colors_code " +
            "FROM (SELECT * FROM orders) AS ordersFound " +
            "JOIN orderItems ON ordersFound.id = orderItems.orderId  " +
            "JOIN phones ON orderItems.phoneId = phones.id " +
            "JOIN phone2color ON phones.id = phone2color.phoneId " +
            "JOIN colors ON colors.id = phone2color.colorId";

    private static final String UPDATE_ORDER_STATUS_SQL_QUERY = "UPDATE orders SET status = ? " +
            "WHERE id = ?";

    private static final String ORDER_ITEMS_TABLE_NAME = "orderItems";

    @Override
    public Optional<Order> get(String secureId) {
        List<Order> result = jdbcTemplate.query(SELECT_ONE_BY_SECURE_ID_SQL_QUERY, new Object[]{secureId}, orderExtractor);

        return getResultOrder(result);
    }

    @Override
    public Optional<Order> get(Long id) {
        List<Order> result = jdbcTemplate.query(SELECT_ONE_BY_ID_SQL_QUERY, new Object[]{id}, orderExtractor);

        return getResultOrder(result);
    }

    @Override
    public void save(Order order) {
        if (order.getId() == null) {
            insertOrder(order);
        } else {
            throw new IllegalStateException(environment.getProperty("error.orderHasAlreadyCreated"));
        }
    }

    @Override
    public List<Order> findAll() {
        return jdbcTemplate.query(SELECT_ALL_SQL_QUERY, orderExtractor);
    }

    @Override
    public void updateStatus(final Order order) {
        jdbcTemplate.update(UPDATE_ORDER_STATUS_SQL_QUERY, order.getStatus().toString(), order.getId());
    }

    private Optional<Order> getResultOrder(List<Order> orders) {
        if (orders.size() == 0) {
            return Optional.empty();
        }

        Order resultOrder = orders.get(0);
        resultOrder.getOrderItems().forEach(orderItem -> orderItem.setOrder(resultOrder));
        return Optional.of(resultOrder);
    }

    private void insertOrder(Order order) {
        saveOrder(order);
        saveOrderItems(order);
    }

    private void saveOrder(Order order) {
        Number newId = jdbcInsertClass.insertAndReturnGeneratedKey("orders",
                new BeanPropertySqlParameterSource(order), "id");
        order.setId(newId.longValue());
    }

    private void saveOrderItems(Order order) {
        order.getOrderItems().forEach(orderItem -> {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            simpleJdbcInsert.withTableName(ORDER_ITEMS_TABLE_NAME)
                    .execute(new MapSqlParameterSource()
                            .addValue("orderId", order.getId())
                            .addValue("phoneId", orderItem.getPhone().getId())
                            .addValue("quantity", orderItem.getQuantity())
                            .addValue("contactPhoneNo", order.getContactPhoneNo()));
        });
    }
}
