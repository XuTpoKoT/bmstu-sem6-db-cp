package com.music_shop.DB.jdbc.mapper;

import com.music_shop.BL.model.DeliveryPoint;
import com.music_shop.BL.model.Order;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
    public class OrderMapper implements RowMapper<Order> {
    @Override
    public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID id = rs.getObject("order_id", java.util.UUID.class);
        String customerLogin = rs.getString("customer_login");
        String employeeLogin = rs.getString("employee_login");
        String statusStr = rs.getString("status");
        Order.Status status = Order.Status.valueOf(statusStr);
        Timestamp ts = rs.getTimestamp("date_");
        ZonedDateTime zonedDateTime = null;
        if (ts != null)
            zonedDateTime =  ZonedDateTime.ofInstant(Instant.ofEpochMilli(ts.getTime()), ZoneOffset.UTC);

        int initialCost = rs.getInt("initial_cost");
        int paidByBonuses = rs.getInt("paid_by_bonuses");
        DeliveryPoint deliveryPoint = new DeliveryPoint(rs.getObject("delivery_point_id", java.util.UUID.class),
                rs.getString("address"));

        return Order.builder().id(id).customerLogin(customerLogin).employeeLogin(employeeLogin)
                .date(zonedDateTime).status(status).initialCost(initialCost).paidByBonuses(paidByBonuses).
                deliveryPoint(deliveryPoint).build();
    }
}
