package com.music_shop.DB.jdbc.mapper;

import com.music_shop.BL.model.Card;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CardMapper implements RowMapper<Card> {
    @Override
    public Card mapRow(ResultSet rs, int rowNum) throws SQLException {
        String customerLogin = rs.getString("user_login");
        int bonuses = rs.getInt("bonuses");
        return new Card(customerLogin, bonuses);
    }
}
