package com.music_shop.BL.service;

import com.music_shop.BL.API.ProductService;
import com.music_shop.BL.exception.NonexistentProductException;
import com.music_shop.BL.model.Product;
import com.music_shop.DB.API.ManufacturerRepo;
import com.music_shop.DB.API.ProductRepo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProductServiceTest {
    static UUID productID;

    @BeforeAll
    static void init() {
        productID = UUID.randomUUID();
    }

    @Test
    public void givenCorrectId_whenGetProductById_thenSuccess() {
        ProductRepo productRepo = mock(ProductRepo.class);
        ManufacturerRepo manufacturerRepo = mock(ManufacturerRepo.class);
        ProductService productService = new ProductServiceImpl(productRepo, manufacturerRepo);

        Map<String, String> characteristics = new HashMap<>();
        characteristics.put("cntFrets", "24");
        Product expectedProduct = Product.builder().name("Prod1").price(12345).characteristics(characteristics).build();
        when(productRepo.getProductById(productID)).thenReturn(expectedProduct);

        Product product = productService.getProductById(productID);

        assertEquals(expectedProduct, product);
    }

    @Test
    public void givenNonexistentId_whenGetProductById_thenThrowNonexistentProductException() {
        ProductRepo productRepo = mock(ProductRepo.class);
        ManufacturerRepo manufacturerRepo = mock(ManufacturerRepo.class);
        ProductService productService = new ProductServiceImpl(productRepo, manufacturerRepo);

        when(productRepo.getProductById(productID)).thenReturn(null);

        assertThrows(NonexistentProductException.class, () -> {
            productService.getProductById(productID);
        });
    }

    @Test
    public void whenGetAllProducts_thenSuccess() {
        ProductRepo productRepo = mock(ProductRepo.class);
        ManufacturerRepo manufacturerRepo = mock(ManufacturerRepo.class);
        ProductService productService = new ProductServiceImpl(productRepo, manufacturerRepo);

        List<Product> productList = new ArrayList<Product>();
        when(productRepo.getAllProducts()).thenReturn(productList);

        assertEquals(productList, productService.getAllProducts());
    }
}
