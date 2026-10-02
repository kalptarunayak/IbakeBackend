package com.ibake.controller;

import com.ibake.dto.ApiResponse;
import com.ibake.dto.CityRequest;
import com.ibake.dto.CityResponse;
import com.ibake.dto.CityStatusUpdateRequest;
import com.ibake.service.CityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    // Public endpoint: List of all active cities
    @GetMapping
    public ResponseEntity<ApiResponse<List<CityResponse>>> getActiveCities() {
        List<CityResponse> cities = cityService.getActiveCities();
        return ResponseEntity.ok(ApiResponse.ok("Active cities retrieved successfully", cities));
    }

    // Admin endpoint: List all cities (including inactive)
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<CityResponse>>> getAllCities() {
        List<CityResponse> cities = cityService.getAllCities();
        return ResponseEntity.ok(ApiResponse.ok("All cities retrieved successfully", cities));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CityResponse>> getCityById(@PathVariable Long id) {
        CityResponse city = cityService.getCityById(id);
        return ResponseEntity.ok(ApiResponse.ok(city));
    }

    // SUPER_ADMIN only: Add new city
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CityResponse>> createCity(@Valid @RequestBody CityRequest request) {
        CityResponse city = cityService.createCity(request);
        return new ResponseEntity<>(ApiResponse.created("City created successfully", city), HttpStatus.CREATED);
    }

    // SUPER_ADMIN only: Enable or disable city globally
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<CityResponse>> updateCityStatus(
            @PathVariable Long id,
            @Valid @RequestBody CityStatusUpdateRequest request
    ) {
        CityResponse updated = cityService.updateCityStatus(id, request.getActive());
        String statusText = request.getActive() ? "enabled" : "disabled";
        return ResponseEntity.ok(ApiResponse.ok("City globally " + statusText + " successfully", updated));
    }
}
