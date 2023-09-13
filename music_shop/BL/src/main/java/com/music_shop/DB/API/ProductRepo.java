package com.music_shop.DB.API;

import com.music_shop.BL.model.Product;

import java.util.List;
import java.util.UUID;

public interface ProductRepo {
    Product getProductById(UUID id);
    List<Product> getAllProducts();

    List<Product> getAllProductsByIds(List<UUID> ids);

    List<Product> getProductsBySkipAndLimit(int skip, int limit);

    int getCountProducts();
    void saveProduct(Product product);
}
