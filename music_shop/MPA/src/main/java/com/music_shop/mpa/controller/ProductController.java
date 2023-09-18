package com.music_shop.mpa.controller;

import com.music_shop.BL.API.ManufacturerService;
import com.music_shop.BL.API.ProductService;
import com.music_shop.BL.log.Logger;
import com.music_shop.BL.log.LoggerImpl;
import com.music_shop.BL.dto.AddProductDTO;
import com.music_shop.BL.model.Manufacturer;
import com.music_shop.BL.model.Product;
import com.music_shop.mpa.util.PaginationHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;
import java.util.*;

@Controller
@RequestMapping("/")
public class ProductController {
    private final Logger log = new LoggerImpl(getClass().getName());
    private final ProductService productService;
    private final ManufacturerService manufacturerService;

    @Autowired
    public ProductController(ProductService productService, ManufacturerService manufacturerService) {
        this.productService = productService;
        this.manufacturerService = manufacturerService;
    }

    @GetMapping()
    public String getProductsByPageNumber(Model model,
                                          @RequestParam(name = "pageNumber", defaultValue = "1") int page) {
        final int pageSize = 6;
        int countProducts = productService.getCountProducts();
        PaginationHelper.addPaginationInfoToModel(countProducts, page, pageSize, model);
        int skip = (page - 1) * pageSize;
        List<Product> products = productService.getProductsBySkipAndLimit(skip, pageSize);
        model.addAttribute("products", products);

        return "home";
    }

    @GetMapping("products/{id}")
    public String getProductInfo(@PathVariable String id, Model model) {
        Product product = productService.getProductById(UUID.fromString(id));
        model.addAttribute("product", product);
        return "product";
    }

    @GetMapping("newproduct")
    public String createProductPage(Model model) {
        List<Manufacturer> manufacturers = manufacturerService.getAllManufacturers();
        model.addAttribute("manufacturers", manufacturers);
        return "createProduct";
    }

    @PostMapping("newproduct")
    public String createProduct(@RequestParam(name = "productName") String productName,
                                @RequestParam(name = "price") int price,
                                @RequestParam(name = "color") String color,
                                @RequestParam(name = "manufacturerId") String manufacturerId,
                                @RequestParam(name = "description") String description,
                                @RequestParam(name = "imgRef") String imgRef) {
        AddProductDTO addProductDTO = AddProductDTO.builder().name(productName).price(price).color(color)
                .description(description).manufacturerId(UUID.fromString(manufacturerId)).imgRef(imgRef).build();

        productService.addProduct(addProductDTO);
        return "home";
    }
    @PatchMapping("product")
    public String changeProductCnt(
            HttpServletRequest request,
            @RequestParam(name = "productId") String productId,
            @RequestParam(name = "storageCnt") Integer productCnt) {
        log.debug("changeProductCnt id " + productCnt);
        log.debug("uri " + request.getRequestURI());

        productService.changeProductCnt(UUID.fromString(productId), productCnt);
        return "redirect:" + request.getRequestURI();
    }
}
