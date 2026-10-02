package com.ibake.service;

import com.ibake.dto.CityRequest;
import com.ibake.dto.CityResponse;

import java.util.List;

public interface CityService {
    List<CityResponse> getActiveCities();
    List<CityResponse> getAllCities();
    CityResponse getCityById(Long id);
    CityResponse createCity(CityRequest request);
    CityResponse updateCityStatus(Long id, boolean active);
}
