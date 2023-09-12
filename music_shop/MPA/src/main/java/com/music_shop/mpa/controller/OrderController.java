package com.music_shop.mpa.controller;

import com.music_shop.BL.API.CartService;
import com.music_shop.BL.API.OrderService;
import com.music_shop.BL.model.MakeOrderDTO;
import com.music_shop.BL.model.Order;
import com.music_shop.BL.model.Product;
import com.music_shop.BL.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final CartService cartService;

    @Autowired
    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @PostMapping()
    public String makeOrder(@RequestParam String deliveryPointId,
                            @RequestParam(name = "customer", required = false) String customerLogin,
                            @RequestParam(required = false) boolean needSpendBonuses,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        System.out.println("makeOrder called ");
        if (customerLogin != null && customerLogin.isEmpty()) {
            customerLogin = null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        String employeeLogin = null;
        if (roles.contains(User.Role.EMPLOYEE.name())) {
            employeeLogin = authentication.getName();
        } else {
            customerLogin = authentication.getName();
        }


        Map<Product, Integer> products = cartService.getProductsInCart(authentication.getName());
        Map<UUID, Integer> productCountMap = products.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey().getId(), Map.Entry::getValue));

        MakeOrderDTO makeOrderDTO = MakeOrderDTO.builder()
                .employeeLogin(employeeLogin)
                .customerLogin(customerLogin)
                .date(ZonedDateTime.now().withZoneSameLocal(ZoneId.of("UTC")))
                .deliveryPointID(UUID.fromString(deliveryPointId))
                .needSpendBonuses(needSpendBonuses)
                .productCountMap(productCountMap)
                .status(Order.Status.formed)
                .build();

        try {
            orderService.makeOrder(makeOrderDTO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            redirectAttributes.addAttribute("errorMessage", e.getMessage());
            return "redirect:/cart";
        }

        return "redirect:/";
    }

    @GetMapping()
    public String getOrdersByLogin(Model model,
                                          @RequestParam(name = "pageNumber", defaultValue = "1") int page) {
        System.out.println("getOrdersByLoginAndPageNumber called");
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Set<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
            List<Order> orders;
            final int pageSize = 10;
            if (roles.contains(User.Role.EMPLOYEE.name())) {
                int countOrders= orderService.getCountOrdersByCustomerLogin(authentication.getName());
                addPaginationInfoToModel(countOrders, page, pageSize, model);
                int skip = (page - 1) * pageSize;
                orders = orderService.getOrdersByEmployeeLogin(authentication.getName(), skip, pageSize);
            } else {
                int countOrders= orderService.getCountOrdersByCustomerLogin(authentication.getName());
                addPaginationInfoToModel(countOrders, page, pageSize, model);
                int skip = (page - 1) * pageSize;
                orders = orderService.getOrdersByCustomerLogin(authentication.getName(), skip, pageSize);
            }
            for (Order o : orders) {
                System.out.println(o.getDate());
            }
            model.addAttribute("orders", orders);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "orders";
    }
    private void addPaginationInfoToModel(int countOrders, int page, int pageSize, Model model) {
        int countPages = (int) Math.ceil((double) countOrders / pageSize);
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
    }
}
