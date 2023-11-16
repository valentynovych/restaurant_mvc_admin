package com.restaurant.restaurant_admin.controller;

import com.restaurant.restaurant_admin.entity.enums.OrderStatus;
import com.restaurant.restaurant_admin.model.*;
import com.restaurant.restaurant_admin.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class OrderController {

    private final OrderService orderService;
    private final int pageSize = 2;

    @GetMapping
    public ModelAndView viewOrders() {
        return new ModelAndView("admin/orders/orders");
    }

    @GetMapping("/edit-order/{orderId}")
    public ModelAndView viewEditOrder(@PathVariable Long orderId) {
        return new ModelAndView("admin/orders/edit-order");
    }

    @GetMapping("/getAllOrders")
    public @ResponseBody Page<OrderShortResponse> getAllOrders(@RequestParam int page) {
        return orderService.getAllOrders(page, pageSize);
    }
    @GetMapping("/getOrdersByFilters")
    public @ResponseBody Page<OrderShortResponse> getOrdersByFilters(@RequestParam int page,
                                                               @RequestParam(required = false) String search,
                                                               @RequestParam(required = false) String date,
                                                               @RequestParam(required = false) OrderStatus status) {
        return orderService.getOrdersByFilters(page, pageSize, search, date, status);
    }

    @GetMapping("/getAllOrderStatus")
    public @ResponseBody ResponseEntity<?> getAllStatus() {
        return new ResponseEntity<>(orderService.getAllOrderStatutes(), HttpStatus.OK);
    }

    @GetMapping("/getOrder/{orderId}")
    public @ResponseBody ResponseEntity<OrderResponse> getOrder(@PathVariable @NotNull Long orderId) {
        return new ResponseEntity<>(orderService.getOrderById(orderId), HttpStatus.OK);
    }

    @GetMapping("/getOrderItemIngredients/{productId}")
    public @ResponseBody ResponseEntity<List<ProductShortResponse>> getProductIngredients(@PathVariable @NotNull Long productId) {
        return new ResponseEntity<>(orderService.getOrderItemIngredients(productId), HttpStatus.OK);
    }

    @PostMapping("/edit-orderItem")
    public @ResponseBody ResponseEntity<OrderResponse> updateOrderItem(@ModelAttribute OrderItemRequest orderItemRequest) {
        return new ResponseEntity<>(orderService.updateOrderItem(orderItemRequest), HttpStatus.OK);
    }

    @PostMapping("/add-orderItem")
    public @ResponseBody ResponseEntity<OrderResponse> addOrderItem(@ModelAttribute OrderItemRequest orderItemRequest) {
        return new ResponseEntity<>(orderService.addOrderItem(orderItemRequest), HttpStatus.OK);
    }

    @GetMapping("/getProducts")
    public @ResponseBody ResponseEntity<?> getIngredients(@RequestParam int page, @RequestParam String search) {
        return new ResponseEntity<>(orderService.getProductsForAdd(page, pageSize, search), HttpStatus.OK);
    }

    @DeleteMapping("/delete-orderItem/{itemId}")
    public @ResponseBody ResponseEntity<?> deleteOrderItem(@PathVariable Long itemId) {
        var isDeleted = orderService.deleteOrderItem(itemId);
        if (isDeleted) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/update-order")
    public @ResponseBody ResponseEntity<?> updateOrder(@Valid @ModelAttribute OrderRequest orderRequest,
                                                       BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseEntity<>(result.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(orderService.updateOrder(orderRequest), HttpStatus.OK);
    }
}
