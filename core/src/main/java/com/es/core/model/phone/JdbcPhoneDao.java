package com.es.core.model.phone;

import com.es.core.model.sort.SortCriteria;
import com.es.core.model.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class JdbcPhoneDao implements PhoneDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ResultSetExtractor<List<Phone>> resultSetExtractor;

    private static final String SELECT_PHONE_BY_ID_QUERY = "SELECT phones.id AS id, brand, model, price, " +
            "displaySizeInches, weightGr, lengthMm, widthMm, heightMm, announced, deviceType, " +
            "os, displayResolution, pixelDensity, displayTechnology,backCameraMegapixels, " +
            "frontCameraMegapixels, ramGb, internalStorageGb, batteryCapacityMah, " +
            "talkTimeHours, standByTimeHours, bluetooth, positioning, imageUrl, description, " +
            "colors.id AS colors_id, " +
            "colors.code AS colors_code, " +
            "stocks.stock AS stock " +
            "FROM (SELECT " +
            "* FROM phones WHERE phones.id = ?) AS phones " +
            "LEFT JOIN phone2color ON phones.id = phone2color.phoneId " +
            "LEFT JOIN colors ON colors.id = phone2color.colorId " +
            "LEFT JOIN stocks ON phones.id = stocks.phoneId";

    private static final String SELECT_ALL_WITH_OFFSET_AND_LIMIT = "SELECT phonesWithColor.id AS id, brand, " +
            "model, price, displaySizeInches, weightGr, lengthMm, widthMm, heightMm, " +
            "announced, deviceType, os, displayResolution, pixelDensity, displayTechnology, " +
            "backCameraMegapixels, frontCameraMegapixels, ramGb, internalStorageGb, batteryCapacityMah, " +
            "talkTimeHours, standByTimeHours, bluetooth, positioning, imageUrl, description, " +
            "colors.id AS colors_id, colors.code AS colors_code " +
            "FROM " +
            "(SELECT * FROM phones " +
            "WHERE phones.id NOT IN " +
            "(SELECT phones.id FROM phones " +
            "LEFT JOIN phone2color ON phones.id = phone2color.phoneId " +
            "WHERE phone2color.phoneId IS NULL) AND phones.price IS NOT NULL " +
            "LIMIT ?, ?) " +
            "AS phonesWithColor " +
            "JOIN phone2color ON phonesWithColor.id = phone2color.phoneId " +
            "JOIN colors ON colors.id = phone2color.colorId " +
            "JOIN stocks ON phonesWithColor.id = stocks.phoneId " +
            "WHERE stocks.stock > 0 " +
            "ORDER BY phonesWithColor.id ";

    private static final String SELECT_ALL_WITH_SEARCH_QUERY = "SELECT phonesWithColor.id AS id, brand, " +
            "model, price, displaySizeInches, weightGr, lengthMm, widthMm, heightMm, " +
            "announced, deviceType, os, displayResolution, pixelDensity, displayTechnology, " +
            "backCameraMegapixels, frontCameraMegapixels, ramGb, internalStorageGb, batteryCapacityMah, " +
            "talkTimeHours, standByTimeHours, bluetooth, positioning, imageUrl, description, " +
            "colors.id AS colors_id, colors.code AS colors_code, " +
            "stocks.stock AS stock " +
            "FROM " +
            "(SELECT * FROM phones " +
            "WHERE phones.id NOT IN " +
            "(SELECT phones.id FROM phones " +
            "LEFT JOIN phone2color ON phones.id = phone2color.phoneId " +
            "WHERE phone2color.phoneId IS NULL) AND phones.price IS NOT NULL " +
            "AND CONCAT(LOWER(phones.model), LOWER(phones.description)) LIKE '%s' " +
            "ORDER BY phones.%s %s " +
            "LIMIT ?, ?) " +
            "AS phonesWithColor " +
            "JOIN phone2color ON phonesWithColor.id = phone2color.phoneId " +
            "JOIN colors ON colors.id = phone2color.colorId " +
            "JOIN stocks ON phonesWithColor.id = stocks.phoneId " +
            "WHERE stocks.stock > 0 ";

    private static final String UPDATE_PHONE_QUERY = "UPDATE phones SET brand=:brand, model=:model, price=:price, " +
            "displaySizeInches=:displaySizeInches, weightGr=:weightGr, lengthMm=:lengthMm, widthMm=:widthMm, " +
            "heightMm=:heightMm, announced=:announced, deviceType=:deviceType, os=:os, displayResolution=:displayResolution, " +
            "pixelDensity=:pixelDensity, displayTechnology=:displayTechnology, backCameraMegapixels=:backCameraMegapixels, " +
            "frontCameraMegapixels=:frontCameraMegapixels, ra   mGb=:ramGb, internalStorageGb=:internalStorageGb, " +
            "batteryCapacityMah=:batteryCapacityMah, talkTimeHours=:talkTimeHours, standByTimeHours=:standByTimeHours, " +
            "bluetooth=:bluetooth, positioning=:positioning, imageUrl=:imageUrl, description=:description " +
            "WHERE phones.id=:id";

    private static final String COUNT_ALL_VALID_PHONES_SQL_QUERY = "SELECT COUNT(*) " +
            "FROM (SELECT * FROM phones " +
            "WHERE phones.id NOT IN " +
            "(SELECT phones.id FROM phones " +
            "LEFT JOIN phone2color ON phones.id = phone2color.phoneId " +
            "WHERE phone2color.phoneId IS NULL) AND phones.price IS NOT NULL " +
            "AND CONCAT(LOWER(phones.model), LOWER(phones.description)) LIKE '%s' " +
            "ORDER BY phones.%s %s) " +
            "AS phonesWithColor " +
            "JOIN stocks ON phonesWithColor.id = stocks.phoneId " +
            "WHERE stocks.stock > 0 ";


    private static final String PHONES_TABLE_NAME = "phones";

    private static final String PHONE2COLORS_TABLE_NAME = "phone2color";

    private final static String DELETE_PHONE2COLOR_RECORDS_SQL_QUERY = "DELETE FROM phone2color WHERE phoneId = ?";

    @Override
    @Transactional(readOnly = true)
    public Optional<Phone> get(final Long key) {
        List<Phone> result = jdbcTemplate.query(SELECT_PHONE_BY_ID_QUERY, resultSetExtractor, key);

        if (result != null && !result.isEmpty()) {
            return Optional.of(result.get(0));
        }
        return Optional.empty();
    }

    @Override
    @Transactional(rollbackFor = DataAccessException.class)
    public void save(final Phone phone) {
        if (phone.getId() == null) {
            insertNewPhone(phone);
        }
        update(phone);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Phone> findAll(int offset, int limit) {
        List<Phone> result = jdbcTemplate.query(SELECT_ALL_WITH_OFFSET_AND_LIMIT, resultSetExtractor, offset, limit);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Phone> findAll(ParamWrapper wrapper) {
        String resultQuery = String.format(SELECT_ALL_WITH_SEARCH_QUERY, getSearchPattern(wrapper.getQuery()),
                wrapper.getSortCriteria().getValue(), wrapper.getSortOrder());

        List<Phone> result = jdbcTemplate.query(resultQuery, resultSetExtractor, wrapper.getOffset(), wrapper.getLimit());
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public int getRecordsQuantity(String query, SortCriteria sortCriteria, SortOrder sortOrder) {
        String resultQuery = String.format(COUNT_ALL_VALID_PHONES_SQL_QUERY, getSearchPattern(query),
                sortCriteria.getValue(), sortOrder);
        return jdbcTemplate.queryForObject(resultQuery, Integer.class);
    }

    private String getSearchPattern(String query) {
        String[] processedTerms = query.toLowerCase().replaceAll("[\\s]{2,}", " ").split(" ");
        List<String> terms = Arrays
                .stream(processedTerms)
                .collect(Collectors.toList());
        String result = "%" + String.join("%", terms) + "%";
        return result;
    }

    private void update(final Phone phone) {
        NamedParameterJdbcTemplate parameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        parameterJdbcTemplate.update(UPDATE_PHONE_QUERY, new BeanPropertySqlParameterSource(phone));
        refreshColors(phone);
    }

    private void insertNewPhone(Phone phone) {
        Long newId = insertAndReturnGeneratedKey(new BeanPropertySqlParameterSource(phone)).longValue();
        phone.setId(newId);
    }

    private Number insertAndReturnGeneratedKey(SqlParameterSource parameters) {
        return new SimpleJdbcInsert(jdbcTemplate)
                .withTableName(PHONES_TABLE_NAME)
                .usingGeneratedKeyColumns(PhoneFieldConstants.PHONE_ID_FIELD)
                .executeAndReturnKey(parameters);
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
