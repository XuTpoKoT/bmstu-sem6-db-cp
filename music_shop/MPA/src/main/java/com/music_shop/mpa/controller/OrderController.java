package com.music_shop.mpa.controller;

import com.music_shop.BL.API.CartService;
import com.music_shop.BL.API.OrderService;
import com.music_shop.BL.log.Logger;
import com.music_shop.BL.log.LoggerImpl;
import com.music_shop.BL.dto.MakeOrderDTO;
import com.music_shop.BL.model.Order;
import com.music_shop.BL.model.Product;
import com.music_shop.BL.model.User;
import com.music_shop.mpa.util.PaginationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private final Logger log = new LoggerImpl(getClass().getName());
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
                            RedirectAttributes redirectAttributes) {
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
            log.error(e.getMessage(), e);
            redirectAttributes.addAttribute("errorMessage", e.getMessage());
            return "redirect:/cart";
        }

        return "redirect:/";
    }

    @GetMapping()
    public String getOrdersByLogin(Model model,
                                          @RequestParam(name = "pageNumber", defaultValue = "1") int page) {
        log.debug("getOrdersByLoginAndPageNumber called");
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Set<String> roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
            List<Order> orders;
            final int pageSize = 10;
            if (roles.contains(User.Role.EMPLOYEE.name())) {
                int countOrders= orderService.getCountOrdersByEmployeeLogin(authentication.getName());
                PaginationHelper.addPaginationInfoToModel(countOrders, page, pageSize, model);
                int skip = (page - 1) * pageSize;
                orders = orderService.getOrdersByEmployeeLogin(authentication.getName(), skip, pageSize);
            } else {
                int countOrders= orderService.getCountOrdersByCustomerLogin(authentication.getName());
                PaginationHelper.addPaginationInfoToModel(countOrders, page, pageSize, model);
                int skip = (page - 1) * pageSize;
                orders = orderService.getOrdersByCustomerLogin(authentication.getName(), skip, pageSize);
            }
            model.addAttribute("orders", orders);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return "orders";
    }

    @GetMapping("/{id}")
    public String getOrdersByLogin(@PathVariable String id, Model model) {
        Order order = orderService.getOrderById(UUID.fromString(id));
        model.addAttribute("order", order);
        return "order";
    }
    @PatchMapping("/{id}")
    public String setOrderStatus(@PathVariable String id,
                                 @RequestParam String orderStatus) {
        orderService.setOrderStatus(UUID.fromString(id), Order.Status.valueOf(orderStatus));
        return "redirect:/orders/" + id;
    }
}
