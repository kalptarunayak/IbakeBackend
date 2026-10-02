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
public class VendorPricingResponse {
    private Long vendorId;
    private String vendorName;
    private Long cityId;
    private String cityName;
    private BigDecimal price;
    private boolean available;
    private Integer stockQuantity;
    private Integer preparationTimeHours;
}
