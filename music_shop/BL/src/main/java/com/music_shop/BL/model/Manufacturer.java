package com.music_shop.BL.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Manufacturer {
    private UUID id;
    private String name;
}