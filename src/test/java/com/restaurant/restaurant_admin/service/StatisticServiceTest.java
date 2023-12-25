package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.Order;
import com.restaurant.restaurant_admin.entity.Staff;
import com.restaurant.restaurant_admin.entity.User;
import com.restaurant.restaurant_admin.entity.UserDetails;
import com.restaurant.restaurant_admin.entity.enums.OrderStatus;
import com.restaurant.restaurant_admin.model.PopularCategory;
import com.restaurant.restaurant_admin.model.StaffResponse;
import com.restaurant.restaurant_admin.model.StatisticModel;
import com.restaurant.restaurant_admin.repository.OrderItemRepo;
import com.restaurant.restaurant_admin.repository.OrderRepo;
import com.restaurant.restaurant_admin.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticServiceTest {

    @Mock
    private OrderRepo orderRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private OrderItemRepo orderItemRepo;
    @Mock
    private StaffService staffService;
    @InjectMocks
    private StatisticService statisticService;

    @Test
    void getOrderStatistic() {
        List<Order> orders = new ArrayList<>();
        Instant now = Instant.now();
        for (int i = 0; i < 30; i++) {
            Order order = new Order();
            order.setTotalAmount(BigDecimal.valueOf(150));
            order.setDatetimeOfCreate(now.minus(i, ChronoUnit.DAYS));
            order.setStatus(OrderStatus.NEW);
            orders.add(order);
        }
        when(orderRepo.findOrderByDatetimeOfCreateAfter(any(Instant.class)))
                .thenReturn(orders);
        Map<String, Integer> orderStatistic = statisticService.getOrderStatistic();
        assertEquals(30, orderStatistic.size());
    }

    @Test
    void getGlobalStatistic() {
        List<Order> orders = new ArrayList<>();
        Instant now = Instant.now();
        for (int i = 0; i < 30; i++) {
            Order order = new Order();
            order.setTotalAmount(BigDecimal.valueOf(150));
            order.setDatetimeOfCreate(now.minus(i, ChronoUnit.DAYS));
            order.setStatus(OrderStatus.NEW);
            orders.add(order);
        }
        when(orderRepo.findOrderByDatetimeOfCreateAfter(any(Instant.class)))
                .thenReturn(orders);
        StatisticModel globalStatistic = statisticService.getGlobalStatistic();
        assertEquals(30, globalStatistic.getActiveOrders());
        assertEquals(BigDecimal.valueOf(150), globalStatistic.getAmountOfLastDay());
        assertEquals(BigDecimal.valueOf(150 * 30), globalStatistic.getAmountOfLastMonth());
        assertEquals(30, globalStatistic.getOrdersOfLastMonth());
    }

    @Test
    void getUsersAge() {
        List<User> users = new ArrayList<>();

        Instant now = Instant.now();
        for (long i = 0; i < 40; i++) {
            User user = new User();
            UserDetails userDetails = new UserDetails();
            userDetails.setDateOfBirth(
                    Date.from(now.minus((i + 5) * 365, ChronoUnit.DAYS)));
            user.setUserDetails(userDetails);
            users.add(user);
        }
        when(userRepo.findAll()).thenReturn(users);
        Map<String, Integer> usersAge = statisticService.getUsersAge();
        assertEquals(6, usersAge.size());
    }

    @Test
    void getPopularCategory() {
        List<PopularCategory> popularCategories = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            PopularCategory popularCategory =
                    new PopularCategory("Category_" + i, (long) (i * 5));
            popularCategories.add(popularCategory);
        }
        when(orderItemRepo.findCountUsedMainCategoryInOrders()).thenReturn(popularCategories);
        Map<String, Long> popularCategory = statisticService.getPopularCategory();
        assertEquals(5, popularCategory.size());
    }

    @Test
    void getCurrentStaff() {
        Staff staff = new Staff();
        staff.setId(1L);
        staff.setEmail("staff@gmail.com");
        staff.setPassword("");
        Principal mockPrincipal = Mockito.mock(Principal.class);

        when(mockPrincipal.getName()).thenReturn(staff.getUsername());
        when(staffService.getStaffByUsername(staff.getEmail())).thenReturn(staff);
        StaffResponse currentStaff = statisticService.getCurrentStaff(mockPrincipal);
        assertEquals(staff.getEmail(), currentStaff.getEmail());
    }

    @Test
    void getCurrentStaff_ifPrincipalIsNull() {
        StaffResponse currentStaff = statisticService.getCurrentStaff(null);
        assertNull(currentStaff);
    }
}