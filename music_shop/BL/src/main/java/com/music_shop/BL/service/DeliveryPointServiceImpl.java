package com.music_shop.BL.service;

import com.music_shop.BL.API.DeliveryPointService;
import com.music_shop.BL.log.Logger;
import com.music_shop.BL.log.LoggerImpl;
import com.music_shop.BL.model.DeliveryPoint;
import com.music_shop.DB.API.DeliveryPointRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DeliveryPointServiceImpl implements DeliveryPointService {
    private final Logger log = new LoggerImpl(getClass().getName());
    private final DeliveryPointRepo deliveryPointRepo;

    @Autowired
    public DeliveryPointServiceImpl(DeliveryPointRepo deliveryPointRepo) {
        this.deliveryPointRepo = deliveryPointRepo;
    }

    @Override
    public List<DeliveryPoint> getAllDeliveryPoints() {
        log.info("getAllDeliveryPoints called");
        return deliveryPointRepo.getAllDeliveryPoints();
    }

    @Override
    public DeliveryPoint getDeliveryPointByID(UUID id) {
        return deliveryPointRepo.getDeliveryPointByID(id);
    }
}
