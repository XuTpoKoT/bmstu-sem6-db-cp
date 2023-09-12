package com.music_shop.DB.jdbc.repo;

import com.music_shop.BL.model.Product;
import com.music_shop.DB.API.ProductRepo;
import com.music_shop.DB.jdbc.mapper.IntMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "/scheme.sql")
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PoductRepoTest {
    private static String SQL_COUNT_PRODUCTS = "SELECT count(*) FROM public.product";
    @Autowired
    private ProductRepo productRepository;
    @Autowired
    private NamedParameterJdbcTemplate jdbc;
    @Autowired
    private IntMapper intMapper;

    @Test
    public void givenValidId_whenGetProductById_thenSuccess() {
        UUID productId = UUID.fromString("c23bbab0-3df8-429c-9b07-9d41ac86a2ca");
        Map<String, String> characteristics = new HashMap<>();
        characteristics.put("cnt_frets", "24");
        Product expectedProduct = Product.builder().id(productId).name("TAKAMINE-FG50").price(35000)
                .characteristics(characteristics).build();

        Product product = productRepository.getProductById(productId);

        assertEquals(expectedProduct.getPrice(), product.getPrice());
        assertEquals(expectedProduct.getName(), product.getName());
        assertEquals(expectedProduct.getId(), product.getId());
    }
    @Test
    public void givenWrongId_whenGetProductById_thenReturnNull() {
        UUID productId = UUID.fromString("46526c2d-c1a4-467e-a48e-4600d590fa3b");

        Product product = productRepository.getProductById(productId);;

        assertEquals(null, product);
    }

    @Test
    public void whenGetAllProducts_thenSuccess() {
        MapSqlParameterSource params = new MapSqlParameterSource();
        Integer expectedCnt = jdbc.queryForObject(SQL_COUNT_PRODUCTS, params, intMapper);

        List<Product> products = productRepository.getAllProducts();
        Integer cnt = products.size();

        assertEquals(expectedCnt, cnt);
    }
}
