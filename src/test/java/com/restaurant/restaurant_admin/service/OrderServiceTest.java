package com.restaurant.restaurant_admin.service;

import com.restaurant.restaurant_admin.entity.*;
import com.restaurant.restaurant_admin.entity.enums.OrderStatus;
import com.restaurant.restaurant_admin.entity.enums.PromotionCondition;
import com.restaurant.restaurant_admin.entity.enums.PromotionType;
import com.restaurant.restaurant_admin.entity.enums.Role;
import com.restaurant.restaurant_admin.mapper.*;
import com.restaurant.restaurant_admin.model.order.OrderItemRequest;
import com.restaurant.restaurant_admin.model.order.OrderRequest;
import com.restaurant.restaurant_admin.model.order.OrderResponse;
import com.restaurant.restaurant_admin.model.order.OrderShortResponse;
import com.restaurant.restaurant_admin.model.product.ProductResponse;
import com.restaurant.restaurant_admin.model.product.ProductShortResponse;
import com.restaurant.restaurant_admin.model.promotion.PromotionResponse;
import com.restaurant.restaurant_admin.model.staff.StaffResponse;
import com.restaurant.restaurant_admin.model.user.UserShortResponse;
import com.restaurant.restaurant_admin.repository.OrderItemRepo;
import com.restaurant.restaurant_admin.repository.OrderRepo;
import com.restaurant.restaurant_admin.repository.specification.OrderSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepo orderRepo;
    @Mock
    private ProductService productService;
    @Mock
    private OrderItemRepo orderItemRepo;
    @Mock
    private StaffService staffService;
    @Mock
    private PromotionService promotionService;
    @InjectMocks
    private OrderService orderService;
    private Order order;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.NEW);

        UserDetails userDetails = new UserDetails();
        userDetails.setId(1L);
        userDetails.setDateOfBirth(
                new Date(LocalDate.of(1998, 10, 23)
                        .getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH)));
        userDetails.setFirstName("First");
        userDetails.setLastName("Last");
        userDetails.setRegistrationDate(new Date());
        User user = new User();
        user.setUserDetails(userDetails);
        user.setId(1L);
        user.setEmail("user@gmail.com");
        user.setIsActive(Boolean.TRUE);
        user.setDateTimeOfLastLogin(Instant.now());

        Staff staff = new Staff();
        staff.setId(1L);
        staff.setStaffRole(Role.ROLE_ADMIN);
        staff.setStatus(Boolean.TRUE);
        staff.setEmail("staff@gmail.com");
        staff.setFirstName("FirstName");
        staff.setLastName("LastName");

        Set<OrderItem> orderItems = new HashSet<>();

        for (int i = 0; i < 5; i++) {
            Product product = new Product();
            product.setName("Product");
            product.setId((long) (i + 1));
            product.setPrice(BigDecimal.valueOf(150));

            OrderItem orderItem = new OrderItem();
            orderItem.setId((long) (i + 1));
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setIsGiftProduct(false);
            orderItems.add(orderItem);
        }
        order.setUser(user);
        order.setOrderPlaced(staff);
        order.setOrderItems(orderItems);

        orderRequest = new OrderRequest();
        orderRequest.setOrderId(order.getId());
        UserShortResponse userShortResponse = UserMapper.MAPPER.userToShortRequest(user);
        orderRequest.setUser(userShortResponse);
        StaffResponse staffResponse = StaffMapper.MAPPER.staffToModelResponse(staff);
        orderRequest.setOrderPlaced(staffResponse);
    }

    @Test
    void getAllOrders() {
        Pageable pageable = PageRequest.ofSize(5);
        List<Order> orders = List.of(order, new Order(), new Order(), new Order(), new Order());
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());
        when(orderRepo.findAll(any(Pageable.class))).thenReturn(orderPage);

        Page<OrderShortResponse> allOrders = orderService.getAllOrders(pageable.getPageNumber(), pageable.getPageSize());
        List<OrderShortResponse> content = allOrders.getContent();
        assertFalse(content.isEmpty());
        assertEquals(5, content.size());
    }

    @Test
    void getOrdersByFilters() {
        Pageable pageable = PageRequest.ofSize(5);
        List<Order> orders = List.of(order, new Order(), new Order(), new Order(), new Order());
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());
        when(orderRepo.findAll(any(OrderSpecification.class), any(Pageable.class))).thenReturn(orderPage);

        Page<OrderShortResponse> allOrders = orderService.getOrdersByFilters(
                pageable.getPageNumber(), pageable.getPageSize(), "", "23-11-2023", OrderStatus.NEW);
        List<OrderShortResponse> content = allOrders.getContent();
        assertFalse(content.isEmpty());
        assertEquals(5, content.size());

    }

    @Test
    void getOrdersByUser() {
        Pageable pageable = PageRequest.ofSize(5);
        List<Order> orders = List.of(order, new Order(), new Order(), new Order(), new Order());
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());
        when(orderRepo.findAll(any(OrderSpecification.class), any(Pageable.class))).thenReturn(orderPage);

        Page<OrderShortResponse> allOrders = orderService.getOrdersByUser(
                pageable.getPageNumber(), pageable.getPageSize(), 1L);
        List<OrderShortResponse> content = allOrders.getContent();
        assertFalse(content.isEmpty());
        assertEquals(5, content.size());
    }

    @Test
    void getAllOrderStatutes() {
        Map<String, String> statuses = new HashMap<>();
        Arrays.stream(OrderStatus.values())
                .forEach(orderStatus -> statuses.put(orderStatus.name(), orderStatus.label));

        Map<String, String> allOrderStatutes = orderService.getAllOrderStatutes();
        assertEquals(statuses.size(), allOrderStatutes.size());
    }

    @Test
    void getOrderById() {
        when(orderRepo.findById(order.getId())).thenReturn(Optional.of(order));
        OrderResponse orderById = orderService.getOrderById(order.getId());
        assertEquals(order.getId(), orderById.getOrderId());
        assertEquals(order.getUser().getEmail(), orderById.getUser().getEmail());
    }

    @Test
    void getOrderById_ifOrderNotFound() {
        when(orderRepo.findById(order.getId())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () ->
                orderService.getOrderById(order.getId()));
    }

    @Test
    void getOrderItemIngredients() {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductId(1L);
        productResponse.setConsistsOfIngredients(new ArrayList<>());
        when(productService.getProductById(productResponse.getProductId())).thenReturn(productResponse);

        List<ProductShortResponse> orderItemIngredients =
                orderService.getOrderItemIngredients(productResponse.getProductId());
        assertTrue(orderItemIngredients.isEmpty());
    }

    @Test
    void updateOrderItem_ifOrderNotFound() {
        OrderItemRequest itemRequest = new OrderItemRequest();
        OrderShortResponse orderResponse = new OrderShortResponse();
        orderResponse.setOrderId(order.getId());
        itemRequest.setOrder(orderResponse);
        when(orderRepo.findById(order.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                orderService.updateOrderItem(itemRequest));
    }

    @Test
    void updateOrderItem() {
        OrderItemRequest itemRequest = new OrderItemRequest();
        OrderShortResponse orderResponse = new OrderShortResponse();
        orderResponse.setOrderId(order.getId());
        ProductShortResponse productShortResponse = new ProductShortResponse();
        productShortResponse.setProductId(1L);
        productShortResponse.setPrice(BigDecimal.valueOf(55));
        itemRequest.setOrder(orderResponse);
        itemRequest.setId(1L);
        itemRequest.setProduct(productShortResponse);
        itemRequest.setItemSalePrice(BigDecimal.valueOf(120));
        itemRequest.setAdditionalIngredients(List.of(productShortResponse));
        OrderItem orderItem = OrderMapper.MAPPER.itemResponseToOrderItem(itemRequest);
        order.setUsedBonuses(20);
        order.setUsedPromotion(new HashSet<>());
        when(orderRepo.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderItemRepo.save(any(OrderItem.class))).thenReturn(orderItem);
        OrderResponse orderResponse1 = orderService.updateOrderItem(itemRequest);
        assertNotNull(orderResponse1);
        assertEquals(6, orderResponse1.getOrderItems().size());
        assertEquals(BigDecimal.valueOf(859.8), orderResponse1.getTotalAmount());
    }

    @Test
    void getProductsForAdd() {

        Pageable pageable = PageRequest.ofSize(10);
        List<ProductShortResponse> products =
                List.of(new ProductShortResponse(), new ProductShortResponse(), new ProductShortResponse());
        Page<ProductShortResponse> productPage = new PageImpl<>(products, pageable, products.size());
        when(productService.getProductsWithoutIngredients(pageable.getPageNumber(), pageable.getPageSize(), ""))
                .thenReturn(productPage);
        Page<ProductShortResponse> ingredients =
                orderService.getProductsForAdd(pageable.getPageNumber(), pageable.getPageSize(), "");
        List<ProductShortResponse> content = ingredients.getContent();
        assertFalse(content.isEmpty());
        assertEquals(3, content.size());
    }

    @Test
    void addOrderItem_ifOrderItemHasNewOrder() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product");
        product.setIsActive(Boolean.TRUE);
        product.setPrice(BigDecimal.valueOf(150));

        OrderItemRequest itemRequest = new OrderItemRequest();
        ProductShortResponse productShortResponse = ProductMapper.MAPPER.productToShortResponse(product);
        itemRequest.setOrder(new OrderShortResponse());
        itemRequest.setId(1L);
        itemRequest.setProduct(productShortResponse);
        itemRequest.setItemSalePrice(BigDecimal.valueOf(120));
        itemRequest.setAdditionalIngredients(List.of(productShortResponse));
        OrderItem orderItem = OrderMapper.MAPPER.itemResponseToOrderItem(itemRequest);
        order.setUsedBonuses(20);
        order.setUsedPromotion(new HashSet<>());

        ProductResponse productResponse = ProductMapper.MAPPER.productToProductResponse(product);
        when(productService.getProductById(orderItem.getProduct().getId())).thenReturn(productResponse);
        when(orderItemRepo.save(any(OrderItem.class))).thenReturn(orderItem);
        when(orderRepo.save(any(Order.class))).thenReturn(order);

        OrderResponse orderResponse1 = orderService.addOrderItem(itemRequest);
        assertNotNull(orderResponse1);
    }

    @Test
    void addOrderItem_ifOrderItemHasOrderId() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product");
        product.setIsActive(Boolean.TRUE);
        product.setPrice(BigDecimal.valueOf(150));

        OrderItemRequest itemRequest = new OrderItemRequest();
        OrderShortResponse orderResponse = new OrderShortResponse();
        orderResponse.setOrderId(order.getId());
        ProductShortResponse productShortResponse = ProductMapper.MAPPER.productToShortResponse(product);
        itemRequest.setOrder(orderResponse);
        itemRequest.setId(1L);
        itemRequest.setProduct(productShortResponse);
        itemRequest.setItemSalePrice(BigDecimal.valueOf(120));
        itemRequest.setAdditionalIngredients(List.of(productShortResponse));
        OrderItem orderItem = OrderMapper.MAPPER.itemResponseToOrderItem(itemRequest);
        order.setUsedBonuses(20);
        order.setUsedPromotion(new HashSet<>());

        ProductResponse productResponse = ProductMapper.MAPPER.productToProductResponse(product);
        when(orderRepo.findById(order.getId())).thenReturn(Optional.of(order));
        when(productService.getProductById(orderItem.getProduct().getId())).thenReturn(productResponse);
        when(orderItemRepo.save(any(OrderItem.class))).thenReturn(orderItem);
        when(orderRepo.save(any(Order.class))).thenReturn(order);

        OrderResponse orderResponse1 = orderService.addOrderItem(itemRequest);
        assertNotNull(orderResponse1);
    }

    @Test
    void deleteOrderItem() {
        when(orderItemRepo.existsById(1L)).thenReturn(false);
        boolean isDeleted = orderService.deleteOrderItem(1L);
        assertTrue(isDeleted);
    }

    @Test
    void deleteOrderItem_itemIdIsNull() {
        boolean isDeleted = orderService.deleteOrderItem(null);
        assertFalse(isDeleted);
    }

    @Test
    void createOrder_ifOrderWithUser() {

        Staff staff = order.getOrderPlaced();

        orderRequest.setStatus(OrderStatus.NEW);

        when(orderRepo.findById(1L)).thenReturn(Optional.ofNullable(order));
        when(staffService.getStaffByUsername(staff.getEmail())).thenReturn(staff);
        when(orderRepo.save(any(Order.class))).thenReturn(order);

        SecurityContext context = new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken(staff.getEmail(),
                        staff.getPassword()));
        SecurityContextHolder.setContext(context);
        OrderResponse order1 = orderService.createOrder(orderRequest);
        assertNotNull(order1);
    }

    @Test
    void createOrder_ifOrderWithoutUser() {
        UserShortResponse user = new UserShortResponse();
        user.setUserId(1L);
        user.setTotalOrders(0);
        user.setEmail("user@gmail.com");
        user.setPhone("328065461543");
        user.setFirstName("Name");
        user.setLastName("Last");
        orderRequest.setUser(user);
        StaffResponse staffResponse = new StaffResponse();
        staffResponse.setStaffId(1L);
        staffResponse.setStatus(Boolean.TRUE);
        staffResponse.setEmail("staff@gmail.com");
        orderRequest.setOrderPlaced(staffResponse);
        Staff staff = new Staff();
        staff.setId(staffResponse.getStaffId());
        staff.setStatus(staffResponse.getStatus());
        staff.setEmail(staffResponse.getEmail());

        orderRequest.setStatus(OrderStatus.NEW);
        order.setUser(null);

        when(orderRepo.findById(1L)).thenReturn(Optional.ofNullable(order));
        when(staffService.getStaffByUsername(staffResponse.getEmail())).thenReturn(staff);
        when(orderRepo.save(any(Order.class))).thenReturn(order);

        SecurityContext context = new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken(staff.getEmail(),
                        staff.getPassword()));
        SecurityContextHolder.setContext(context);
        OrderResponse order1 = orderService.createOrder(orderRequest);
        assertNotNull(order1);

    }

    @Test
    void updateOrder_ifOrderNotFound() {
        when(orderRepo.findById(order.getId())).thenReturn(Optional.empty());
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderId(1L);
        assertThrows(EntityNotFoundException.class, () -> orderService.updateOrder(orderRequest));

    }

    @Test
    void updateOrder_withoutPromotion() {

        Staff staff = order.getOrderPlaced();
        order.setDatetimeOfCreate(Instant.now());

        SecurityContext context = new SecurityContextImpl(
                new UsernamePasswordAuthenticationToken(staff.getEmail(),
                        staff.getPassword()));
        SecurityContextHolder.setContext(context);

        when(orderRepo.findById(order.getId())).thenReturn(Optional.ofNullable(order));
        when(staffService.getStaffByUsername(staff.getEmail())).thenReturn(staff);
        when(orderRepo.save(any(Order.class))).thenReturn(order);
        OrderResponse order1 = orderService.updateOrder(orderRequest);
        assertNotNull(order1);
    }

    @Test
    void updateOrder_withPromotionCondition_PERCENT_FOR_CATEGORYAndExistGistProduct() {

        MainCategory mainCategory = new MainCategory();
        mainCategory.setId(1L);
        Subcategory subcategory = new Subcategory();
        subcategory.setId(1L);
        mainCategory.setSubcategories(List.of(subcategory));

        Product product = new Product();
        product.setName("Product");
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(150));
        product.setMainCategory(mainCategory);
        product.setSubcategory(subcategory);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setIsGiftProduct(Boolean.FALSE);
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setId(1L);
        orderItem1.setOrder(order);
        orderItem1.setProduct(product);
        orderItem1.setIsGiftProduct(Boolean.TRUE);
        Set<OrderItem> orderItems = new HashSet<>();
        orderItems.add(orderItem);
        orderItems.add(orderItem1);
        order.setOrderItems(orderItems);
        orderRequest.setStatus(OrderStatus.ACCEPTED);

        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("Promo1");
        promotion.setPromotionType(PromotionType.FOR_PRODUCT);
        promotion.setPromotionCondition(PromotionCondition.PERCENT_FOR_CATEGORY);
        promotion.setIsActive(Boolean.TRUE);
        promotion.setForCategory(mainCategory);
        promotion.setSubcategory(subcategory);
        promotion.setDiscountAmount(15);
        promotion.setGiftProduct(product);
        PromotionResponse promotionResponse = PromotionMapper.MAPPER.promotionToPromotionResponse(promotion);

        orderRequest.setUsedPromotion(List.of(promotionResponse));
        order.setDatetimeOfCreate(Instant.now());
        order.setStatus(OrderStatus.ACCEPTED);

        when(orderRepo.findById(order.getId())).thenReturn(Optional.ofNullable(order));
        when(orderRepo.save(any(Order.class))).thenReturn(order);
        when(promotionService.getPromotion(promotion.getId())).thenReturn(promotionResponse);
        OrderResponse order1 = orderService.updateOrder(orderRequest);
        assertNotNull(order1);
    }

    @Test
    void updateOrder_withPromotionCondition_PERCENT_FOR_CATEGORY() {

        MainCategory mainCategory = new MainCategory();
        mainCategory.setId(1L);
        Subcategory subcategory = new Subcategory();
        subcategory.setId(1L);
        mainCategory.setSubcategories(List.of(subcategory));

        Product product = new Product();
        product.setName("Product");
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(150));
        product.setMainCategory(mainCategory);
        product.setSubcategory(subcategory);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setIsGiftProduct(false);
        order.setOrderItems(Set.of(orderItem));
        orderRequest.setStatus(OrderStatus.ACCEPTED);

        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("Promo1");
        promotion.setPromotionType(PromotionType.FOR_PRODUCT);
        promotion.setPromotionCondition(PromotionCondition.PERCENT_FOR_CATEGORY);
        promotion.setIsActive(Boolean.TRUE);
        promotion.setForCategory(mainCategory);
        promotion.setSubcategory(subcategory);
        promotion.setDiscountAmount(15);
        PromotionResponse promotionResponse = PromotionMapper.MAPPER.promotionToPromotionResponse(promotion);

        orderRequest.setUsedPromotion(List.of(promotionResponse));
        order.setDatetimeOfCreate(Instant.now());
        order.setStatus(OrderStatus.ACCEPTED);

        when(orderRepo.findById(order.getId())).thenReturn(Optional.ofNullable(order));
        when(orderRepo.save(any(Order.class))).thenReturn(order);
        when(promotionService.getPromotion(promotion.getId())).thenReturn(promotionResponse);
        OrderResponse order1 = orderService.updateOrder(orderRequest);
        assertNotNull(order1);
    }

    @Test
    void updateOrder_withPromotionCondition_PERCENT_FOR_PRODUCT() {

        Product product = new Product();
        product.setName("Product");
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(150));

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setIsGiftProduct(false);
        order.setOrderItems(Set.of(orderItem));
        orderRequest.setStatus(OrderStatus.ACCEPTED);

        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("Promo1");
        promotion.setPromotionType(PromotionType.FOR_PRODUCT);
        promotion.setPromotionCondition(PromotionCondition.PERCENT_FOR_PRODUCT);
        promotion.setIsActive(Boolean.TRUE);
        promotion.setForProduct(product);
        promotion.setDiscountAmount(15);
        PromotionResponse promotionResponse = PromotionMapper.MAPPER.promotionToPromotionResponse(promotion);

        orderRequest.setUsedPromotion(List.of(promotionResponse));
        order.setDatetimeOfCreate(Instant.now());
        order.setStatus(OrderStatus.ACCEPTED);

        when(orderRepo.findById(order.getId())).thenReturn(Optional.ofNullable(order));
        when(orderRepo.save(any(Order.class))).thenReturn(order);
        when(promotionService.getPromotion(promotion.getId())).thenReturn(promotionResponse);
        OrderResponse order1 = orderService.updateOrder(orderRequest);
        assertNotNull(order1);
    }

    @Test
    void updateOrder_withPromotionCondition_PERCENT_OF_AMOUNT() {

        Product product = new Product();
        product.setName("Product");
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(150));

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setIsGiftProduct(false);
        order.setOrderItems(Set.of(orderItem));
        orderRequest.setStatus(OrderStatus.ACCEPTED);

        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("Promo1");
        promotion.setPromotionType(PromotionType.FOR_PRODUCT);
        promotion.setPromotionCondition(PromotionCondition.PERCENT_OF_AMOUNT);
        promotion.setIsActive(Boolean.TRUE);
        promotion.setForProduct(product);
        promotion.setDiscountAmount(15);
        promotion.setMinimalAmount(50);
        PromotionResponse promotionResponse = PromotionMapper.MAPPER.promotionToPromotionResponse(promotion);

        orderRequest.setUsedPromotion(List.of(promotionResponse));
        order.setDatetimeOfCreate(Instant.now());
        order.setStatus(OrderStatus.ACCEPTED);

        when(orderRepo.findById(order.getId())).thenReturn(Optional.ofNullable(order));
        when(orderRepo.save(any(Order.class))).thenReturn(order);
        when(promotionService.getPromotion(promotion.getId())).thenReturn(promotionResponse);
        OrderResponse order1 = orderService.updateOrder(orderRequest);
        assertNotNull(order1);
    }

    @Test
    void updateOrder_withPromotionCondition_PERCENT_ON_BIRTHDAY() {

        Product product = new Product();
        product.setName("Product");
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(150));

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setIsGiftProduct(false);
        order.setOrderItems(Set.of(orderItem));
        orderRequest.setStatus(OrderStatus.ACCEPTED);
        order.getUser().getUserDetails().setDateOfBirth(new Date());

        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("Promo1");
        promotion.setPromotionType(PromotionType.FOR_PRODUCT);
        promotion.setPromotionCondition(PromotionCondition.PERCENT_ON_BIRTHDAY);
        promotion.setIsActive(Boolean.TRUE);
        promotion.setDiscountAmount(15);
        PromotionResponse promotionResponse = PromotionMapper.MAPPER.promotionToPromotionResponse(promotion);

        orderRequest.setUsedPromotion(List.of(promotionResponse));
        order.setDatetimeOfCreate(Instant.now());
        order.setStatus(OrderStatus.ACCEPTED);

        when(orderRepo.findById(order.getId())).thenReturn(Optional.ofNullable(order));
        when(orderRepo.save(any(Order.class))).thenReturn(order);
        when(promotionService.getPromotion(promotion.getId())).thenReturn(promotionResponse);
        OrderResponse order1 = orderService.updateOrder(orderRequest);
        assertNotNull(order1);
    }

    @Test
    void updateOrder_withPromotionCondition_FIRST_BYU() {

        Product product = new Product();
        product.setName("Product");
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(150));

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setIsGiftProduct(false);
        order.setOrderItems(Set.of(orderItem));
        orderRequest.setStatus(OrderStatus.ACCEPTED);
        order.getUser().setTotalOrders(0);

        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("Promo1");
        promotion.setPromotionType(PromotionType.FOR_PRODUCT);
        promotion.setPromotionCondition(PromotionCondition.FIRST_BUY);
        promotion.setIsActive(Boolean.TRUE);
        promotion.setDiscountAmount(15);
        PromotionResponse promotionResponse = PromotionMapper.MAPPER.promotionToPromotionResponse(promotion);

        orderRequest.setUsedPromotion(List.of(promotionResponse));
        order.setDatetimeOfCreate(Instant.now());
        order.setStatus(OrderStatus.ACCEPTED);

        when(orderRepo.findById(order.getId())).thenReturn(Optional.ofNullable(order));
        when(orderRepo.save(any(Order.class))).thenReturn(order);
        when(promotionService.getPromotion(promotion.getId())).thenReturn(promotionResponse);
        OrderResponse order1 = orderService.updateOrder(orderRequest);
        assertNotNull(order1);
    }

    @Test
    void updateOrder_withPromotionCondition_FREE_DELIVERY_OF_AMOUNT() {

        Product product = new Product();
        product.setName("Product");
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(150));

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setIsGiftProduct(false);
        order.setOrderItems(Set.of(orderItem));
        orderRequest.setStatus(OrderStatus.ACCEPTED);
        order.getUser().setTotalOrders(0);

        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("Promo1");
        promotion.setPromotionType(PromotionType.FOR_PRODUCT);
        promotion.setPromotionCondition(PromotionCondition.FREE_DELIVERY_OF_AMOUNT);
        promotion.setIsActive(Boolean.TRUE);
        promotion.setMinimalAmount(50);
        PromotionResponse promotionResponse = PromotionMapper.MAPPER.promotionToPromotionResponse(promotion);

        orderRequest.setUsedPromotion(List.of(promotionResponse));
        order.setDatetimeOfCreate(Instant.now());
        order.setStatus(OrderStatus.ACCEPTED);

        when(orderRepo.findById(order.getId())).thenReturn(Optional.ofNullable(order));
        when(orderRepo.save(any(Order.class))).thenReturn(order);
        when(promotionService.getPromotion(promotion.getId())).thenReturn(promotionResponse);
        OrderResponse order1 = orderService.updateOrder(orderRequest);
        assertNotNull(order1);
    }

    @Test
    void updateOrder_withPromotionCondition_THIRD_PRODUCT_ON_GIFTAndFOR_PRODUCT() {

        Product product = new Product();
        product.setName("Product");
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(150));

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setIsGiftProduct(false);
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setId(1L);
        orderItem1.setOrder(order);
        orderItem1.setProduct(product);
        orderItem1.setIsGiftProduct(false);

        Set<OrderItem> orderItems = new HashSet<>();
        orderItems.add(orderItem);
        orderItems.add(orderItem1);
        order.setOrderItems(orderItems);
        orderRequest.setStatus(OrderStatus.ACCEPTED);
        order.getUser().setTotalOrders(0);

        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("Promo1");
        promotion.setPromotionType(PromotionType.FOR_PRODUCT);
        promotion.setPromotionCondition(PromotionCondition.THIRD_PRODUCT_ON_GIFT);
        promotion.setIsActive(Boolean.TRUE);
        promotion.setMinimalAmount(50);
        promotion.setForProduct(product);
        promotion.setGiftProduct(product);
        PromotionResponse promotionResponse = PromotionMapper.MAPPER.promotionToPromotionResponse(promotion);

        orderRequest.setUsedPromotion(List.of(promotionResponse));
        order.setDatetimeOfCreate(Instant.now());
        order.setStatus(OrderStatus.ACCEPTED);

        when(orderRepo.findById(order.getId())).thenReturn(Optional.ofNullable(order));
        when(orderRepo.save(any(Order.class))).thenReturn(order);
        when(promotionService.getPromotion(promotion.getId())).thenReturn(promotionResponse);
        OrderResponse order1 = orderService.updateOrder(orderRequest);
        assertNotNull(order1);
    }

    @Test
    void updateOrder_withPromotionCondition_THIRD_PRODUCT_ON_GIFTAndFOR_CATEGORY() {

        MainCategory mainCategory = new MainCategory();
        mainCategory.setId(1L);
        Subcategory subcategory = new Subcategory();
        subcategory.setId(1L);
        mainCategory.setSubcategories(List.of(subcategory));
        Product product = new Product();
        product.setName("Product");
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(150));
        product.setMainCategory(mainCategory);
        product.setSubcategory(subcategory);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setIsGiftProduct(false);
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setId(1L);
        orderItem1.setOrder(order);
        orderItem1.setProduct(product);
        orderItem1.setIsGiftProduct(false);

        Set<OrderItem> orderItems = new HashSet<>();
        orderItems.add(orderItem);
        orderItems.add(orderItem1);

        order.setOrderItems(orderItems);
        orderRequest.setStatus(OrderStatus.ACCEPTED);
        order.getUser().setTotalOrders(0);

        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("Promo1");
        promotion.setPromotionType(PromotionType.FOR_CATEGORY);
        promotion.setPromotionCondition(PromotionCondition.THIRD_PRODUCT_ON_GIFT);
        promotion.setIsActive(Boolean.TRUE);
        promotion.setForProduct(product);
        promotion.setGiftProduct(product);
        promotion.setForCategory(mainCategory);
        promotion.setSubcategory(subcategory);
        PromotionResponse promotionResponse = PromotionMapper.MAPPER.promotionToPromotionResponse(promotion);

        orderRequest.setUsedPromotion(List.of(promotionResponse));
        order.setDatetimeOfCreate(Instant.now());
        order.setStatus(OrderStatus.ACCEPTED);

        when(orderRepo.findById(order.getId())).thenReturn(Optional.ofNullable(order));
        when(orderRepo.save(any(Order.class))).thenReturn(order);
        when(promotionService.getPromotion(promotion.getId())).thenReturn(promotionResponse);
        OrderResponse order1 = orderService.updateOrder(orderRequest);
        assertNotNull(order1);
    }
}