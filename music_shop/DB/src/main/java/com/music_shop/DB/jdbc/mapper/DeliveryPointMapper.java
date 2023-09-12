package com.music_shop.DB.jdbc.mapper;

import com.music_shop.BL.model.Card;
import com.music_shop.BL.model.DeliveryPoint;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class DeliveryPointMapper implements RowMapper<DeliveryPoint> {
    @Override
    public DeliveryPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID id = rs.getObject("id", java.util.UUID.class);
        String address = rs.getString("address");
        return new DeliveryPoint(id, address);
    }
}
