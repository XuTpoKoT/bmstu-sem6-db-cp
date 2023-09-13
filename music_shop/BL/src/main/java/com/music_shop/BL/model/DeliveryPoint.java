package com.music_shop.BL.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DeliveryPoint {
    private UUID id;
    private String address;
}
