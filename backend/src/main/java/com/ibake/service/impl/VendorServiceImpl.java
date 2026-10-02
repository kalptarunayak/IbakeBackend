package com.ibake.service.impl;

import com.ibake.dto.VendorAvailabilityRequest;
import com.ibake.dto.VendorAvailabilityResponse;
import com.ibake.dto.VendorRequest;
import com.ibake.dto.VendorResponse;
import com.ibake.entity.*;
import com.ibake.exception.BadRequestException;
import com.ibake.exception.ResourceNotFoundException;
import com.ibake.repository.*;
import com.ibake.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;
    private final ProductRepository productRepository;
    private final CityRepository cityRepository;
    private final VendorProductCityRepository vendorProductCityRepository;

    @Override
    @Transactional(readOnly = true)
    public List<VendorResponse> getAllVendors() {
        return vendorRepository.findAll().stream()
                .map(this::mapToVendorResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VendorResponse getVendorById(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));
        return mapToVendorResponse(vendor);
    }

    @Override
    @Transactional
    public VendorResponse createVendor(VendorRequest request) {
        if (vendorRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new BadRequestException("Vendor with name already exists: " + request.getName());
        }

        Vendor vendor = Vendor.builder()
                .name(request.getName().trim())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .address(request.getAddress())
                .active(request.isActive())
                .build();

        return mapToVendorResponse(vendorRepository.save(vendor));
    }

    @Override
    @Transactional
    public VendorResponse updateVendor(Long id, VendorRequest request) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));

        vendor.setName(request.getName().trim());
        vendor.setContactEmail(request.getContactEmail());
        vendor.setContactPhone(request.getContactPhone());
        vendor.setAddress(request.getAddress());
        vendor.setActive(request.isActive());

        return mapToVendorResponse(vendorRepository.save(vendor));
    }

    @Override
    @Transactional
    public void deleteVendor(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));
        vendorRepository.delete(vendor);
    }

    @Override
    @Transactional
    public VendorAvailabilityResponse assignVendorToProductCity(VendorAvailabilityRequest request) {
        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", request.getVendorId()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("City", "id", request.getCityId()));

        VendorProductCity vpc = vendorProductCityRepository
                .findByVendorIdAndProductIdAndCityId(vendor.getId(), product.getId(), city.getId())
                .orElse(VendorProductCity.builder()
                        .vendor(vendor)
                        .product(product)
                        .city(city)
                        .build());

        vpc.setPrice(request.getPrice());
        vpc.setAvailable(request.isAvailable());
        vpc.setStockQuantity(request.getStockQuantity());
        vpc.setPreparationTimeHours(request.getPreparationTimeHours());

        VendorProductCity saved = vendorProductCityRepository.save(vpc);
        return mapToAvailabilityResponse(saved);
    }

    @Override
    @Transactional
    public void removeVendorFromProductCity(Long id) {
        VendorProductCity vpc = vendorProductCityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor product city mapping", "id", id));
        vendorProductCityRepository.delete(vpc);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VendorAvailabilityResponse> getVendorAvailabilities(Long vendorId) {
        vendorRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", vendorId));

        return vendorProductCityRepository.findByVendorId(vendorId).stream()
                .map(this::mapToAvailabilityResponse)
                .toList();
    }

    private VendorResponse mapToVendorResponse(Vendor vendor) {
        return VendorResponse.builder()
                .id(vendor.getId())
                .name(vendor.getName())
                .contactEmail(vendor.getContactEmail())
                .contactPhone(vendor.getContactPhone())
                .address(vendor.getAddress())
                .active(vendor.isActive())
                .createdAt(vendor.getCreatedAt())
                .build();
    }

    private VendorAvailabilityResponse mapToAvailabilityResponse(VendorProductCity vpc) {
        return VendorAvailabilityResponse.builder()
                .id(vpc.getId())
                .vendorId(vpc.getVendor().getId())
                .vendorName(vpc.getVendor().getName())
                .productId(vpc.getProduct().getId())
                .productName(vpc.getProduct().getName())
                .cityId(vpc.getCity().getId())
                .cityName(vpc.getCity().getName())
                .price(vpc.getPrice())
                .available(vpc.isAvailable())
                .stockQuantity(vpc.getStockQuantity())
                .preparationTimeHours(vpc.getPreparationTimeHours())
                .updatedAt(vpc.getUpdatedAt())
                .build();
    }
}
