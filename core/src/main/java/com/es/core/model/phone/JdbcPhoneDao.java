package com.es.core.model.phone;

import com.es.core.model.CustomJdbcUtils;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.es.core.model.phone.PhoneFieldConstants.*;

@Component
public class JdbcPhoneDao implements PhoneDao {

    private JdbcTemplate jdbcTemplate;

    private CustomJdbcUtils customJdbcUtils;

    private final ResultSetExtractor<List<Phone>> resultSetExtractor = JdbcTemplateMapperFactory
            .newInstance().addKeys(PHONE_ID_FIELD)
            .newResultSetExtractor(Phone.class);

    private static final String SELECT_PHONE_BY_ID_QUERY = "SELECT phones.id AS id, brand, model, price, " +
            "displaySizeInches, weightGr, lengthMm, widthMm, heightMm, announced, deviceType, " +
            "os, displayResolution, pixelDensity, displayTechnology,backCameraMegapixels, " +
            "frontCameraMegapixels, ramGb, internalStorageGb, batteryCapacityMah, " +
            "talkTimeHours, standByTimeHours, bluetooth, positioning, imageUrl, description, " +
            "colors.id AS colors_id, " +
            "colors.code AS colors_code " +
            "FROM (SELECT " +
            "* FROM phones WHERE phones.id = ?) AS phones " +
            "LEFT JOIN phone2color ON phones.id = phone2color.phoneId " +
            "LEFT JOIN colors ON colors.id = phone2color.colorId ";

    private static final String SELECT_ALL_WITH_OFFSET_AND_LIMIT = "SELECT phonesWithColor.id AS id, brand, " +
            "model, price, displaySizeInches, weightGr, lengthMm, widthMm, heightMm, " +
            "announced, deviceType, os, displayResolution, pixelDensity, displayTechnology, " +
            "backCameraMegapixels, frontCameraMegapixels, ramGb, internalStorageGb, batteryCapacityMah, " +
            "talkTimeHours, standByTimeHours, bluetooth, positioning, imageUrl, description, " +
            "colors.id AS colors_id, colors.code AS colors_code FROM " +
            "(SELECT * FROM phones " +
            "WHERE phones.id NOT IN " +
            "(SELECT phones.id FROM phones " +
            "LEFT JOIN phone2color ON phones.id = phone2color.phoneId " +
            "WHERE phone2color.phoneId IS NULL) " +
            "LIMIT ?, ?) " +
            "AS phonesWithColor " +
            "JOIN phone2color ON phonesWithColor.id = phone2color.phoneId " +
            "JOIN colors ON colors.id = phone2color.colorId " +
            "ORDER BY phonesWithColor.id ";

    private static final String UPDATE_PHONE_QUERY = "UPDATE phones SET brand=:brand, model=:model, price=:price, " +
            "displaySizeInches=:displaySizeInches, weightGr=:weightGr, lengthMm=:lengthMm, widthMm=:widthMm, " +
            "heightMm=:heightMm, announced=:announced, deviceType=:deviceType, os=:os, displayResolution=:displayResolution, " +
            "pixelDensity=:pixelDensity, displayTechnology=:displayTechnology, backCameraMegapixels=:backCameraMegapixels, " +
            "frontCameraMegapixels=:frontCameraMegapixels, ramGb=:ramGb, internalStorageGb=:internalStorageGb, " +
            "batteryCapacityMah=:batteryCapacityMah, talkTimeHours=:talkTimeHours, standByTimeHours=:standByTimeHours, " +
            "bluetooth=:bluetooth, positioning=:positioning, imageUrl=:imageUrl, description=:description " +
            "WHERE phones.id=:id";


    private static final String PHONES_TABLE_NAME = "phones";

    private static final String PHONE2COLORS_TABLE_NAME = "phone2color";

    private final static String DELETE_PHONE2COLOR_RECORDS_SQL_QUERY = "DELETE FROM phone2color WHERE phoneId = ?";


    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.customJdbcUtils = new CustomJdbcUtils(jdbcTemplate);
    }

        @Transactional(readOnly = true)
    public Optional<Phone> get(final Long key) {
        List<Phone> result = jdbcTemplate.query(SELECT_PHONE_BY_ID_QUERY, resultSetExtractor, key);

        if (result != null) {
            if (!result.isEmpty()) {
                return Optional.of(result.get(0));
            }
        }
        return Optional.empty();
    }

    @Transactional(rollbackFor = DataAccessException.class)
    public void save(final Phone phone) {
        boolean isEntityExist = customJdbcUtils.isEntityExist(PHONES_TABLE_NAME,
                Map.of(BRAND_UNIQUE_FIELD, phone.getBrand(), MODEL_UNIQUE_FIELD, phone.getModel()));
        if (isEntityExist) {
            update(phone);
        } else {
            insertNewPhone(phone);
        }
    }

    @Transactional(readOnly = true)
    public List<Phone> findAll(int offset, int limit) {
        List<Phone> result = jdbcTemplate.query(SELECT_ALL_WITH_OFFSET_AND_LIMIT, resultSetExtractor, offset, limit);

        return result;
    }

    private void update(final Phone phone) {
        NamedParameterJdbcTemplate parameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        parameterJdbcTemplate.update(UPDATE_PHONE_QUERY, new BeanPropertySqlParameterSource(phone));
        refreshColors(phone);
    }

    private void insertNewPhone(Phone phone) {
        Long newId = customJdbcUtils.insertAndReturnGeneratedKey(PHONES_TABLE_NAME, new BeanPropertySqlParameterSource(phone),
                PHONE_ID_FIELD).longValue();
        phone.setId(newId);
        saveColors(phone);
    }

    private void refreshColors(final Phone phone) {
        jdbcTemplate.update(DELETE_PHONE2COLOR_RECORDS_SQL_QUERY, phone.getId());
        saveColors(phone);
    }

    private void saveColors(final Phone phone) {
        for (Color color : phone.getColors()) {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
            simpleJdbcInsert.withTableName(PHONE2COLORS_TABLE_NAME)
                    .execute(new MapSqlParameterSource()
                            .addValue("phoneId", phone.getId())
                            .addValue("colorId", color.getId()));
        }
    }
}
