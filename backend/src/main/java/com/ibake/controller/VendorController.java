package com.ibake.controller;

import com.ibake.dto.*;
import com.ibake.service.VendorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class VendorController {

    private final VendorService vendorService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VendorResponse>>> getAllVendors() {
        List<VendorResponse> vendors = vendorService.getAllVendors();
        return ResponseEntity.ok(ApiResponse.ok("Vendors retrieved successfully", vendors));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorResponse>> getVendorById(@PathVariable Long id) {
        VendorResponse vendor = vendorService.getVendorById(id);
        return ResponseEntity.ok(ApiResponse.ok(vendor));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VendorResponse>> createVendor(@Valid @RequestBody VendorRequest request) {
        VendorResponse vendor = vendorService.createVendor(request);
        return new ResponseEntity<>(ApiResponse.created("Vendor created successfully", vendor), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorResponse>> updateVendor(
            @PathVariable Long id,
            @Valid @RequestBody VendorRequest request
    ) {
        VendorResponse vendor = vendorService.updateVendor(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Vendor updated successfully", vendor));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteVendor(@PathVariable Long id) {
        vendorService.deleteVendor(id);
        return ResponseEntity.ok(ApiResponse.ok("Vendor deleted successfully", null));
    }

    // Assign / update vendor availability for a product in a specific city with price
    @PostMapping("/availability")
    public ResponseEntity<ApiResponse<VendorAvailabilityResponse>> assignVendorToProductCity(
            @Valid @RequestBody VendorAvailabilityRequest request
    ) {
        VendorAvailabilityResponse response = vendorService.assignVendorToProductCity(request);
        return ResponseEntity.ok(ApiResponse.ok("Vendor assigned to product and city successfully", response));
    }

    @DeleteMapping("/availability/{id}")
    public ResponseEntity<ApiResponse<Void>> removeVendorFromProductCity(@PathVariable Long id) {
        vendorService.removeVendorFromProductCity(id);
        return ResponseEntity.ok(ApiResponse.ok("Vendor availability removed successfully", null));
    }

    @GetMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<List<VendorAvailabilityResponse>>> getVendorAvailabilities(@PathVariable Long id) {
        List<VendorAvailabilityResponse> availabilities = vendorService.getVendorAvailabilities(id);
        return ResponseEntity.ok(ApiResponse.ok("Vendor availabilities retrieved successfully", availabilities));
    }
}
