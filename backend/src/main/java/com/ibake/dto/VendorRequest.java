package com.ibake.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorRequest {

    @NotBlank(message = "Vendor name is required")
    @Size(max = 150, message = "Vendor name cannot exceed 150 characters")
    private String name;

    @Email(message = "Please provide a valid email address")
    private String contactEmail;

    private String contactPhone;

    private String address;

    @Builder.Default
    private boolean active = true;
}
