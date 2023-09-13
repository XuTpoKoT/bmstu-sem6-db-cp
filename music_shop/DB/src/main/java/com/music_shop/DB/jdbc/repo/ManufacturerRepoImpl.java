package com.music_shop.DB.jdbc.repo;

import com.music_shop.BL.exception.DBException;
import com.music_shop.BL.model.Manufacturer;
import com.music_shop.DB.API.ManufacturerRepo;
import com.music_shop.DB.jdbc.mapper.ManufacturerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class ManufacturerRepoImpl implements ManufacturerRepo {
    private static final String SQL_GET_ALL_MANUFACTURERS = """
        SELECT *
        FROM public.manufacturer
    """;
    private static final String SQL_SET_ROLE_ADMIN = """
        SET ROLE admin_;
    """;
    private static final String SQL_GET_Manufacturer_BY_ID = """
        SELECT *
        FROM public.manufacturer
        WHERE id = :id 
    """;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ManufacturerMapper manufacturerMapper;

    @Autowired
    public ManufacturerRepoImpl(NamedParameterJdbcTemplate jdbcTemplate, ManufacturerMapper manufacturerMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.manufacturerMapper = manufacturerMapper;
    }
    @Override
    public List<Manufacturer> getAllManufacturers() {
        MapSqlParameterSource params = new MapSqlParameterSource();
        List<Manufacturer> manufacturers = new ArrayList<>();
        try {
            jdbcTemplate.update(SQL_SET_ROLE_ADMIN, new MapSqlParameterSource());
            manufacturers = jdbcTemplate.query(SQL_GET_ALL_MANUFACTURERS, params, manufacturerMapper)
                    .stream().toList();
            System.out.println(manufacturers);
        } catch (IncorrectResultSizeDataAccessException e) {
            return manufacturers;
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }

        return manufacturers;
    }

    @Override
    public Manufacturer getManufacturerByID(UUID id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        Manufacturer manufacturer;
        try {
            jdbcTemplate.update(SQL_SET_ROLE_ADMIN, new MapSqlParameterSource());
            manufacturer = jdbcTemplate.queryForObject(SQL_GET_Manufacturer_BY_ID, params, manufacturerMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }

        return manufacturer;
    }
}
