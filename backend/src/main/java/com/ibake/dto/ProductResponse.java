package com.ibake.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private Long occasionId;
    private String occasionName;
    private boolean isVeg;
    private Integer weightInGrams;
    private boolean active;
    private BigDecimal startingPrice; // Minimum price among active vendors in requested city
    private List<VendorPricingResponse> availableVendors;
    private LocalDateTime createdAt;
}
