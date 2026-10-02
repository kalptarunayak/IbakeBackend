package com.ibake.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productImageUrl;
    private boolean isVeg;
    private Long vendorId;
    private String vendorName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
