package com.music_shop.BL.API;

import com.music_shop.BL.model.MakeOrderDTO;
import com.music_shop.BL.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    UUID makeOrder(MakeOrderDTO order);

    List<Order> getOrdersByCustomerLogin(String customerLogin, int skip, int limit);
    List<Order> getOrdersByEmployeeLogin(String employeeLogin, int skip, int limit);

    int getCountOrdersByCustomerLogin(String customerLogin);
    int getCountOrdersByEmployeeLogin(String employeeLogin);
}
