package com.ibake.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorAvailabilityRequest {

    @NotNull(message = "Vendor ID is required")
    private Long vendorId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "City ID is required")
    private Long cityId;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "1.00", message = "Price must be at least ₹1.00")
    private BigDecimal price;

    @Builder.Default
    private boolean available = true;

    @NotNull(message = "Stock quantity is required")
    @PositiveOrZero(message = "Stock quantity cannot be negative")
    @Builder.Default
    private Integer stockQuantity = 50;

    @NotNull(message = "Preparation time in hours is required")
    @PositiveOrZero(message = "Preparation time cannot be negative")
    @Builder.Default
    private Integer preparationTimeHours = 4;
}
