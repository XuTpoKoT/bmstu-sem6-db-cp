package com.music_shop.DB.jdbc.mapper;

import com.music_shop.BL.model.DeliveryPoint;
import com.music_shop.BL.model.Manufacturer;
import com.music_shop.BL.model.Product;
import org.json.JSONObject;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class ProductMapper implements RowMapper<Product> {
    @Override
    @Nullable
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        UUID id = rs.getObject("id", java.util.UUID.class);
        String name = rs.getString("pname");
        int price = rs.getInt("price");
        String description = rs.getString("description");
        String color = rs.getString("color");
        int stogageCnt = rs.getInt("storage_cnt");
        String imgRef = rs.getString("img_ref");

        String characteristicsStr  = rs.getString("characteristics");
        Map<String, String> characteristicsMap = new HashMap<>();
        if (characteristicsStr != null) {
            JSONObject json = new JSONObject(characteristicsStr);
            Map<String, Object> map = json.toMap();
            for (String key : map.keySet()) {
                characteristicsMap.put(key, map.get(key).toString());
            }
        }
        Manufacturer manufacturer = new Manufacturer(rs.getObject("manufacturer_id", java.util.UUID.class),
                rs.getString("mname"));

        return Product.builder().id(id).name(name).price(price).storageCnt(stogageCnt).description(description)
                .color(color).imgRef(imgRef).manufacturer(manufacturer).characteristics(characteristicsMap).build();
    }

}
