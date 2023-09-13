package com.music_shop.BL.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class Product {
    private final UUID id;
    private final String name;
    private final int price;
    private final int storageCnt;
    private final String description;
    private final String color;
    private final Manufacturer manufacturer;
    private final String imgRef;
    private final Map<String, String> characteristics;

    @Builder
    public Product(UUID id, String name, int price, int storageCnt, String description, String color, Manufacturer manufacturer, String imgRef, Map<String,
            String> characteristics) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.storageCnt = storageCnt;
        this.description = description;
        this.color = color;
        this.manufacturer = manufacturer;
        this.imgRef = imgRef;
        this.characteristics = characteristics;
    }
}

