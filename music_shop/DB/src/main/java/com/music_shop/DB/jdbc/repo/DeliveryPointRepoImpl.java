package com.music_shop.DB.jdbc.repo;

import com.music_shop.BL.exception.DBException;
import com.music_shop.BL.model.DeliveryPoint;
import com.music_shop.DB.API.DeliveryPointRepo;
import com.music_shop.DB.jdbc.mapper.DeliveryPointMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class DeliveryPointRepoImpl implements DeliveryPointRepo {
    private static final String SQL_GET_ALL_DELIVERY_POINTS = """
        SELECT *
        FROM public.deliverypoint
    """;
    private static final String SQL_GET_DELIVERY_POINT_BY_ID = """
        SELECT *
        FROM public.deliverypoint
        WHERE id = :id 
    """;
    private static final String SQL_SET_ROLE_UNREGISTERED = """
        SET ROLE unregistered;
    """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DeliveryPointMapper deliveryPointMapper;

    @Autowired
    public DeliveryPointRepoImpl(NamedParameterJdbcTemplate jdbcTemplate, DeliveryPointMapper deliveryPointMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.deliveryPointMapper = deliveryPointMapper;
    }

    @Override
    public List<DeliveryPoint> getAllDeliveryPoints() {
        MapSqlParameterSource params = new MapSqlParameterSource();
        List<DeliveryPoint> deliveryPoints = new ArrayList<>();
        try {
            jdbcTemplate.update(SQL_SET_ROLE_UNREGISTERED, new MapSqlParameterSource());
            deliveryPoints = jdbcTemplate.query(SQL_GET_ALL_DELIVERY_POINTS, params, deliveryPointMapper)
                    .stream().toList();
        } catch (IncorrectResultSizeDataAccessException e) {
            return deliveryPoints;
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }

        return deliveryPoints;
    }

    @Override
    public DeliveryPoint getDeliveryPointByID(UUID id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        DeliveryPoint deliveryPoint;
        try {
            jdbcTemplate.update(SQL_SET_ROLE_UNREGISTERED, new MapSqlParameterSource());
            deliveryPoint = jdbcTemplate.queryForObject(SQL_GET_DELIVERY_POINT_BY_ID, params, deliveryPointMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }

        return deliveryPoint;
    }
}
