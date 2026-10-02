package com.ibake.repository;

import com.ibake.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT DISTINCT p FROM Product p " +
           "JOIN VendorProductCity vpc ON vpc.product = p " +
           "WHERE vpc.city.id = :cityId " +
           "AND vpc.city.active = true " +
           "AND vpc.available = true " +
           "AND vpc.vendor.active = true " +
           "AND p.active = true " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:occasionId IS NULL OR p.occasion.id = :occasionId) " +
           "AND (:isVeg IS NULL OR p.isVeg = :isVeg) " +
           "AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Product> findAvailableProductsInCity(
            @Param("cityId") Long cityId,
            @Param("categoryId") Long categoryId,
            @Param("occasionId") Long occasionId,
            @Param("isVeg") Boolean isVeg,
            @Param("search") String search
    );

    List<Product> findByActiveTrue();
}
