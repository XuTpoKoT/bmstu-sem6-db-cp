package com.music_shop.BL.service;

import com.music_shop.BL.API.OrderService;
import com.music_shop.BL.exception.NonexistentCardException;
import com.music_shop.BL.exception.NonexistentCustomerException;
import com.music_shop.BL.exception.NonexistentProductException;
import com.music_shop.BL.log.Logger;
import com.music_shop.BL.log.LoggerImpl;
import com.music_shop.BL.model.*;
import com.music_shop.DB.API.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.min;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final Logger log = new LoggerImpl(getClass().getName());
    private final int MAX_PERCENTS_SPENT_BONUSES = 30;
    private final int BONUS_PERCENT = 5;
    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final DeliveryPointRepo deliveryPointRepo;
    private final CardRepo cardRepo;
    private final CartRepo cartRepo;

    @Override
    public List<Order> getOrdersByCustomerLogin(String customerLogin, int skip, int limit) {
        log.info("getOrdersByCustomerLogin called with " + customerLogin);
        return orderRepo.getOrdersByCustomerLogin(customerLogin, skip, limit);
    }

    @Override
    public List<Order> getOrdersByEmployeeLogin(String employeeLogin, int skip, int limit) {
        log.info("getOrdersByEmployeeLogin called with " + employeeLogin);
        return orderRepo.getOrdersByEmployeeLogin(employeeLogin, skip, limit);
    }

    @Override
    public int getCountOrdersByCustomerLogin(String customerLogin) {
        log.info("getCountOrdersByCustomerLogin called with " + customerLogin);
        return orderRepo.getCountOrdersByCustomerLogin(customerLogin);
    }

    @Override
    public int getCountOrdersByEmployeeLogin(String employeeLogin) {
        log.info("getCountOrdersByEmployeeLogin called with " + employeeLogin);
        return orderRepo.getCountOrdersByEmployeeLogin(employeeLogin);
    }

    @Transactional
    @Override
    public UUID makeOrder(MakeOrderDTO makeOrderDTO) {
        log.info("makeOrder called with orderId " + makeOrderDTO.id());
        checkCustomer(makeOrderDTO.customerLogin());
        List<Product> products = getProductsInOrder(makeOrderDTO.productCountMap().keySet());
        Map<UUID, Integer> productPriceMap = getProductPriceMap(products);
        int initialCost = calcInitialCost(products, makeOrderDTO.productCountMap());
        DeliveryPoint deliveryPoint = deliveryPointRepo.getDeliveryPointByID(makeOrderDTO.deliveryPointID());

        int paidByBonuses = 0;
        if (makeOrderDTO.customerLogin() != null) {
            Card card = cardRepo.getCardByCustomerLogin(makeOrderDTO.customerLogin());
            if (card == null) {
                RuntimeException e = new NonexistentCardException("Карта не найдена!");
                log.error("Nonexistent card", e);
                throw e;
            }

            if (makeOrderDTO.needSpendBonuses()) {
                paidByBonuses = calcSpentBonuses(card, initialCost);
            }
            int accruedBonuses = calcAccruedBonuses(initialCost - paidByBonuses);
            card.incBonuses(accruedBonuses - paidByBonuses);
            cardRepo.updateCard(card);
        }

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .employeeLogin(makeOrderDTO.employeeLogin())
                .customerLogin(makeOrderDTO.customerLogin())
                .date(makeOrderDTO.date())
                .deliveryPoint(deliveryPoint)
                .needSpendBonuses(makeOrderDTO.needSpendBonuses())
                .productCountMap(makeOrderDTO.productCountMap())
                .status(makeOrderDTO.status())
                .initialCost(initialCost)
                .productPriceMap(productPriceMap)
                .paidByBonuses(paidByBonuses)
                .build();
        orderRepo.addOrder(order);
        cleanCart(makeOrderDTO);
        return order.getId();
    }

    private void checkCustomer(String customerLogin) {
        log.debug("checkCustomer called with " + customerLogin);
        if (customerLogin != null && userRepo.getUserByLogin(customerLogin) == null) {
            RuntimeException e = new NonexistentCustomerException("Нет такого заказчика!");
            log.error("Nonexistent customer", e);
            throw e;
        }
    }
    private List<Product> getProductsInOrder(Set<UUID> productIds) {
        log.debug("getProductsInOrder called");
        List<Product> products = new ArrayList<>();
        for (UUID productId : productIds) {
            Product product = productRepo.getProductById(productId);
            if (product == null) {
                RuntimeException e = new NonexistentProductException("Нет такого продукта!");
                log.error("login failed", e);
                throw e;
            }
            products.add(product);
        }
        return products;
    }

    private int calcInitialCost(List<Product> productsInOrder, Map<UUID, Integer> productCountMap) {
        log.debug("calcInitialCost called");
        int initialCost = 0;
        for (Product p : productsInOrder) {
            initialCost += p.getPrice() * productCountMap.get(p.getId());
        }

        return initialCost;
    }

    private Map<UUID, Integer> getProductPriceMap(List<Product> productsInOrder) {
        log.debug("getProductPriceMap called");
        return productsInOrder.stream().collect(Collectors.toMap(Product::getId, Product::getPrice));
    }

    private int calcSpentBonuses(Card card, int initialCost) {
        log.debug("calcSpentBonuses called with customerLogin " + card.getCustomerLogin() + " and initial cost "
            + initialCost);
        int maxSpentBonuses = MAX_PERCENTS_SPENT_BONUSES * initialCost / 100;
        return min(card.getBonuses(), maxSpentBonuses);
    }
    private int calcAccruedBonuses(int realCost) {
        log.debug("calcAccruedBonuses called with realCost " + realCost);
        return BONUS_PERCENT * realCost / 100;
    }

    private void cleanCart(MakeOrderDTO makeOrderDTO) {
        log.debug("cleanCart called");
        String login = makeOrderDTO.employeeLogin();
        if (login == null) {
            login = makeOrderDTO.customerLogin();
        }
        cartRepo.cleanCart(login);
    }
}
