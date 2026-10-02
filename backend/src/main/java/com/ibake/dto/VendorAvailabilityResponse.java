package com.ibake.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorAvailabilityResponse {
    private Long id;
    private Long vendorId;
    private String vendorName;
    private Long productId;
    private String productName;
    private Long cityId;
    private String cityName;
    private BigDecimal price;
    private boolean available;
    private Integer stockQuantity;
    private Integer preparationTimeHours;
    private LocalDateTime updatedAt;
}
