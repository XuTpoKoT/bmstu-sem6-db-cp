package com.music_shop.DB.API;

import com.music_shop.BL.model.DeliveryPoint;

import java.util.List;
import java.util.UUID;

public interface DeliveryPointRepo {
    List<DeliveryPoint> getAllDeliveryPoints();

    DeliveryPoint getDeliveryPointByID(UUID id);
}
