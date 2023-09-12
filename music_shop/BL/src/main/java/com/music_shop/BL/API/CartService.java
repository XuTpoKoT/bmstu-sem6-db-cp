package com.music_shop.BL.API;

import com.music_shop.BL.model.Product;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CartService {
    Map<Product, Integer> getProductsInCart(String login);
    void addProductToCart(String login, UUID productID, int count);
    void removeProductFromCart(String login, UUID productID);
    void updateProductInCart(String login, UUID productID, int count);
}
