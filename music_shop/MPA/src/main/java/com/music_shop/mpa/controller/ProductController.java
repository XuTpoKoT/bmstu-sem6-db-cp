package com.music_shop.mpa.controller;

import com.music_shop.BL.API.ManufacturerService;
import com.music_shop.BL.API.ProductService;
import com.music_shop.BL.model.AddProductDTO;
import com.music_shop.BL.model.Manufacturer;
import com.music_shop.BL.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/")
public class ProductController {
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
        System.out.println("getProductsByPageNumber called");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        System.out.println(roles);
        final int pageSize = 6;
        try {
            int countProducts = productService.getCountProducts();
            int countPages = (int) Math.ceil((double) countProducts / pageSize);
            if (page < 1) {
                page = 1;
            }  else if (page > countPages) {
                page = countPages;
            }
            int startPage = Math.max(page - 1, 1);
            int endPage = Math.min(page + 1, countPages);
            List<Integer> pageNumbers = IntStream.rangeClosed(startPage, endPage)
                    .boxed().toList();
            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("needDots", Collections.max(pageNumbers) + 1 < countPages);
            model.addAttribute("needLastPage", Collections.max(pageNumbers) < countPages);
            model.addAttribute("countPages", countPages);
            model.addAttribute("currentPage", page);
            int skip = (page - 1) * pageSize;
            List<Product> products = productService.getProductsBySkipAndLimit(skip, pageSize);
            model.addAttribute("products", products);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "home";
    }

    @GetMapping("products/{id}")
    public String getProductInfo(@PathVariable String id, Model model) {
        System.out.println("getProductInfo called " + id);
        Product product = productService.getProductById(UUID.fromString(id));
        model.addAttribute("product", product);
        return "product";
    }

    @GetMapping("newproduct")
    public String createProductPage(Model model) {
        System.out.println("createProductPage called ");
        List<Manufacturer> manufacturers = manufacturerService.getAllManufacturers();
        System.out.println("manufacturers");
        System.out.println(manufacturers);
        model.addAttribute("manufacturers", manufacturers);
        return "createProduct";
    }
    @PostMapping("newproduct")
    public String createProduct(Model model,
                                @RequestParam(name = "productName") String productName,
                                @RequestParam(name = "price") int price,
                                @RequestParam(name = "color") String color,
                                @RequestParam(name = "manufacturerId") String manufacturerId,
                                @RequestParam(name = "description") String description,
                                @RequestParam(name = "imgRef") String imgRef) {
        System.out.println("createProduct called ");
        AddProductDTO addProductDTO = AddProductDTO.builder().name(productName).price(price).color(color)
                .description(description).manufacturerId(UUID.fromString(manufacturerId)).imgRef(imgRef).build();

        productService.addProduct(addProductDTO);
        return "home";
    }
}
