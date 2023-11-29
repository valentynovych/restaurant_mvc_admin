package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.*;
import com.restaurant.restaurant_admin.entity.enums.OrderStatus;
import com.restaurant.restaurant_admin.entity.enums.PromotionType;
import com.restaurant.restaurant_admin.mapper.OrderMapper;
import com.restaurant.restaurant_admin.mapper.ProductMapper;
import com.restaurant.restaurant_admin.mapper.PromotionMapper;
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
import org.springframework.data.jpa.domain.Specification;
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
    private final PromotionService promotionService;


    public Page<OrderShortResponse> getAllOrders(int page, int pageSize) {
        log.info("method getAllOrders -> start");
        Pageable pageable = PageRequest.of(page, pageSize);
        log.info(String.format("method getAllOrders -> find orders by pageable, page: %s, pageSize: %s", page, pageSize));
        Page<Order> orderPage = orderRepo.findAll(pageable);
        //orderPage.getContent().forEach(order -> order.setTotalAmount(countTotalAmount(order)));
        List<OrderShortResponse> shortResponseList = OrderMapper.MAPPER.listOrderToShortResponseList(orderPage.getContent());
        Page<OrderShortResponse> shortResponsePage =
                new PageImpl<>(shortResponseList, pageable, orderPage.getTotalElements());
        log.info(String.format("method getAllOrders -> exit, finds orders - %s", shortResponsePage.getTotalElements()));
        return shortResponsePage;
    }

    public Page<OrderShortResponse> getOrdersByFilters(int page, int pageSize, String search,
                                                       String date, OrderStatus status) {
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
        Page<Order> orderPage = orderRepo.findAll(Specification.where(
                new OrderSpecification(orderCriteria)), pageable);
        //orderPage.getContent().forEach(order -> order.setTotalAmount(countTotalAmount(order)));
        List<OrderShortResponse> shortResponseList =
                OrderMapper.MAPPER.listOrderToShortResponseList(orderPage.getContent());
        Page<OrderShortResponse> shortResponsePage =
                new PageImpl<>(shortResponseList, pageable, orderPage.getTotalElements());
        log.info(String.format("method getOrdersByFilters -> exit, finds orders - %s", shortResponsePage.getTotalElements()));
        return shortResponsePage;
    }

    public Page<OrderShortResponse> getOrdersByUser(int page, int pageSize, Long userId) {
        log.info("method getOrdersByUser -> start");
        Pageable pageable = PageRequest.of(page, pageSize);
        OrderCriteria orderCriteria = OrderCriteria.builder()
                .userId(userId)
                .build();

        log.info(String.format("method getOrdersByUser -> find orders by pageable, page: %s, pageSize: %s", page, pageSize));
        Page<Order> orderPage = orderRepo.findAll(Specification.where(
                new OrderSpecification(orderCriteria)), pageable);

        List<OrderShortResponse> shortResponseList =
                OrderMapper.MAPPER.listOrderToShortResponseList(orderPage.getContent());
        Page<OrderShortResponse> shortResponsePage =
                new PageImpl<>(shortResponseList, pageable, orderPage.getTotalElements());
        log.info(String.format("method getOrdersByUser -> exit, finds orders - %s", shortResponsePage.getTotalElements()));
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
        //order = countTotalAmount(order);
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
        order = modifyPromotionDetails(order);
        countTotalAmount(order);
        OrderResponse orderResponse = OrderMapper.MAPPER.orderToResponse(order);
        log.info("method updateOrderItem -> exit, return Order");
        return orderResponse;
    }

    private Order countTotalAmount(Order order) {
        log.info("method countTotalAmount -> start");
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem orderItem : order.getOrderItems()) {
            log.info("method countTotalAmount -> start set itemPrice by id: " + orderItem.getId());
            BigDecimal itemPrice = orderItem.getProduct().getPrice();
            BigDecimal itemSalePrice = orderItem.getItemSalePrice();
            BigDecimal additionalPrice = BigDecimal.ZERO;
            if (orderItem.getAdditionalIngredients() != null && !orderItem.getAdditionalIngredients().isEmpty()) {
                for (Product additionalIngredient : orderItem.getAdditionalIngredients()) {
                    additionalPrice = additionalPrice.add(additionalIngredient.getPrice());
                    if (itemSalePrice != null) itemSalePrice = itemSalePrice.add(additionalPrice);
                }
                log.info("method countTotalAmount -> OrderItem have additional product in price, id: " + orderItem.getId());
            }
            orderItem.setItemPrice(itemPrice.add(additionalPrice));
            orderItem.setItemSalePrice(itemSalePrice);
            totalAmount = totalAmount.add(itemSalePrice != null ? orderItem.getItemSalePrice() : orderItem.getItemPrice());
        }
        if (order.getUsedBonuses() != null && order.getUsedBonuses() > 0) {
            log.info("method countTotalAmount -> Order have UsedBonuses, add discount to order, id: " + order.getId());
            totalAmount = totalAmount.subtract(BigDecimal.valueOf(order.getUsedBonuses() * 0.01));
        }
        if (order.getUsedPromotion() != null) {
            log.info("method countTotalAmount -> Order have UsedPromotion, add discount to order, id: " + order.getId());
            // TODO: 08.11.2023 add subtract promotion size
        }
        log.info("method countTotalAmount -> exit, return totalAmount order with id: " + order.getId());
        order.setTotalAmount(totalAmount);
        return order;
    }

    public Page<ProductShortResponse> getProductsForAdd(int page, int pageSize, String search) {
        log.info("method getProductsForAdd -> start, use exterior method");
        return productService.getProductsWithoutIngredients(page, pageSize, search);
    }

    public OrderResponse addOrderItem(OrderItemRequest orderItemRequest) {
        log.info("method addOrderItem -> start");
        OrderItem orderItem = OrderMapper.MAPPER.itemResponseToOrderItem(orderItemRequest);
        Optional<Order> orderBy;
        if (orderItem.getOrder().getId() != null && orderItem.getOrder().getId() > 0) {
            orderBy = orderRepo.findById(orderItem.getOrder().getId());
        } else {
            return createOrderWithOrderItem(orderItem);
        }
        Order order = orderBy.orElseThrow(EntityNotFoundException::new);
        ProductResponse itemProduct = productService.getProductById(orderItem.getProduct().getId());
        orderItem.setProduct(ProductMapper.MAPPER.productResponseToProduct(itemProduct));
        orderItem.setItemPrice(itemProduct.getPrice());
        order.getOrderItems().add(itemRepo.save(orderItem));
        order = modifyPromotionDetails(order);
        order = countTotalAmount(order);
        Order save = orderRepo.save(order);
        OrderResponse orderResponse = OrderMapper.MAPPER.orderToResponse(save);
        log.info("method addOrderItem -> exit, return OrderResponse");
        return orderResponse;
    }

    private OrderResponse createOrderWithOrderItem(OrderItem orderItem) {
        Order order = orderRepo.save(new Order());
        orderItem.setOrder(order);
        ProductResponse product = productService.getProductById(orderItem.getProduct().getId());
        orderItem.setItemPrice(product.getPrice());
        orderItem.setIsGiftProduct(Boolean.FALSE);
        order.setOrderItems(Set.of(itemRepo.save(orderItem)));
        order = countTotalAmount(order);
        OrderResponse save = OrderMapper.MAPPER.orderToResponse(order);
        return save;
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

    public OrderResponse createOrder(OrderRequest orderRequest) {
        log.info("method createOrder -> start");
        Order request = OrderMapper.MAPPER.requestToOrder(orderRequest);
        Optional<Order> byId = orderRepo.findById(request.getId());
        Order orderByDb = byId.orElseThrow(EntityNotFoundException::new);
        request.setOrderItems(orderByDb.getOrderItems());
        request.setDatetimeOfCreate(Instant.now());

        User user;
        if (orderByDb.getUser() != null) {
            user = orderByDb.getUser();
        } else {
            user = request.getUser();
        }

        UserDetails userDetails = user.getUserDetails();
        UserDetails userDetailsRequest = request.getUser().getUserDetails();
        userDetails.setLastName(userDetailsRequest.getLastName());
        userDetails.setFirstName(userDetailsRequest.getFirstName());
        userDetails.setPhone(userDetailsRequest.getPhone());
        user.setUserDetails(userDetails);
        request.setUser(user);

        SecurityContext context = SecurityContextHolder.getContext();
        String currUsername = context.getAuthentication().getName();
        Staff staff = staffService.getStaffByUsername(currUsername);
        request.setOrderPlaced(staff);

        Order save = orderRepo.save(request);
        OrderResponse orderResponse = OrderMapper.MAPPER.orderToResponse(save);
        log.info("method createOrder -> exit");
        return orderResponse;
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
        Order order = countTotalAmount(request);

        Order save = orderRepo.save(modifyPromotionDetails(order));
        OrderResponse orderResponse = OrderMapper.MAPPER.orderToResponse(save);
        log.info("method updateOrder -> exit");
        return orderResponse;
    }

    private Order modifyPromotionDetails(Order order) {
        if (order.getUsedPromotion() != null && !order.getUsedPromotion().isEmpty()) {
            for (Promotion promotion : order.getUsedPromotion()) {
                promotion = PromotionMapper.MAPPER.promotionResponseToPromotion(
                        promotionService.getPromotion(promotion.getId()));
                if (promotion.getIsActive().equals(Boolean.TRUE)) {
                    order = conditionManager(order, promotion);
                }
            }
        }
        return order;
    }

    private Order conditionManager(Order order, Promotion promotion) {
        Set<OrderItem> orderItems = new HashSet<>(order.getOrderItems());
        List<OrderItem> orderItemList = new ArrayList<>(order.getOrderItems());

        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem item = orderItemList.get(i);
            if (item.getIsGiftProduct() != null && item.getIsGiftProduct()) break;
            List<OrderItem> giftsProduct = orderItems.stream().filter(OrderItem::getIsGiftProduct).toList();
            if (!giftsProduct.isEmpty() &&
                    giftsProduct.stream().anyMatch(orderItem ->
                            orderItem.getProduct().getId().equals(promotion.getGiftProduct().getId()))) {
                break;
            }

            switch (promotion.getPromotionCondition()) {
                case PERCENT_FOR_CATEGORY -> {
                    var product = item.getProduct();
                    if (product.getMainCategory().getId().equals(promotion.getForCategory().getId())
                            && product.getSubcategory().getId().equals(promotion.getSubcategory().getId())) {
                        item.setItemSalePrice(calculateSalePriceOnPercent(item.getItemPrice(), promotion.getDiscountAmount()));
                    }
                    item.setItemSalePrice(
                            calculateSalePriceOnPercent(item.getItemPrice(), promotion.getDiscountAmount()));
                }
                case PERCENT_FOR_PRODUCT -> {
                    if (item.getProduct().getId().equals(promotion.getForProduct().getId())) {
                        item.setItemSalePrice(calculateSalePriceOnPercent(item.getItemPrice(), promotion.getDiscountAmount()));
                    }
                }
                case PERCENT_OF_AMOUNT -> {
                    if (order.getTotalAmount().compareTo(BigDecimal.valueOf(promotion.getMinimalAmount())) > 0) {
                        item.setItemSalePrice(calculateSalePriceOnPercent(item.getItemPrice(), promotion.getDiscountAmount()));
                    }
                }
                case PERCENT_ON_BIRTHDAY -> {
                    var orderedUser = order.getUser();
                    var today = new Date();
                    if (orderedUser.getUserDetails().getDateOfBirth().equals(today)) {
                        item.setItemSalePrice(calculateSalePriceOnPercent(item.getItemPrice(), promotion.getDiscountAmount()));
                    }
                }
                case FIRST_BUY -> {
                    var orderedUser = order.getUser();
                    if (orderedUser.getTotalOrders() == 0) {
                        item.setItemSalePrice(calculateSalePriceOnPercent(item.getItemPrice(), promotion.getDiscountAmount()));
                    }
                }
                case FREE_DELIVERY_OF_AMOUNT -> {
                    if (order.getTotalAmount().compareTo(BigDecimal.valueOf(promotion.getMinimalAmount())) > 0) {
                        order.setTotalAmount(order.getTotalAmount().subtract(new BigDecimal(50)));
                    }
                }
                case THIRD_PRODUCT_ON_GIFT -> {
                    var promotionType = promotion.getPromotionType();
                    if (promotionType.equals(PromotionType.FOR_PRODUCT)) {
                        List<OrderItem> filtered = orderItemList.stream().filter(orderItem ->
                                orderItem.getProduct().getId().equals(promotion.getForProduct().getId())).toList();
                        if (!filtered.isEmpty() && filtered.size() >= 2) {
                            order.getOrderItems().add(itemRepo.save(createGiftOrderItem(promotion.getGiftProduct(), order)));
                            filtered.forEach(orderItemList::remove);
                        }
                    } else if (promotionType.equals(PromotionType.FOR_CATEGORY)) {
                        List<OrderItem> filtered = orderItemList.stream().filter(orderItem ->
                                orderItem.getProduct().getMainCategory().getId().equals(promotion.getForCategory().getId())
                                        && orderItem.getProduct().getSubcategory().getId().equals(
                                        promotion.getSubcategory().getId())).toList();
                        if (!filtered.isEmpty() && filtered.size() >= 2) {
                            order.getOrderItems().add(itemRepo.save(createGiftOrderItem(promotion.getGiftProduct(), order)));
                            filtered.forEach(orderItemList::remove);
                        }
                    }
                }
            }

            item.setPromotionCondition(promotion.getPromotionCondition());
            item.setPromotionType(promotion.getPromotionType());
            item.setDiscountAmount(promotion.getDiscountAmount());
            item.setMinimalAmount(promotion.getMinimalAmount());
            item.setPromoCode(promotion.getPromoCode());
            item.setPromotionName(promotion.getName());
        }
//        for (OrderItem item : order.getOrderItems()) {
//            if (item.getIsGiftProduct() != null && item.getIsGiftProduct()) break;
//            List<OrderItem> giftsProduct = orderItems.stream().filter(OrderItem::getIsGiftProduct).toList();
//            if (!giftsProduct.isEmpty() &&
//                    giftsProduct.stream().anyMatch(orderItem ->
//                            orderItem.getProduct().getId().equals(promotion.getGiftProduct().getId()))) {
//                break;
//            }
//
//            switch (promotion.getPromotionCondition()) {
//                case PERCENT_FOR_CATEGORY -> {
//                    var product = item.getProduct();
//                    if (product.getMainCategory().getId().equals(promotion.getForCategory().getId())
//                            && product.getSubcategory().getId().equals(promotion.getSubcategory().getId())) {
//                        item.setItemSalePrice(calculateSalePriceOnPercent(item.getItemPrice(), promotion.getDiscountAmount()));
//                    }
//                    item.setItemSalePrice(
//                            calculateSalePriceOnPercent(item.getItemPrice(), promotion.getDiscountAmount()));
//                }
//                case PERCENT_FOR_PRODUCT -> {
//                    if (item.getProduct().getId().equals(promotion.getForProduct().getId())) {
//                        item.setItemSalePrice(calculateSalePriceOnPercent(item.getItemPrice(), promotion.getDiscountAmount()));
//                    }
//                }
//                case PERCENT_OF_AMOUNT -> {
//                    if (order.getTotalAmount().compareTo(BigDecimal.valueOf(promotion.getMinimalAmount())) > 0) {
//                        item.setItemSalePrice(calculateSalePriceOnPercent(item.getItemPrice(), promotion.getDiscountAmount()));
//                    }
//                }
//                case PERCENT_ON_BIRTHDAY -> {
//                    var orderedUser = order.getUser();
//                    var today = new Date();
//                    if (orderedUser.getUserDetails().getDateOfBirth().equals(today)) {
//                        item.setItemSalePrice(calculateSalePriceOnPercent(item.getItemPrice(), promotion.getDiscountAmount()));
//                    }
//                }
//                case FIRST_BUY -> {
//                    var orderedUser = order.getUser();
//                    if (orderedUser.getTotalOrders() == 0) {
//                        item.setItemSalePrice(calculateSalePriceOnPercent(item.getItemPrice(), promotion.getDiscountAmount()));
//                    }
//                }
//                case FREE_DELIVERY_OF_AMOUNT -> {
//                    if (order.getTotalAmount().compareTo(BigDecimal.valueOf(promotion.getMinimalAmount())) > 0) {
//                        order.setTotalAmount(order.getTotalAmount().subtract(new BigDecimal(50)));
//                    }
//                }
//                case THIRD_PRODUCT_ON_GIFT -> {
//                    var promotionType = promotion.getPromotionType();
//                    if (promotionType.equals(PromotionType.FOR_PRODUCT)) {
//                        List<OrderItem> filtered = orderItems.stream().filter(orderItem ->
//                                orderItem.getProduct().getId().equals(promotion.getForProduct().getId())).toList();
//                        if (!filtered.isEmpty() && filtered.size() >= 2) {
//                            orderItems.add(itemRepo.save(createGiftOrderItem(promotion.getGiftProduct(), order)));
//                            filtered.forEach(orderItems::remove);
//                        }
//                    } else if (promotionType.equals(PromotionType.FOR_CATEGORY)) {
//                        List<OrderItem> filtered = orderItems.stream().filter(orderItem ->
//                                orderItem.getProduct().getMainCategory().getId().equals(promotion.getForCategory().getId())
//                                        && orderItem.getProduct().getSubcategory().getId().equals(
//                                        promotion.getSubcategory().getId())).toList();
//                        if (!filtered.isEmpty() && filtered.size() >= 2) {
//                            orderItems.add(itemRepo.save(createGiftOrderItem(promotion.getGiftProduct(), order)));
//                            filtered.forEach(orderItems::remove);
//                        }
//                    }
//                }
//            }
//
//            item.setPromotionCondition(promotion.getPromotionCondition());
//            item.setPromotionType(promotion.getPromotionType());
//            item.setDiscountAmount(promotion.getDiscountAmount());
//            item.setMinimalAmount(promotion.getMinimalAmount());
//            item.setPromoCode(promotion.getPromoCode());
//            item.setPromotionName(promotion.getName());
//        }
        return order;
    }

    private OrderItem createGiftOrderItem(Product giftProduct, Order order) {
        OrderItem orderItem = new OrderItem();
        orderItem.setIsGiftProduct(Boolean.TRUE);
        orderItem.setItemSalePrice(new BigDecimal("0.1"));
        orderItem.setItemPrice(giftProduct.getPrice());
        orderItem.setProduct(giftProduct);
        orderItem.setOrder(order);
        return orderItem;
    }

    private BigDecimal calculateSalePriceOnPercent(BigDecimal price, Integer discount) {
        return price.subtract(price.multiply(new BigDecimal(discount).divide(new BigDecimal(100))));
    }
}
