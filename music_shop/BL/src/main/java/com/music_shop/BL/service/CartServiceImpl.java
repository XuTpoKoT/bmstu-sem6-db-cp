package com.music_shop.BL.service;

import com.music_shop.BL.API.CartService;
import com.music_shop.BL.exception.DBException;
import com.music_shop.BL.log.Logger;
import com.music_shop.BL.log.LoggerImpl;
import com.music_shop.BL.model.Product;
import com.music_shop.DB.API.CartRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class CartServiceImpl implements CartService {
    private final Logger log = new LoggerImpl(getClass().getName());
    private final CartRepo cartRepo;

    @Autowired
    public CartServiceImpl(CartRepo cartRepo) {
        this.cartRepo = cartRepo;
    }

    @Override
    public Map<Product, Integer> getProductsInCart(String login) {
        return cartRepo.getProductsInCart(login);
    }

    @Override
    public void addProductToCart(String login, UUID productID, int count) {
        cartRepo.addProductToCart(login, productID, count);
    }

    @Override
    public void removeProductFromCart(String login, UUID productID) {
        try {
            cartRepo.removeProductFromCart(login, productID);
        } catch (DBException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void updateProductInCart(String login, UUID productID, int count) {
        cartRepo.updateProductInCart(login, productID, count);
    }
}
