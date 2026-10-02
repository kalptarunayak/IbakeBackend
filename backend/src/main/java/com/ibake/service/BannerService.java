package com.ibake.service;

import com.ibake.dto.BannerRequest;
import com.ibake.dto.BannerResponse;

import java.util.List;

public interface BannerService {
    List<BannerResponse> getActiveBanners(Long cityId, Long occasionId);
    List<BannerResponse> getAllBanners();
    BannerResponse getBannerById(Long id);
    BannerResponse createBanner(BannerRequest request);
    BannerResponse updateBanner(Long id, BannerRequest request);
    void deleteBanner(Long id);
}
