package com.ibake.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerRequest {

    @NotBlank(message = "Banner title is required")
    @Size(max = 150, message = "Banner title cannot exceed 150 characters")
    private String title;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private String redirectUrl;

    private Long cityId; // Nullable for nationwide banners

    private Long occasionId; // Nullable for general promotion

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @Builder.Default
    private boolean active = true;
}
