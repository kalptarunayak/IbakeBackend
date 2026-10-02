package com.ibake.service.impl;

import com.ibake.dto.CreateOrderRequest;
import com.ibake.dto.OrderItemResponse;
import com.ibake.dto.OrderResponse;
import com.ibake.dto.OrderStatusUpdateRequest;
import com.ibake.entity.*;
import com.ibake.exception.BadRequestException;
import com.ibake.exception.ForbiddenException;
import com.ibake.exception.ResourceNotFoundException;
import com.ibake.repository.CartRepository;
import com.ibake.repository.OrderRepository;
import com.ibake.repository.UserRepository;
import com.ibake.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public OrderResponse createOrderFromCart(Long userId, CreateOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Shopping cart is empty"));

        if (cart.getItems().isEmpty() || cart.getCity() == null) {
            throw new BadRequestException("Cannot place order with an empty cart");
        }

        City city = cart.getCity();
        if (!city.isActive()) {
            throw new BadRequestException("Deliveries are currently disabled for " + city.getName());
        }

        String orderNumber = generateOrderNumber();

        Order order = Order.builder()
                .orderNumber(orderNumber)
                .user(user)
                .city(city)
                .deliveryAddress(request.getDeliveryAddress().trim())
                .deliveryDate(request.getDeliveryDate())
                .deliverySlot(request.getDeliverySlot().trim())
                .customerNotes(request.getCustomerNotes() != null ? request.getCustomerNotes().trim() : null)
                .status(OrderStatus.CONFIRMED)
                .items(new ArrayList<>())
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getItems()) {
            BigDecimal subtotal = cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            total = total.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(cartItem.getProduct())
                    .vendor(cartItem.getVendor())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getUnitPrice())
                    .subtotal(subtotal)
                    .build();
            order.getItems().add(orderItem);
        }

        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);

        // Clear user's cart after successful checkout
        cart.getItems().clear();
        cart.setCity(null);
        cartRepository.save(cart);

        return mapToOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, Long requestingUserId, boolean isAdmin) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!isAdmin && !order.getUser().getId().equals(requestingUserId)) {
            throw new ForbiddenException("You do not have permission to view this order");
        }

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders(OrderStatus status, Long cityId) {
        return orderRepository.findAllFiltered(status, cityId).stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        order.setStatus(request.getStatus());
        Order updated = orderRepository.save(order);
        return mapToOrderResponse(updated);
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        int randSuffix = 1000 + secureRandom.nextInt(9000);
        return "IBK-" + timestamp + "-" + randSuffix;
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productImageUrl(item.getProduct().getImageUrl())
                        .vendorId(item.getVendor().getId())
                        .vendorName(item.getVendor().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUser().getId())
                .userEmail(order.getUser().getEmail())
                .userFullName(order.getUser().getFullName())
                .cityId(order.getCity().getId())
                .cityName(order.getCity().getName())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryDate(order.getDeliveryDate())
                .deliverySlot(order.getDeliverySlot())
                .customerNotes(order.getCustomerNotes())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
