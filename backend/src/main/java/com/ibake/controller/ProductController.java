package com.ibake.controller;

import com.ibake.dto.ApiResponse;
import com.ibake.dto.ProductRequest;
import com.ibake.dto.ProductResponse;
import com.ibake.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Public catalog: filterable by city, category, occasion, isVeg, and search keyword
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts(
            @RequestParam(required = false) Long cityId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long occasionId,
            @RequestParam(required = false) Boolean isVeg,
            @RequestParam(required = false) String search
    ) {
        List<ProductResponse> products = productService.getProducts(cityId, categoryId, occasionId, isVeg, search);
        return ResponseEntity.ok(ApiResponse.ok("Products retrieved successfully", products));
    }

    // Public product detail
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @PathVariable Long id,
            @RequestParam(required = false) Long cityId
    ) {
        ProductResponse product = productService.getProductById(id, cityId);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }

    // ADMIN / SUPER_ADMIN create product
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse product = productService.createProduct(request);
        return new ResponseEntity<>(ApiResponse.created("Product created successfully", product), HttpStatus.CREATED);
    }

    // ADMIN / SUPER_ADMIN update product
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request
    ) {
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Product updated successfully", product));
    }
}
