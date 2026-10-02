package com.ibake.repository;

import com.ibake.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    @Query("SELECT b FROM Banner b " +
           "WHERE b.active = true " +
           "AND (b.city IS NULL OR b.city.id = :cityId) " +
           "AND (:occasionId IS NULL OR b.occasion.id = :occasionId) " +
           "AND :now BETWEEN b.startDate AND b.endDate " +
           "ORDER BY b.createdAt DESC")
    List<Banner> findActiveBannersForCityAndOccasion(
            @Param("cityId") Long cityId,
            @Param("occasionId") Long occasionId,
            @Param("now") LocalDateTime now
    );

    List<Banner> findByActiveTrue();
}
