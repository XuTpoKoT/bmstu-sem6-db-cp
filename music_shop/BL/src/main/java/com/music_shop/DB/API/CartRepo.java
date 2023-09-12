package com.music_shop.DB.API;

import com.music_shop.BL.model.Product;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CartRepo {
    Map<Product, Integer> getProductsInCart(String login);
    void addProductToCart(String login, UUID productID, int count);
    void removeProductFromCart(String login, UUID productID);
    void updateProductInCart(String login, UUID productID, int count);

    void cleanCart(String login);
}
