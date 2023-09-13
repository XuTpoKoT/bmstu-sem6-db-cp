package com.music_shop.BL.service;

import com.music_shop.BL.API.ManufacturerService;
import com.music_shop.BL.model.DeliveryPoint;
import com.music_shop.BL.model.Manufacturer;
import com.music_shop.DB.API.ManufacturerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManufacturerServiceIml implements ManufacturerService {
    private final ManufacturerRepo manufacturerRepo;

    @Autowired
    public ManufacturerServiceIml(ManufacturerRepo manufacturerRepo) {
        this.manufacturerRepo = manufacturerRepo;
    }

    @Override
    public List<Manufacturer> getAllManufacturers() {
        return manufacturerRepo.getAllManufacturers();
    }
}
