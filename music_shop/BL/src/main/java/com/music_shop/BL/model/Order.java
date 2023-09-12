package com.music_shop.BL.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
public class Order {
    public enum Status {
        formed("оформлен"),
        built("собран"),
        delivered("доставлен"),
        received("получен");
        private String name;
        public String getName() {
            return name;
        }

        Status(String str) {
            name = str;
        }
    }
    private final UUID id;
    private final String customerLogin;
    private final String employeeLogin;
    private final ZonedDateTime date;
    private final Order.Status status;
    private final DeliveryPoint deliveryPoint;
    private final boolean needSpendBonuses;
    private final int initialCost;
    private final int paidByBonuses;
    @Setter
    private Map<UUID, Integer> productCountMap;
    @Setter
    private Map<UUID, Integer> productPriceMap;

    @Builder
    public Order(UUID id, String customerLogin, String employeeLogin, ZonedDateTime date, Status status,
                 DeliveryPoint deliveryPoint, Map<UUID, Integer> productCountMap, boolean needSpendBonuses,
                 Map<UUID, Integer> productPriceMap, int initialCost, int paidByBonuses) {
        this.id = id;
        this.customerLogin = customerLogin;
        this.employeeLogin = employeeLogin;
        this.date = date;
        this.status = status;
        this.deliveryPoint = deliveryPoint;
        this.productCountMap = productCountMap;
        this.needSpendBonuses = needSpendBonuses;
        this.productPriceMap = productPriceMap;
        this.initialCost = initialCost;
        this.paidByBonuses = paidByBonuses;
    }
}
