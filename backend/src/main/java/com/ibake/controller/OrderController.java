package com.ibake.controller;

import com.ibake.dto.ApiResponse;
import com.ibake.dto.CreateOrderRequest;
import com.ibake.dto.OrderResponse;
import com.ibake.dto.OrderStatusUpdateRequest;
import com.ibake.entity.OrderStatus;
import com.ibake.entity.Role;
import com.ibake.security.UserPrincipal;
import com.ibake.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // CUSTOMER: Place order from current active cart
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        OrderResponse order = orderService.createOrderFromCart(userPrincipal.getId(), request);
        return new ResponseEntity<>(ApiResponse.created("Order placed successfully", order), HttpStatus.CREATED);
    }

    // CUSTOMER: View personal order history
    @GetMapping("/my-orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<OrderResponse> orders = orderService.getUserOrders(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Order history retrieved successfully", orders));
    }

    // CUSTOMER (own order) or ADMIN/SUPER_ADMIN: Get order details by ID
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        boolean isAdmin = userPrincipal.getRole() == Role.ADMIN || userPrincipal.getRole() == Role.SUPER_ADMIN;
        OrderResponse order = orderService.getOrderById(id, userPrincipal.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    // ADMIN / SUPER_ADMIN: View all orders with optional filter by status and city
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long cityId
    ) {
        List<OrderResponse> orders = orderService.getAllOrders(status, cityId);
        return ResponseEntity.ok(ApiResponse.ok("Orders retrieved successfully", orders));
    }

    // ADMIN / SUPER_ADMIN: Update order status (PENDING, CONFIRMED, BAKING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusUpdateRequest request
    ) {
        OrderResponse order = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Order status updated to " + request.getStatus(), order));
    }
}
