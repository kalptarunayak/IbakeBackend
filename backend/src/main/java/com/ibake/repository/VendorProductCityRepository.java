package com.ibake.repository;

import com.ibake.entity.VendorProductCity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface VendorProductCityRepository extends JpaRepository<VendorProductCity, Long> {

    Optional<VendorProductCity> findByVendorIdAndProductIdAndCityId(Long vendorId, Long productId, Long cityId);

    @Query("SELECT vpc FROM VendorProductCity vpc " +
           "WHERE vpc.product.id = :productId " +
           "AND vpc.city.id = :cityId " +
           "AND vpc.available = true " +
           "AND vpc.vendor.active = true " +
           "AND vpc.city.active = true " +
           "ORDER BY vpc.price ASC")
    List<VendorProductCity> findActiveVendorsForProductInCity(@Param("productId") Long productId, @Param("cityId") Long cityId);

    @Query("SELECT MIN(vpc.price) FROM VendorProductCity vpc " +
           "WHERE vpc.product.id = :productId " +
           "AND vpc.city.id = :cityId " +
           "AND vpc.available = true " +
           "AND vpc.vendor.active = true " +
           "AND vpc.city.active = true")
    BigDecimal findMinPriceForProductInCity(@Param("productId") Long productId, @Param("cityId") Long cityId);

    List<VendorProductCity> findByVendorId(Long vendorId);

    List<VendorProductCity> findByProductId(Long productId);

    void deleteByVendorIdAndProductIdAndCityId(Long vendorId, Long productId, Long cityId);
}
