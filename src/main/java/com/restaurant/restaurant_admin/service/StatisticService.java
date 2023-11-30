package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.Order;
import com.restaurant.restaurant_admin.entity.User;
import com.restaurant.restaurant_admin.entity.enums.OrderStatus;
import com.restaurant.restaurant_admin.model.PopularCategory;
import com.restaurant.restaurant_admin.model.StatisticModel;
import com.restaurant.restaurant_admin.repository.OrderItemRepo;
import com.restaurant.restaurant_admin.repository.OrderRepo;
import com.restaurant.restaurant_admin.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final OrderRepo orderRepo;
    private final UserRepo userRepo;
    private final OrderItemRepo orderItemRepo;

    public Map<String, Integer> getOrderStatistic() {
        Map<String, Integer> orderOnDate = new LinkedHashMap<>();
        Instant today = Instant.now();
        Instant lastMonth = today.minus(30, ChronoUnit.DAYS);

        Instant tempDate = Instant.from(lastMonth);
        List<Order> orderByDatetimeOfCreateAfter =
                orderRepo.findOrderByDatetimeOfCreateAfter(lastMonth);

        while (today.isAfter(tempDate)) {
            LocalDate localDate = LocalDate.ofInstant(tempDate, ZoneId.systemDefault());
            String dateStr = localDate.format(DateTimeFormatter.ofPattern("dd.MM"));
            LocalDateTime startOfDay = localDate.atStartOfDay();
            int countOrderOnDate = (int) orderByDatetimeOfCreateAfter
                    .stream()
                    .filter(order ->
                            order.getDatetimeOfCreate()
                                    .isAfter(startOfDay.atZone(ZoneId.systemDefault()).toInstant())
                                    && order.getDatetimeOfCreate()
                                    .isBefore(startOfDay.plusDays(1).atZone(ZoneId.systemDefault()).toInstant()))
                    .count();
            orderOnDate.put(dateStr, countOrderOnDate);
            tempDate = tempDate.plus(1, ChronoUnit.DAYS);
        }
        return orderOnDate;
    }

    public StatisticModel getGlobalStatistic() {
        Instant today = Instant.now();
        List<Order> orderByDatetimeOfCreateAfter = orderRepo.findOrderByDatetimeOfCreateAfter(
                today.minus(30, ChronoUnit.DAYS));

        BigDecimal amountOfLastMonth = orderByDatetimeOfCreateAfter
                .stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        LocalDateTime startDayToday = LocalDate.ofInstant(today, ZoneId.systemDefault()).atStartOfDay();

        BigDecimal amountOfLastDay = orderByDatetimeOfCreateAfter
                .stream()
                .filter(order -> order.getDatetimeOfCreate().isAfter(startDayToday.atZone(ZoneId.systemDefault()).toInstant()))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer activeOrders = (int) orderByDatetimeOfCreateAfter
                .stream()
                .filter(order -> order.getStatus().equals(OrderStatus.NEW)).count();

        return StatisticModel
                .builder()
                .activeOrders(activeOrders)
                .amountOfLastDay(amountOfLastDay)
                .amountOfLastMonth(amountOfLastMonth)
                .ordersOfLastMonth(orderByDatetimeOfCreateAfter.size())
                .build();
    }

    public Map<String, Integer> getUsersAge() {
        Map<String, Integer> usersAgeMap = new LinkedHashMap<>(6);
        List<User> userList = userRepo.findAll();
        //Instant today = Instant.now();
        ZonedDateTime today = ZonedDateTime.now(ZoneId.systemDefault());

        usersAgeMap.put("Більше 50 років", (int) userList
                .stream()
                .filter(user -> user.getUserDetails().getDateOfBirth() != null)
                .filter(user -> user.getUserDetails().getDateOfBirth().toInstant()
                        .isBefore(today.minusYears(50).toInstant())).count());
        usersAgeMap.put("40 - 49 років", (int) userList
                .stream()
                .filter(user -> user.getUserDetails().getDateOfBirth() != null)
                .filter(user -> user.getUserDetails().getDateOfBirth().toInstant()
                        .isAfter(today.minusYears(49).toInstant())
                        && user.getUserDetails().getDateOfBirth().toInstant()
                        .isBefore(today.minusYears(40).toInstant())).count());
        usersAgeMap.put("30 - 39 років", (int) userList
                .stream()
                .filter(user -> user.getUserDetails().getDateOfBirth() != null)
                .filter(user -> user.getUserDetails().getDateOfBirth().toInstant()
                        .isAfter(today.minusYears(39).toInstant())
                        && user.getUserDetails().getDateOfBirth().toInstant()
                        .isBefore(today.minusYears(30).toInstant())).count());
        usersAgeMap.put("25 - 29 років", (int) userList
                .stream()
                .filter(user -> user.getUserDetails().getDateOfBirth() != null)
                .filter(user -> user.getUserDetails().getDateOfBirth().toInstant()
                        .isAfter(today.minusYears(29).toInstant())
                        && user.getUserDetails().getDateOfBirth().toInstant()
                        .isBefore(today.minusYears(25).toInstant())).count());
        usersAgeMap.put("18 - 24 років", (int) userList
                .stream()
                .filter(user -> user.getUserDetails().getDateOfBirth() != null)
                .filter(user -> user.getUserDetails().getDateOfBirth().toInstant()
                        .isBefore(today.minusYears(18).toInstant())
                        && user.getUserDetails().getDateOfBirth().toInstant()
                        .isAfter(today.minusYears(24).toInstant())).count());
        usersAgeMap.put("Менше 18 років", (int) userList
                .stream()
                .filter(user -> user.getUserDetails().getDateOfBirth() != null)
                .filter(user -> user.getUserDetails().getDateOfBirth().toInstant()
                        .isAfter(today.minusYears(17).toInstant())).count());
        return usersAgeMap;
    }

    public Map<String, Long> getPopularCategory() {
        List<PopularCategory> countUsedMainCategoryInOrders = orderItemRepo.findCountUsedMainCategoryInOrders();
        Map<String, Long> map = countUsedMainCategoryInOrders
                .stream()
                .collect(Collectors.toMap(PopularCategory::getCategoryName, PopularCategory::getCountUsed));
        return map;
    }
}
