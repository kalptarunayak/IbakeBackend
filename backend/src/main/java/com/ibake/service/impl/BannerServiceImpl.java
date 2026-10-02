package com.ibake.service.impl;

import com.ibake.dto.BannerRequest;
import com.ibake.dto.BannerResponse;
import com.ibake.entity.Banner;
import com.ibake.entity.City;
import com.ibake.entity.Occasion;
import com.ibake.exception.BadRequestException;
import com.ibake.exception.ResourceNotFoundException;
import com.ibake.repository.BannerRepository;
import com.ibake.repository.CityRepository;
import com.ibake.repository.OccasionRepository;
import com.ibake.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;
    private final CityRepository cityRepository;
    private final OccasionRepository occasionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponse> getActiveBanners(Long cityId, Long occasionId) {
        LocalDateTime now = LocalDateTime.now();
        return bannerRepository.findActiveBannersForCityAndOccasion(cityId, occasionId, now).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponse> getAllBanners() {
        return bannerRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BannerResponse getBannerById(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner", "id", id));
        return mapToResponse(banner);
    }

    @Override
    @Transactional
    public BannerResponse createBanner(BannerRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("Banner end date must be after start date");
        }

        City city = null;
        if (request.getCityId() != null) {
            city = cityRepository.findById(request.getCityId())
                    .orElseThrow(() -> new ResourceNotFoundException("City", "id", request.getCityId()));
        }

        Occasion occasion = null;
        if (request.getOccasionId() != null) {
            occasion = occasionRepository.findById(request.getOccasionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Occasion", "id", request.getOccasionId()));
        }

        Banner banner = Banner.builder()
                .title(request.getTitle().trim())
                .imageUrl(request.getImageUrl().trim())
                .redirectUrl(request.getRedirectUrl())
                .city(city)
                .occasion(occasion)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(request.isActive())
                .build();

        return mapToResponse(bannerRepository.save(banner));
    }

    @Override
    @Transactional
    public BannerResponse updateBanner(Long id, BannerRequest request) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner", "id", id));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("Banner end date must be after start date");
        }

        City city = null;
        if (request.getCityId() != null) {
            city = cityRepository.findById(request.getCityId())
                    .orElseThrow(() -> new ResourceNotFoundException("City", "id", request.getCityId()));
        }

        Occasion occasion = null;
        if (request.getOccasionId() != null) {
            occasion = occasionRepository.findById(request.getOccasionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Occasion", "id", request.getOccasionId()));
        }

        banner.setTitle(request.getTitle().trim());
        banner.setImageUrl(request.getImageUrl().trim());
        banner.setRedirectUrl(request.getRedirectUrl());
        banner.setCity(city);
        banner.setOccasion(occasion);
        banner.setStartDate(request.getStartDate());
        banner.setEndDate(request.getEndDate());
        banner.setActive(request.isActive());

        return mapToResponse(bannerRepository.save(banner));
    }

    @Override
    @Transactional
    public void deleteBanner(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banner", "id", id));
        bannerRepository.delete(banner);
    }

    private BannerResponse mapToResponse(Banner banner) {
        return BannerResponse.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .imageUrl(banner.getImageUrl())
                .redirectUrl(banner.getRedirectUrl())
                .cityId(banner.getCity() != null ? banner.getCity().getId() : null)
                .cityName(banner.getCity() != null ? banner.getCity().getName() : "All Cities")
                .occasionId(banner.getOccasion() != null ? banner.getOccasion().getId() : null)
                .occasionName(banner.getOccasion() != null ? banner.getOccasion().getName() : "General")
                .startDate(banner.getStartDate())
                .endDate(banner.getEndDate())
                .active(banner.isActive())
                .createdAt(banner.getCreatedAt())
                .build();
    }
}
