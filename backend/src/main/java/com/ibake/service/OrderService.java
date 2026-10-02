package com.ibake.service;

import com.ibake.dto.CreateOrderRequest;
import com.ibake.dto.OrderResponse;
import com.ibake.dto.OrderStatusUpdateRequest;
import com.ibake.entity.OrderStatus;

import java.util.List;

public interface OrderService {
    OrderResponse createOrderFromCart(Long userId, CreateOrderRequest request);
    List<OrderResponse> getUserOrders(Long userId);
    OrderResponse getOrderById(Long orderId, Long requestingUserId, boolean isAdmin);
    List<OrderResponse> getAllOrders(OrderStatus status, Long cityId);
    OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest request);
}
