package com.music_shop.DB.jdbc.repo;

import com.music_shop.BL.exception.DBException;
import com.music_shop.BL.model.Product;
import com.music_shop.DB.API.CartRepo;
import com.music_shop.DB.jdbc.mapper.CartItemMapper;
import com.music_shop.DB.jdbc.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CartRepoImpl implements CartRepo {
    private static final String SQL_GET_PRODUCTS_IN_CART = """
        SELECT p.id, p.name_ as pname, price, color, description, characteristics, m.name_ as mname,
         c.cnt_products
        FROM public.product p
            join public.manufacturer m on m.id = p.manufacturer_id
            join public.cart c on c.product_id = p.id
        WHERE c.login = :login
    """;
    private static final String SQL_ADD_PRODUCT= """
        INSERT INTO public.cart (login, product_id, cnt_products) 
        VALUES (:login, :product_id, :count) 
        ON CONFLICT (login, product_id) DO UPDATE SET
                              cnt_products=:count;
    """;
    private static final String SQL_REMOVE_PRODUCT= """
        DELETE FROM public.cart c
        WHERE c.login = :login
            AND c.product_id = :product_id
    """;
    private static final String SQL_CLEAN_CART= """
        DELETE FROM public.cart c
        WHERE c.login = :login;
    """;
    private static final String SQL_UPDATE_PRODUCT= """
        UPDATE public.cart
        SET cnt_products=:count
        WHERE login=:login and product_id=:product_id;
    """;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final CartItemMapper cartItemMapper;

    @Autowired
    public CartRepoImpl(NamedParameterJdbcTemplate jdbcTemplate, CartItemMapper cartItemMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.cartItemMapper = cartItemMapper;
    }
    @Override
    public Map<Product, Integer> getProductsInCart(String login) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("login", login);
        List<AbstractMap.SimpleImmutableEntry<Product, Integer>> products;
        Map<Product, Integer> resMap = new HashMap<>();
        try {
            products = jdbcTemplate.query(SQL_GET_PRODUCTS_IN_CART, params, cartItemMapper).stream().toList();
            for (AbstractMap.SimpleImmutableEntry<Product, Integer> entry : products) {
                resMap.put(entry.getKey(), entry.getValue());
            }
        } catch (IncorrectResultSizeDataAccessException e) {
            return resMap;
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }

        return resMap;
    }

    @Override
    public void addProductToCart(String login, UUID productID, int count) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("login", login);
        params.addValue("product_id", productID);
        params.addValue("count", count);

        try {
            jdbcTemplate.update(SQL_ADD_PRODUCT, params);
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void removeProductFromCart(String login, UUID productID) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("login", login);
        params.addValue("product_id", productID);

        try {
            jdbcTemplate.update(SQL_REMOVE_PRODUCT, params);
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void updateProductInCart(String login, UUID productID, int count) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("login", login);
        params.addValue("product_id", productID);
        params.addValue("count", count);

        try {
            jdbcTemplate.update(SQL_UPDATE_PRODUCT, params);
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public void cleanCart(String login) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("login", login);

        try {
            jdbcTemplate.update(SQL_CLEAN_CART, params);
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }
    }
}
