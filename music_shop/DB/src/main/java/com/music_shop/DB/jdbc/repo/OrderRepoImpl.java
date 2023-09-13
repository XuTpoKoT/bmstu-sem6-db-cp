package com.music_shop.DB.jdbc.repo;

import com.music_shop.BL.exception.DBException;
import com.music_shop.BL.model.Order;
import com.music_shop.BL.model.Product;
import com.music_shop.DB.API.OrderRepo;
import com.music_shop.DB.jdbc.mapper.OrderDetailsMapper;
import com.music_shop.DB.jdbc.mapper.OrderMapper;
import com.music_shop.DB.jdbc.model.OrderDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class OrderRepoImpl implements OrderRepo {
    private static final String SQL_ADD_ORDER = """
        INSERT INTO public.order_ (id, customer_login, employee_login, date_, status, delivery_point_id, initial_cost, paid_by_bonuses) 
        VALUES (:id, :customerLogin, :employeeLogin, :date, :status, :deliveryPointId, :initialCost, :paidByBonuses) 
    """;
    private static final String SQL_GET_COUNT_ORDERS_BY_CUSTOMER_LOGIN = """
        SELECT count (*)
        FROM public.order_
        WHERE customer_login = :login
    """;
    private static final String SQL_GET_COUNT_ORDERS_BY_EMPLOYEE_LOGIN = """
        SELECT count (*)
        FROM public.order_
        WHERE employee_login = :login
    """;
    private static final String SQL_GET_ORDERS_BY_CUSTOMER_LOGIN = """
        SELECT o.id as order_id, customer_login, employee_login, date_, status, initial_cost, paid_by_bonuses,
            d.id as delivery_point_id, d.address
        FROM public.order_ o
            JOIN public.deliverypoint d on o.delivery_point_id = d.id
        WHERE customer_login = :login
        LIMIT :limit
        OFFSET :offset
    """;
    private static final String SQL_GET_ORDERS_BY_EMPLOYEE_LOGIN = """
        SELECT o.id as order_id, customer_login, employee_login, date_, status, initial_cost, paid_by_bonuses,
            d.id as delivery_point_id, d.address
        FROM public.order_ o
            JOIN public.deliverypoint d on o.delivery_point_id = d.id
        WHERE employee_login = :login
        LIMIT :limit
        OFFSET :offset
    """;
    private static final String SQL_GET_ORDER_DETAILS_BY_ORDER_ID = """        
        SELECT *
        FROM public.order_product
        WHERE order_id = :order_id
    """;
    private static final String SQL_ADD_ORDER_DETAILS = """
        INSERT INTO public.order_product (order_id, product_id, price, cnt_products)
        VALUES (:orderId, :productId, :price, :cntProducts)
    """;
    private static final String SQL_SET_ROLE_UNREGISTERED = """
        SET ROLE unregistered;
    """;
    private static final String SQL_SET_ROLE_CUSTOMER = """
        SET ROLE customer;
    """;
    private static final String SQL_SET_ROLE_EMPLOYEE = """
        SET ROLE employee;
    """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final OrderMapper orderMapper;
    private final OrderDetailsMapper orderDetailsMapper;

    @Autowired
    public OrderRepoImpl(NamedParameterJdbcTemplate jdbcTemplate, OrderMapper orderMapper,
                         OrderDetailsMapper orderDetailsMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.orderMapper = orderMapper;
        this.orderDetailsMapper = orderDetailsMapper;
    }

    @Transactional
    @Override
    public void addOrder(Order order) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", order.getId());
        params.addValue("customerLogin", order.getCustomerLogin());
        params.addValue("employeeLogin", order.getEmployeeLogin());
        Timestamp ts = Timestamp.from(order.getDate().toInstant());
        params.addValue("date", ts);
        params.addValue("status", order.getStatus().name());
        params.addValue("deliveryPointId", order.getDeliveryPoint().getId());
        params.addValue("initialCost", order.getInitialCost());
        params.addValue("paidByBonuses", order.getPaidByBonuses());

        try {
            jdbcTemplate.update(SQL_SET_ROLE_CUSTOMER, new MapSqlParameterSource());
            jdbcTemplate.update(SQL_ADD_ORDER, params);
            Map<UUID, Integer> productCountMap = order.getProductCountMap();
            Map<UUID, Integer> productPriceMap = order.getProductPriceMap();
            for (UUID productId : productCountMap.keySet()) {
                MapSqlParameterSource oDParams = new MapSqlParameterSource();
                oDParams.addValue("orderId", order.getId());
                oDParams.addValue("productId", productId);
                oDParams.addValue("cntProducts", productCountMap.get(productId));
                oDParams.addValue("price", productPriceMap.get(productId));
                jdbcTemplate.update(SQL_ADD_ORDER_DETAILS, oDParams);
            }
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }
    }

    @Override
    public List<Order> getOrdersByCustomerLogin(String customerLogin, int skip, int limit) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("login", customerLogin);
        params.addValue("offset", skip);
        params.addValue("limit", limit);
        List<Order> orders;
        try {
            jdbcTemplate.update(SQL_SET_ROLE_CUSTOMER, new MapSqlParameterSource());
            orders = jdbcTemplate.query(SQL_GET_ORDERS_BY_CUSTOMER_LOGIN, params, orderMapper).stream().toList();
            for (Order o : orders) {
                Map<UUID, Integer> productCountMap = new HashMap<>();
                Map<UUID, Integer> productPriceMap = new HashMap<>();
                MapSqlParameterSource oDParams = new MapSqlParameterSource();
                oDParams.addValue("order_id", o.getId());
                List<OrderDetails> orderDetailsList = jdbcTemplate.query(SQL_GET_ORDER_DETAILS_BY_ORDER_ID, oDParams,
                                orderDetailsMapper).stream().toList();
                for (OrderDetails od : orderDetailsList) {
                    productCountMap.put(od.getProductId(), od.getCntProducts());
                    productPriceMap.put(od.getProductId(), od.getPrice());
                }
                o.setProductCountMap(productCountMap);
                o.setProductPriceMap(productPriceMap);
            }
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }

        return orders;
    }

    @Override
    public List<Order> getOrdersByEmployeeLogin(String employeeLogin, int skip, int limit) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("login", employeeLogin);
        params.addValue("offset", skip);
        params.addValue("limit", limit);
        List<Order> orders;
        try {
            jdbcTemplate.update(SQL_SET_ROLE_EMPLOYEE, new MapSqlParameterSource());
            orders = jdbcTemplate.query(SQL_GET_ORDERS_BY_EMPLOYEE_LOGIN, params, orderMapper).stream().toList();
            for (Order o : orders) {
                Map<UUID, Integer> productCountMap = new HashMap<>();
                MapSqlParameterSource oDParams = new MapSqlParameterSource();
                oDParams.addValue("order_id", o.getId());
                List<OrderDetails> orderDetailsList = jdbcTemplate.query(SQL_GET_ORDER_DETAILS_BY_ORDER_ID, oDParams,
                        orderDetailsMapper).stream().toList();
                for (OrderDetails od : orderDetailsList) {
                    productCountMap.put(od.getProductId(), od.getCntProducts());
                }
                o.setProductCountMap(productCountMap);
            }
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        } catch (DataAccessException e) {
            throw new DBException(e.getMessage());
        }

        return orders;
    }

    @Override
    public int getCountOrdersByCustomerLogin(String customerLogin) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("login", customerLogin);
        Integer count;
        try {
            jdbcTemplate.update(SQL_SET_ROLE_CUSTOMER, new MapSqlParameterSource());
            count = jdbcTemplate.queryForObject(
                    SQL_GET_COUNT_ORDERS_BY_CUSTOMER_LOGIN, params, Integer.class);
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }

        return count;
    }

    @Override
    public int getCountOrdersByEmployeeLogin(String employeeLogin) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("login", employeeLogin);
        Integer count;
        try {
            jdbcTemplate.update(SQL_SET_ROLE_EMPLOYEE, new MapSqlParameterSource());
            count = jdbcTemplate.queryForObject(
                    SQL_GET_COUNT_ORDERS_BY_EMPLOYEE_LOGIN, params, Integer.class);
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }

        return count;
    }
}
