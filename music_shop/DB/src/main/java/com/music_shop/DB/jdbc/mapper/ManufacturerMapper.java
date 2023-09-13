package com.music_shop.DB.jdbc.mapper;

import com.music_shop.BL.model.Manufacturer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class ManufacturerMapper implements RowMapper<Manufacturer> {
    @Override
    public Manufacturer mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID id = rs.getObject("id", java.util.UUID.class);
        String name = rs.getString("name_");
        return new Manufacturer(id, name);
    }
}
