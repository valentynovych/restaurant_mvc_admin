package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.*;
import com.restaurant.restaurant_admin.entity.enums.OrderStatus;
import com.restaurant.restaurant_admin.mapper.OrderMapper;
import com.restaurant.restaurant_admin.model.*;
import com.restaurant.restaurant_admin.repository.OrderItemRepo;
import com.restaurant.restaurant_admin.repository.OrderRepo;
import com.restaurant.restaurant_admin.repository.specification.OrderSpecification;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderService {

    private final OrderRepo orderRepo;
    private final ProductService productService;
    private final OrderItemRepo itemRepo;
    private final StaffService staffService;


    public Page<OrderShortResponse> getAllOrders(int page, int pageSize) {
        log.info("method getAllOrders -> start");
        Pageable pageable = PageRequest.of(page, pageSize);
        log.info(String.format("method getAllOrders -> find orders by pageable, page: %s, pageSize: %s", page, pageSize));
        Page<Order> orderPage = orderRepo.findAll(pageable);
        orderPage.getContent().forEach(order -> order.setTotalAmount(countTotalAmount(order)));
        List<OrderShortResponse> shortResponseList = OrderMapper.MAPPER.listOrderToShortResponseList(orderPage.getContent());
        Page<OrderShortResponse> shortResponsePage =
                new PageImpl<>(shortResponseList, pageable, orderPage.getTotalElements());
        log.info(String.format("method getAllOrders -> exit, finds orders - %s", shortResponsePage.getTotalElements()));
        return shortResponsePage;
    }

    public Page<OrderShortResponse> getOrdersByFilters(int page,
                                                       int pageSize,
                                                       String search,
                                                       String date,
                                                       OrderStatus status) {
        log.info("method getOrdersByFilters -> start");
        Pageable pageable = PageRequest.of(page, pageSize);

        Instant dateFrom = null;
        Instant dateTo = null;
        if (date != null) {
            LocalDateTime dateTime = LocalDateTime.of(
                    LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy")), LocalTime.MIDNIGHT);
            ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.systemDefault());
            dateFrom = zonedDateTime.toInstant();
            dateTo = zonedDateTime.toInstant().plus(1, ChronoUnit.DAYS);
        }

        OrderCriteria orderCriteria = OrderCriteria.builder()
                .orderStatus(status)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .search(search)
                .build();

        log.info(String.format("method getOrdersByFilters -> find orders by pageable, page: %s, pageSize: %s", page, pageSize));
        Page<Order> orderPage = orderRepo.findAll(
                new OrderSpecification(orderCriteria)
                        .and(OrderSpecification.joinUser(orderCriteria)), pageable);
        orderPage.getContent().forEach(order -> order.setTotalAmount(countTotalAmount(order)));
        List<OrderShortResponse> shortResponseList =
                OrderMapper.MAPPER.listOrderToShortResponseList(orderPage.getContent());
        Page<OrderShortResponse> shortResponsePage =
                new PageImpl<>(shortResponseList, pageable, orderPage.getTotalElements());
        log.info(String.format("method getOrdersByFilters -> exit, finds orders - %s", shortResponsePage.getTotalElements()));
        return shortResponsePage;
    }

    public Map<String, String> getAllOrderStatutes() {
        log.info("method getAllOrderStatutes -> start");
        Map<String, String> statutes = new HashMap<>();
        Arrays.stream(OrderStatus.values())
                .forEach(orderStatus -> statutes.put(orderStatus.name(), orderStatus.label));
        log.info("method getAllOrderStatutes -> exit, return statuses Map");
        return statutes;
    }

    public OrderResponse getOrderById(Long orderId) {
        log.info(String.format("method getOrderById -> start, find Order by id: %s", orderId));
        Optional<Order> byId = orderRepo.findById(orderId);
        Order order = byId.orElseThrow(EntityNotFoundException::new);
        order.setTotalAmount(countTotalAmount(order));
        log.info("method getOrderById -> Order isPresent, mapping Order to OrderResponse");
        OrderResponse orderResponse = OrderMapper.MAPPER.orderToResponse(order);
        log.info("method getOrderById -> exit, return OrderResponse");
        return orderResponse;
    }

    public List<ProductShortResponse> getOrderItemIngredients(Long productId) {
        log.info(String.format("method getOrderItemIngredients -> start, get Product by id: %s", productId));
        ProductResponse productById = productService.getProductById(productId);
        List<ProductShortResponse> productList = productById.getConsistsOfIngredients();
        log.info("method getOrderItemIngredients -> exit, return List<Product> consistsOfIngredients ");
        return productList;
    }

    public OrderResponse updateOrderItem(OrderItemRequest orderItemRequest) {
        log.info("method updateOrderItem -> map OrderItemRequest to OrderItem");
        OrderItem orderItem = OrderMapper.MAPPER.itemResponseToOrderItem(orderItemRequest);
        log.info(String.format("method updateOrderItem -> get Order by id: %s", orderItem.getOrder().getId()));
        Optional<Order> orderBy = orderRepo.findById(orderItem.getOrder().getId());
        Order order = orderBy.orElseThrow(EntityNotFoundException::new);
        Set<OrderItem> orderItems = order.getOrderItems();
        orderItems.remove(orderItem);
        orderItems.add(itemRepo.save(orderItem));
        log.info("method updateOrderItem -> update OrderItems in Order");
        order.setOrderItems(orderItems);
        order.setTotalAmount(countTotalAmount(order));
        OrderResponse orderResponse = OrderMapper.MAPPER.orderToResponse(order);
        log.info("method updateOrderItem -> exit, return Order");
        return orderResponse;
    }

    private BigDecimal countTotalAmount(Order order) {
        log.info("method countTotalAmount -> start");
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem orderItem : order.getOrderItems()) {
            log.info("method countTotalAmount -> start set itemPrice by id: " + orderItem.getId());
            BigDecimal itemPrice = BigDecimal.ZERO;
            itemPrice = itemPrice.add(orderItem.getProduct().getPrice());
            if (orderItem.getAdditionalIngredients() != null && !orderItem.getAdditionalIngredients().isEmpty()) {
                for (Product additionalIngredient : orderItem.getAdditionalIngredients()) {
                    itemPrice = itemPrice.add(additionalIngredient.getPrice());
                }
                log.info("method countTotalAmount -> OrderItem have additional product in price, id: " + orderItem.getId());
            }
            orderItem.setItemPrice(itemPrice);
            totalAmount = totalAmount.add(itemPrice);
        }
        if (order.getUsedBonuses() != null && order.getUsedBonuses() > 0) {
            log.info("method countTotalAmount -> Order have UsedBonuses, add discount to order, id: " + order.getId());
            totalAmount = totalAmount.subtract(BigDecimal.valueOf(order.getUsedBonuses() * 0.01));
        }
        if (order.getUserPromotion() != null) {
            log.info("method countTotalAmount -> Order have UsedPromotion, add discount to order, id: " + order.getId());
            // TODO: 08.11.2023 add subtract promotion size
        }
        log.info("method countTotalAmount -> exit, return totalAmount order with id: " + order.getId());
        return totalAmount;
    }

    public Page<ProductShortResponse> getProductsForAdd(int page, int pageSize, String search) {
        log.info("method getProductsForAdd -> start, use exterior method");
        return productService.getProductsWithoutIngredients(page, pageSize, search);
    }

    public OrderResponse addOrderItem(OrderItemRequest orderItemRequest) {
        log.info("method addOrderItem -> start");
        OrderItem orderItem = OrderMapper.MAPPER.itemResponseToOrderItem(orderItemRequest);
        Optional<Order> orderBy = orderRepo.findById(orderItem.getOrder().getId());
        Order order = orderBy.orElseThrow(EntityNotFoundException::new);
        order.getOrderItems().add(itemRepo.save(orderItem));
        order.setTotalAmount(countTotalAmount(order));
        Order save = orderRepo.save(order);
        OrderResponse orderResponse = OrderMapper.MAPPER.orderToResponse(save);
        log.info("method addOrderItem -> exit, return OrderResponse");
        return orderResponse;
    }

    public boolean deleteOrderItem(Long itemId) {
        log.info("method deleteOrderItem -> start");
        if (itemId != null) {
            itemRepo.deleteById(itemId);
            log.info(String.format("method deleteOrderItem -> OrderItem with id: %s - has been deleted", itemId));
            return !itemRepo.existsById(itemId);
        }
        log.info("method deleteOrderItem -> exit without delete, because itemId is null");
        return false;
    }

    public OrderResponse updateOrder(OrderRequest orderRequest) {
        log.info("method updateOrder -> start");
        Order request = OrderMapper.MAPPER.requestToOrder(orderRequest);
        Optional<Order> byId = orderRepo.findById(request.getId());
        Order orderByDb = byId.orElseThrow(EntityNotFoundException::new);
        request.setOrderItems(orderByDb.getOrderItems());
        request.setDatetimeOfCreate(orderByDb.getDatetimeOfCreate());

        User user = orderByDb.getUser();
        UserDetails userDetails = orderByDb.getUser().getUserDetails();
        UserDetails userDetailsRequest = request.getUser().getUserDetails();
        userDetails.setLastName(userDetailsRequest.getLastName());
        userDetails.setFirstName(userDetailsRequest.getFirstName());
        userDetails.setPhone(userDetailsRequest.getPhone());
        user.setUserDetails(userDetails);
        request.setUser(user);

        if (orderByDb.getStatus().equals(OrderStatus.NEW)) {
            log.info("method updateOrder -> is first edit order, set orderPlaced");
            SecurityContext context = SecurityContextHolder.getContext();
            String currUsername = context.getAuthentication().getName();
            Staff staff = staffService.getStaffByUsername(currUsername);
            request.setOrderPlaced(staff);
        }

        Order save = orderRepo.save(request);
        OrderResponse orderResponse = OrderMapper.MAPPER.orderToResponse(save);
        log.info("method updateOrder -> exit");
        return orderResponse;
    }
}
