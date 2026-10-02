package com.ibake.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    @NotNull(message = "Delivery date is required")
    @FutureOrPresent(message = "Delivery date cannot be in the past")
    private LocalDate deliveryDate;

    @NotBlank(message = "Delivery slot is required")
    private String deliverySlot; // e.g. "Morning (9 AM - 1 PM)", "Evening (5 PM - 9 PM)", "Midnight (11 PM - 12 AM)"

    private String customerNotes; // e.g., cake inscription
}
