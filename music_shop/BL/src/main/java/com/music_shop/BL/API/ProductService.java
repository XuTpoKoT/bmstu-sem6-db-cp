package com.music_shop.BL.API;

import com.music_shop.BL.model.AddProductDTO;
import com.music_shop.BL.model.Product;

import java.util.List;
import java.util.UUID;

public interface ProductService {
    Product getProductById(UUID id);
    List<Product> getAllProducts();
    List<Product> getProductsByIds(List<UUID> ids);
    List<Product> getProductsBySkipAndLimit(int skip, int limit);
    int getCountProducts();
    void addProduct(AddProductDTO addProductDTO);
}
