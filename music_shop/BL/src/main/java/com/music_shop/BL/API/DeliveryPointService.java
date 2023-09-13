package com.music_shop.BL.API;

import com.music_shop.BL.model.DeliveryPoint;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public interface DeliveryPointService {
    List<DeliveryPoint> getAllDeliveryPoints();
}
