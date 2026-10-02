package com.ibake.repository;

import com.ibake.entity.Order;
import com.ibake.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("SELECT o FROM Order o " +
           "WHERE (:status IS NULL OR o.status = :status) " +
           "AND (:cityId IS NULL OR o.city.id = :cityId) " +
           "ORDER BY o.createdAt DESC")
    List<Order> findAllFiltered(@Param("status") OrderStatus status, @Param("cityId") Long cityId);
}
