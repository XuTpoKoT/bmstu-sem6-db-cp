package com.music_shop.BL.service;

import com.music_shop.BL.API.ProductService;
import com.music_shop.BL.exception.DBException;
import com.music_shop.BL.exception.NonexistentProductException;
import com.music_shop.BL.log.Logger;
import com.music_shop.BL.log.LoggerImpl;
import com.music_shop.BL.dto.AddProductDTO;
import com.music_shop.BL.model.Manufacturer;
import com.music_shop.BL.model.Product;
import com.music_shop.DB.API.ManufacturerRepo;
import com.music_shop.DB.API.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {
    private final Logger log = new LoggerImpl(getClass().getName());
    private final ProductRepo productRepo;
    private final ManufacturerRepo manufacturerRepo;

    @Autowired
    public ProductServiceImpl(ProductRepo productRepo, ManufacturerRepo manufacturerRepo) {
        this.productRepo = productRepo;
        this.manufacturerRepo = manufacturerRepo;
    }

    @Override
    public Product getProductById(UUID id) {
        log.info("getProductById called with id " + id);
        Product product = productRepo.getProductById(id);
        if (product == null) {
            RuntimeException e = new NonexistentProductException("Нет такого продукта!");
            log.error("login failed", e);
            throw e;
        }
        return product;
    }

    @Override
    public List<Product> getAllProducts() {
        log.info("getAllProducts called");
        return productRepo.getAllProducts();
    }

    @Override
    public List<Product> getProductsBySkipAndLimit(int skip, int limit) {
        return productRepo.getProductsBySkipAndLimit(skip, limit);
    }

    @Override
    public int getCountProducts() {
        return productRepo.getCountProducts();
    }

    @Override
    public void addProduct(AddProductDTO addProductDTO) {
        Manufacturer manufacturer = manufacturerRepo.getManufacturerByID(addProductDTO.manufacturerId());
        Product product = Product.builder()
                .name(addProductDTO.name())
                .price(addProductDTO.price())
                .color(addProductDTO.color())
                .description(addProductDTO.description())
                .imgRef(addProductDTO.imgRef())
                .manufacturer(manufacturer)
                .build();
        productRepo.saveProduct(product);
    }

    @Override
    public void changeProductCnt(UUID productId, int cnt) {
        log.info("changeProductCnt called for " + productId);
        try {
            productRepo.changeProductCnt(productId, cnt);
        } catch (DBException e) {
            log.error("changeProductCnt failed", e);
        }

    }
}
