package com.ibake.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 150, message = "Product name cannot exceed 150 characters")
    private String name;

    private String description;

    private String imageUrl;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private Long occasionId;

    @Builder.Default
    private boolean isVeg = true;

    @Positive(message = "Weight in grams must be positive")
    private Integer weightInGrams;

    @Builder.Default
    private boolean active = true;
}
