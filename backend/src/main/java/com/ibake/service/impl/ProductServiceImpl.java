package com.ibake.service.impl;

import com.ibake.dto.ProductRequest;
import com.ibake.dto.ProductResponse;
import com.ibake.dto.VendorPricingResponse;
import com.ibake.entity.*;
import com.ibake.exception.ResourceNotFoundException;
import com.ibake.repository.*;
import com.ibake.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OccasionRepository occasionRepository;
    private final CityRepository cityRepository;
    private final VendorProductCityRepository vendorProductCityRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts(Long cityId, Long categoryId, Long occasionId, Boolean isVeg, String search) {
        List<Product> products;

        if (cityId != null) {
            // Verify city exists
            cityRepository.findById(cityId)
                    .orElseThrow(() -> new ResourceNotFoundException("City", "id", cityId));

            // Query only products having at least one active vendor in this city
            products = productRepository.findAvailableProductsInCity(cityId, categoryId, occasionId, isVeg, search);
        } else {
            // Catalog listing without city constraint (e.g. initial landing or admin catalog)
            products = productRepository.findByActiveTrue();
        }

        return products.stream()
                .map(product -> mapToResponse(product, cityId))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id, Long cityId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        return mapToResponse(product, cityId);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Occasion occasion = null;
        if (request.getOccasionId() != null) {
            occasion = occasionRepository.findById(request.getOccasionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Occasion", "id", request.getOccasionId()));
        }

        Product product = Product.builder()
                .name(request.getName().trim())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .category(category)
                .occasion(occasion)
                .isVeg(request.isVeg())
                .weightInGrams(request.getWeightInGrams())
                .active(request.isActive())
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponse(savedProduct, null);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Occasion occasion = null;
        if (request.getOccasionId() != null) {
            occasion = occasionRepository.findById(request.getOccasionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Occasion", "id", request.getOccasionId()));
        }

        product.setName(request.getName().trim());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);
        product.setOccasion(occasion);
        product.setVeg(request.isVeg());
        product.setWeightInGrams(request.getWeightInGrams());
        product.setActive(request.isActive());

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct, null);
    }

    private ProductResponse mapToResponse(Product product, Long cityId) {
        BigDecimal startingPrice = null;
        List<VendorPricingResponse> availableVendors = Collections.emptyList();

        if (cityId != null) {
            startingPrice = vendorProductCityRepository.findMinPriceForProductInCity(product.getId(), cityId);
            List<VendorProductCity> vendorOfferings = vendorProductCityRepository.findActiveVendorsForProductInCity(product.getId(), cityId);
            availableVendors = vendorOfferings.stream()
                    .map(vpc -> VendorPricingResponse.builder()
                            .vendorId(vpc.getVendor().getId())
                            .vendorName(vpc.getVendor().getName())
                            .cityId(vpc.getCity().getId())
                            .cityName(vpc.getCity().getName())
                            .price(vpc.getPrice())
                            .available(vpc.isAvailable())
                            .stockQuantity(vpc.getStockQuantity())
                            .preparationTimeHours(vpc.getPreparationTimeHours())
                            .build())
                    .toList();
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .occasionId(product.getOccasion() != null ? product.getOccasion().getId() : null)
                .occasionName(product.getOccasion() != null ? product.getOccasion().getName() : null)
                .isVeg(product.isVeg())
                .weightInGrams(product.getWeightInGrams())
                .active(product.isActive())
                .startingPrice(startingPrice)
                .availableVendors(availableVendors)
                .createdAt(product.getCreatedAt())
                .build();
    }
}
