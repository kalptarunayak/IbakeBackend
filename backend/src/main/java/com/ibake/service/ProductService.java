package com.ibake.service;

import com.ibake.dto.ProductRequest;
import com.ibake.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getProducts(Long cityId, Long categoryId, Long occasionId, Boolean isVeg, String search);
    ProductResponse getProductById(Long id, Long cityId);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
}
