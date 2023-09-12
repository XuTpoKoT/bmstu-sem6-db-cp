package com.music_shop.DB.jdbc.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class OrderDetails {
    private final UUID orderId;
    private final UUID productId;
    private final int cntProducts;
    private final int price;
}
