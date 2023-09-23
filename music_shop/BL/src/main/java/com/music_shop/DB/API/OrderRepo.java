package com.music_shop.DB.API;

import com.music_shop.BL.model.Order;

import java.util.List;
import java.util.UUID;

public interface OrderRepo {
    void addOrder(Order order);
    List<Order> getOrdersByCustomerLogin(String customerLogin, int skip, int limit);
    List<Order> getOrdersByEmployeeLogin(String employeeLogin, int skip, int limit);

    int getCountOrdersByCustomerLogin(String customerLogin);

    int getCountOrdersByEmployeeLogin(String employeeLogin);

    Order getOrderById(UUID id);

    void setOrderStatus(UUID id, Order.Status status);
}
