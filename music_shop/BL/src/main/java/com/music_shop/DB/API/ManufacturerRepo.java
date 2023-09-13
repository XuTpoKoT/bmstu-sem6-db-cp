package com.music_shop.DB.API;

import com.music_shop.BL.model.DeliveryPoint;
import com.music_shop.BL.model.Manufacturer;

import java.util.List;
import java.util.UUID;

public interface ManufacturerRepo {

    List<Manufacturer> getAllManufacturers();
    Manufacturer getManufacturerByID(UUID id);
}
