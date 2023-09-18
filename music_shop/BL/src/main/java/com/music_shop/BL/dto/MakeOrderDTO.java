package com.music_shop.BL.model;

import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

public record MakeOrderDTO(UUID id, String customerLogin, String employeeLogin, ZonedDateTime date, Order.Status status,
                           UUID deliveryPointID, Map<UUID, Integer> productCountMap, boolean needSpendBonuses) {
    @Builder
    public MakeOrderDTO {
    }
}
