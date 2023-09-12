package com.music_shop.DB.jdbc.mapper;

import com.music_shop.DB.jdbc.model.OrderDetails;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class OrderDetailsMapper implements RowMapper<OrderDetails> {
    @Override
    public OrderDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID orderId = rs.getObject("order_id", java.util.UUID.class);
        UUID productId = rs.getObject("product_id", java.util.UUID.class);
        int cntProducts = rs.getInt("cnt_products");
        int price = rs.getInt("price");
        return new OrderDetails(orderId, productId, cntProducts, price);
    }
}
