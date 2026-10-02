package com.ibake.service.impl;

import com.ibake.dto.CityRequest;
import com.ibake.dto.CityResponse;
import com.ibake.entity.City;
import com.ibake.exception.BadRequestException;
import com.ibake.exception.ResourceNotFoundException;
import com.ibake.repository.CityRepository;
import com.ibake.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CityResponse> getActiveCities() {
        return cityRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CityResponse> getAllCities() {
        return cityRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CityResponse getCityById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City", "id", id));
        return mapToResponse(city);
    }

    @Override
    @Transactional
    public CityResponse createCity(CityRequest request) {
        if (cityRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new BadRequestException("City already exists: " + request.getName());
        }

        City city = City.builder()
                .name(request.getName().trim())
                .state(request.getState().trim())
                .active(request.isActive())
                .build();

        return mapToResponse(cityRepository.save(city));
    }

    @Override
    @Transactional
    public CityResponse updateCityStatus(Long id, boolean active) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City", "id", id));
        city.setActive(active);
        return mapToResponse(cityRepository.save(city));
    }

    private CityResponse mapToResponse(City city) {
        return CityResponse.builder()
                .id(city.getId())
                .name(city.getName())
                .state(city.getState())
                .active(city.isActive())
                .createdAt(city.getCreatedAt())
                .build();
    }
}
