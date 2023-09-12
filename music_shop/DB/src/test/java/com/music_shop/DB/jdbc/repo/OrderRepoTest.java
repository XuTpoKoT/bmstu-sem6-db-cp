package com.music_shop.DB.jdbc.repo;

import com.music_shop.BL.model.DeliveryPoint;
import com.music_shop.BL.model.Order;
import com.music_shop.BL.model.Product;
import com.music_shop.DB.API.OrderRepo;
import com.music_shop.DB.jdbc.mapper.IntMapper;
import com.music_shop.DB.jdbc.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@Sql(scripts = "/scheme.sql")
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/clear.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class OrderRepoTest {
    private static String SQL_COUNT_ORDERS = "SELECT count(*) FROM public.order_";
    private static final String SQL_GET_ORDERS_BY_CUSTOMER_LOGIN = """
        SELECT o.id as order_id, customer_login, employee_login, date_, status, initial_cost, paid_by_bonuses,
            d.id as delivery_point_id, d.address
        FROM public.order_ o
            JOIN public.deliverypoint d on o.delivery_point_id = d.id
        WHERE customer_login = :login
    """;
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private NamedParameterJdbcTemplate jdbc;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private IntMapper intMapper;
    @Test
    public void givenValidOrder_whenAddOrder_thenSuccess() {
        String customerLogin = "Bob";
        boolean needSpendBonuses = false;
        ZonedDateTime date = ZonedDateTime.of(2005, 10, 30, 0, 0, 0, 0,
                ZoneId.of("Europe/Moscow"));
        Order.Status status = Order.Status.formed;
        UUID id = UUID.randomUUID();

        UUID productId = UUID.fromString("c23bbab0-3df8-429c-9b07-9d41ac86a2ca");
        DeliveryPoint deliveryPoint = new DeliveryPoint(UUID.fromString("b92f19d8-8db0-498f-8ee9-ce2580706708"),
                "Петровский проспект, 24");
        Map<String, String> characteristics = new HashMap<>();
        characteristics.put("cnt_frets", "24");
        Product product = Product.builder().id(productId).name("TAKAMINE-FG50").price(35000)
                .characteristics(characteristics).build();
        Map<UUID, Integer> productCountMap = new HashMap<>();
        productCountMap.put(product.getId(), 2);
        Map<UUID, Integer> productPriceMap = new HashMap<>();
        productPriceMap.put(product.getId(), product.getPrice());

        Order expectedOrder = Order.builder().id(id).initialCost(35000).paidByBonuses(0).
                customerLogin(customerLogin).date(date).status(status)
                .needSpendBonuses(needSpendBonuses).productCountMap(productCountMap)
                .deliveryPoint(deliveryPoint).build();
        expectedOrder.setProductPriceMap(productPriceMap);

        MapSqlParameterSource params = new MapSqlParameterSource();
        Integer expectedCnt = jdbc.queryForObject(SQL_COUNT_ORDERS, params, intMapper) + 1;

        orderRepo.addOrder(expectedOrder);
        MapSqlParameterSource getParams = new MapSqlParameterSource();
        getParams.addValue("login", customerLogin);
        Order order = jdbc.queryForObject(SQL_GET_ORDERS_BY_CUSTOMER_LOGIN, getParams, orderMapper);
        Integer cnt = jdbc.queryForObject(SQL_COUNT_ORDERS, params, intMapper);

        assertEquals(expectedOrder.getId(), order.getId());
        assertEquals(expectedOrder.getCustomerLogin(), order.getCustomerLogin());
        assertEquals(expectedOrder.getEmployeeLogin(), order.getEmployeeLogin());
        assertEquals(expectedOrder.getStatus(), order.getStatus());
        assertEquals(expectedOrder.getPaidByBonuses(), order.getPaidByBonuses());
        assertEquals(expectedOrder.getInitialCost(), order.getInitialCost());
        assertEquals(expectedOrder.getDeliveryPoint().getId(), order.getDeliveryPoint().getId());
        assertEquals(expectedCnt, cnt);
    }
}
