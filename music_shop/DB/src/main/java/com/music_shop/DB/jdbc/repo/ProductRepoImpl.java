package com.music_shop.DB.jdbc.repo;

import com.music_shop.BL.exception.DBException;
import com.music_shop.BL.model.Product;
import com.music_shop.DB.API.ProductRepo;
import com.music_shop.DB.jdbc.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class ProductRepoImpl implements ProductRepo {
    private static final String SQL_GET_PRODUCT_BY_ID = """
        SELECT p.id, p.name_ as pname, price, color, storage_cnt, img_ref, description, characteristics, m.name_ as mname
            ,m.id as manufacturer_id
        FROM public.product p
            join public.manufacturer m on m.id = p.manufacturer_id
        WHERE p.id = :id 
    """;
    private static final String SQL_GET_ALL_PRODUCTS = """
        SELECT p.id, p.name_ as pname, price, color, storage_cnt, img_ref, description, characteristics, m.name_ as mname
        ,m.id as manufacturer_id
        FROM public.product p
            join public.manufacturer m on m.id = p.manufacturer_id
    """;
    private static final String SQL_GET_PRODUCTS_BY_PAGE_AND_SIZE  = """
        SELECT p.id, p.name_ as pname, price, storage_cnt, img_ref, color, description, characteristics, m.name_ as mname
        ,m.id as manufacturer_id
        FROM public.product p
            join public.manufacturer m on m.id = p.manufacturer_id
        LIMIT :limit
        OFFSET :offset
    """;
    private static final String SQL_GET_PRODUCTS_BY_IDS = """
        SELECT p.id, p.name_ as pname, price, color, storage_cnt, img_ref, description, characteristics, m.name_ as mname
        ,m.id as manufacturer_id
        FROM public.product p
            join public.manufacturer m on m.id = p.manufacturer_id
        WHERE p.id in (:ids)
    """;
    private static final String SQL_SAVE_PRODUCT = """
        INSERT INTO public.product (name_, price, color, img_ref, description, characteristics, manufacturer_id)
        VALUES (:name_, :price, :color, :img_ref, :description, :characteristics, :manufacturer_id)
    """;
    private static final String SQL_GET_COUNT_OF_PRODUCTS = """
        SELECT count (*)
        FROM public.product p
    """;
    private static final String SQL_SET_ROLE_UNREGISTERED = """
        SET ROLE unregistered;
    """;
    private static final String SQL_SET_ROLE_ADMIN = """
        SET ROLE admin_;
    """;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ProductMapper productMapper;

    @Autowired
    public ProductRepoImpl(NamedParameterJdbcTemplate jdbcTemplate, ProductMapper productMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.productMapper = productMapper;
    }

    @Override
    public Product getProductById(UUID id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        Product product;
        try {
            jdbcTemplate.update(SQL_SET_ROLE_UNREGISTERED, new MapSqlParameterSource());
            product = jdbcTemplate.queryForObject(SQL_GET_PRODUCT_BY_ID, params, productMapper);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }

        return product;
    }

    @Override
    public List<Product> getAllProducts() {
        MapSqlParameterSource params = new MapSqlParameterSource();
        List<Product> products = new ArrayList<>();
        try {
            jdbcTemplate.update(SQL_SET_ROLE_UNREGISTERED, new MapSqlParameterSource());
            products = jdbcTemplate.query(SQL_GET_ALL_PRODUCTS, params, productMapper).stream().toList();
        } catch (IncorrectResultSizeDataAccessException e) {
            return products;
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }

        return products;
    }

    @Override
    public List<Product> getAllProductsByIds(List<UUID> ids) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("ids", ids);
        List<Product> products = new ArrayList<>();
        try {
            jdbcTemplate.update(SQL_SET_ROLE_UNREGISTERED, new MapSqlParameterSource());
            products = jdbcTemplate.query(SQL_GET_PRODUCTS_BY_IDS, params, productMapper).stream().toList();
        } catch (IncorrectResultSizeDataAccessException e) {
            return products;
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }

        return products;
    }

    @Override
    public List<Product> getProductsBySkipAndLimit(int skip, int limit) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("offset", skip);
        params.addValue("limit", limit);
        List<Product> products = new ArrayList<>();
        try {
            jdbcTemplate.update(SQL_SET_ROLE_UNREGISTERED, new MapSqlParameterSource());
            products = jdbcTemplate.query(SQL_GET_PRODUCTS_BY_PAGE_AND_SIZE, params, productMapper).stream().toList();
        } catch (IncorrectResultSizeDataAccessException e) {
            return products;
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }

        return products;
    }

    @Override
    public int getCountProducts() {
        MapSqlParameterSource params = new MapSqlParameterSource();
        Integer count;
        try {
            jdbcTemplate.update(SQL_SET_ROLE_UNREGISTERED, new MapSqlParameterSource());
            count = jdbcTemplate.queryForObject(
                    SQL_GET_COUNT_OF_PRODUCTS, params, Integer.class);
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }

        return count;
    }

    @Override
    public void saveProduct(Product product) {
        System.out.println("saveProduct called");
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name_", product.getName());
        params.addValue("price", product.getPrice());
        params.addValue("color", product.getColor());
        params.addValue("img_ref", product.getImgRef());
        params.addValue("manufacturer_id", product.getManufacturer().getId());
        params.addValue("description", product.getDescription());
        params.addValue("characteristics", product.getCharacteristics());

        try {
            jdbcTemplate.update(SQL_SET_ROLE_ADMIN, new MapSqlParameterSource());
            jdbcTemplate.update(SQL_SAVE_PRODUCT, params);
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }
    }
}
