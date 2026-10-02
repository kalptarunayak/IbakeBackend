package com.ibake.controller;

import com.ibake.dto.ApiResponse;
import com.ibake.dto.BannerRequest;
import com.ibake.dto.BannerResponse;
import com.ibake.service.BannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    // Public endpoint: Get active banners for a city (and optional occasion) within active date range
    @GetMapping
    public ResponseEntity<ApiResponse<List<BannerResponse>>> getActiveBanners(
            @RequestParam(required = false) Long cityId,
            @RequestParam(required = false) Long occasionId
    ) {
        List<BannerResponse> banners = bannerService.getActiveBanners(cityId, occasionId);
        return ResponseEntity.ok(ApiResponse.ok("Active banners retrieved successfully", banners));
    }

    // ADMIN: View all banners
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<List<BannerResponse>>> getAllBanners() {
        List<BannerResponse> banners = bannerService.getAllBanners();
        return ResponseEntity.ok(ApiResponse.ok("All banners retrieved successfully", banners));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BannerResponse>> getBannerById(@PathVariable Long id) {
        BannerResponse banner = bannerService.getBannerById(id);
        return ResponseEntity.ok(ApiResponse.ok(banner));
    }

    // ADMIN: Create banner
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BannerResponse>> createBanner(@Valid @RequestBody BannerRequest request) {
        BannerResponse banner = bannerService.createBanner(request);
        return new ResponseEntity<>(ApiResponse.created("Banner created successfully", banner), HttpStatus.CREATED);
    }

    // ADMIN: Update banner
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<BannerResponse>> updateBanner(
            @PathVariable Long id,
            @Valid @RequestBody BannerRequest request
    ) {
        BannerResponse banner = bannerService.updateBanner(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Banner updated successfully", banner));
    }

    // ADMIN: Delete banner
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.ok(ApiResponse.ok("Banner deleted successfully", null));
    }
}
