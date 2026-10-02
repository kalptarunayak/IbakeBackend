package com.ibake.repository;

import com.ibake.entity.Occasion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OccasionRepository extends JpaRepository<Occasion, Long> {
    Optional<Occasion> findBySlug(String slug);
    boolean existsByNameIgnoreCase(String name);
}
