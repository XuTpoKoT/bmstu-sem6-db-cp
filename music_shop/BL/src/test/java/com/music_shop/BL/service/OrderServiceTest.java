package com.music_shop.BL.service;

import com.music_shop.BL.exception.NonexistentCardException;
import com.music_shop.BL.exception.NonexistentCustomerException;
import com.music_shop.BL.exception.NonexistentProductException;
import com.music_shop.BL.model.*;
import com.music_shop.DB.API.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceTest {
    static String customerLogin;
    static String employeeLogin;
    static ZonedDateTime dateTime;
    static Order.Status status;
    static UUID deliveryPointID;
    static UUID productId;
    static Map<UUID, Integer> productCountMap;
    static String password;
    static byte[] encodedPassword;
    static User customer;
    @Mock private OrderRepo orderRepo;
    @Mock private UserRepo userRepo;
    @Mock private ProductRepo productRepo;
    @Mock private DeliveryPointRepo deliveryPointRepo;
    @Mock private CardRepo cardRepo;
    @Mock private CartRepo cartRepo;
    @InjectMocks private OrderServiceImpl orderService;

    @BeforeAll
    static void initBeforeAll() {
        customerLogin = "customerLogin";
        employeeLogin = null;
        dateTime = ZonedDateTime.of(2005, 10, 30, 0, 0, 0, 0,
                ZoneId.of("Europe/Moscow"));
        status = Order.Status.formed;
        deliveryPointID = UUID.randomUUID();
        productId = UUID.randomUUID();
        productCountMap = new HashMap<>();
        productCountMap.put(productId, 2);
        customerLogin = "customerLogin";
        password = "Qwe123";
        encodedPassword = Base64.getEncoder().encode(password.getBytes());
        customer = User.builder().login(customerLogin).password(encodedPassword).build();
    }

    @Test
    void givenCustomerOrderWithoutBonuses_whenMakeOrder_thenSuccess() {
        boolean needSpendBonuses = false;
        Card card = new Card(customerLogin, 0);
        when(userRepo.getUserByLogin(customerLogin)).thenReturn(customer);
        when(cardRepo.getCardByCustomerLogin(customerLogin)).thenReturn(card);
        Map<String, String> characteristics = new HashMap<>();
        characteristics.put("cntFrets", "24");
        Product product = Product.builder().id(productId).name("Prod1").price(12345).characteristics(characteristics)
                .build();
        when(productRepo.getProductById(any())).thenReturn(product);
        MakeOrderDTO order = MakeOrderDTO.builder().customerLogin(customerLogin).employeeLogin(employeeLogin).date(dateTime).
                status(status).deliveryPointID(deliveryPointID).productCountMap(productCountMap)
                .needSpendBonuses(needSpendBonuses).build();

        try {
            orderService.makeOrder(order);
        } catch (Exception e) {
            e.printStackTrace();
        }

        verify(orderRepo).addOrder(any(Order.class));
    }

    @Test
    void givenOrderWithSpendingBonuses_whenMakeOrder_thenSuccess() {
        boolean needSpendBonuses = true;
        Card card = new Card(customerLogin, 100);
        when(userRepo.getUserByLogin(customerLogin)).thenReturn(customer);
        Map<String, String> characteristics = new HashMap<>();
        characteristics.put("cntFrets", "24");
        Product product = Product.builder().id(productId).name("Prod1").price(12345).characteristics(characteristics).build();
        when(productRepo.getProductById(any())).thenReturn(product);
        when(cardRepo.getCardByCustomerLogin(customerLogin)).thenReturn(card);
        MakeOrderDTO order = MakeOrderDTO.builder().customerLogin(customerLogin).employeeLogin(employeeLogin).date(dateTime).
                status(status).deliveryPointID(deliveryPointID).productCountMap(productCountMap)
                .needSpendBonuses(needSpendBonuses).build();

        orderService.makeOrder(order);
        verify(orderRepo).addOrder(any(Order.class));
    }

    @Test
    void givenNonexistentCustomer_whenMakeOrder_thenThrowNonexistentCustomerException() {
        when(userRepo.getUserByLogin(customerLogin)).thenReturn(null);
        boolean needSpendBonuses = true;
        Card card = new Card(customerLogin, 100);
        MakeOrderDTO order = MakeOrderDTO.builder().customerLogin(customerLogin).employeeLogin(employeeLogin).date(dateTime).
                status(status).deliveryPointID(deliveryPointID).productCountMap(productCountMap)
                .needSpendBonuses(needSpendBonuses).build();

        assertThrows(NonexistentCustomerException.class, () ->
                orderService.makeOrder(order));
    }

    @Test
    void givenCustomerWithoutCard_whenMakeOrder_thenThrowNonexistentCardException() {
        when(userRepo.getUserByLogin(customerLogin)).thenReturn(customer);
        when(cardRepo.getCardByCustomerLogin(customerLogin)).thenReturn(null);
        Map<String, String> characteristics = new HashMap<>();
        characteristics.put("cntFrets", "24");
        Product product = Product.builder().id(productId).name("Prod1").price(12345)
                .characteristics(characteristics).build();
        when(productRepo.getProductById(any())).thenReturn(product);
        boolean needSpendBonuses = true;
        MakeOrderDTO order = MakeOrderDTO.builder().customerLogin(customerLogin).employeeLogin(employeeLogin).date(dateTime).
                status(status).deliveryPointID(deliveryPointID).productCountMap(productCountMap)
                .needSpendBonuses(needSpendBonuses).build();

        assertThrows(NonexistentCardException.class, () ->
                orderService.makeOrder(order));
    }

    @Test
    void givenInvalidProductId_whenMakeOrder_thenThrowNonexistentCardException() {
        when(userRepo.getUserByLogin(customerLogin)).thenReturn(customer);
        when(productRepo.getProductById(any())).thenReturn(null);
        boolean needSpendBonuses = true;
        Card card = new Card(customerLogin, 100);
        when(cardRepo.getCardByCustomerLogin(customerLogin)).thenReturn(card);
        MakeOrderDTO order = MakeOrderDTO.builder().customerLogin(customerLogin).employeeLogin(employeeLogin).date(dateTime).
                status(status).deliveryPointID(deliveryPointID).productCountMap(productCountMap)
                .needSpendBonuses(needSpendBonuses).build();

        assertThrows(NonexistentProductException.class, () ->
                orderService.makeOrder(order));
    }

    @Test
    void givenValidCustomerLogin_whenGetOrdersByCustomerLogin_thenSuccess() {
        boolean needSpendBonuses = true;
        Card card = new Card(customerLogin, 100);
        when(cardRepo.getCardByCustomerLogin(customerLogin)).thenReturn(card);
        Order order = Order.builder().customerLogin(customerLogin).employeeLogin(employeeLogin).date(dateTime).
                status(status).productCountMap(productCountMap)
                .needSpendBonuses(needSpendBonuses).build();
        List<Order> expectedOrders = List.of(order);
        when(orderRepo.getOrdersByCustomerLogin(customerLogin, 0, 10)).thenReturn(expectedOrders);

        assertEquals(expectedOrders, orderService.getOrdersByCustomerLogin(customerLogin, 0, 10));
    }
    @Test
    void givenValidEmployeeLogin_whenGetOrdersByEmployeeLogin_thenSuccess() {
        boolean needSpendBonuses = true;
        Card card = new Card(customerLogin, 100);
        when(cardRepo.getCardByCustomerLogin(customerLogin)).thenReturn(card);
        Order order = Order.builder().customerLogin(customerLogin).employeeLogin(employeeLogin).date(dateTime).
                status(status).productCountMap(productCountMap)
                .needSpendBonuses(needSpendBonuses).build();
        List<Order> orderList = List.of(order);
        when(orderRepo.getOrdersByEmployeeLogin(employeeLogin, 0, 10)).thenReturn(orderList);

        assertEquals(orderList, orderService.getOrdersByEmployeeLogin(employeeLogin, 0, 10));
    }
}