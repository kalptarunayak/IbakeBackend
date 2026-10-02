package com.ibake.service;

import com.ibake.dto.VendorAvailabilityRequest;
import com.ibake.dto.VendorAvailabilityResponse;
import com.ibake.dto.VendorRequest;
import com.ibake.dto.VendorResponse;

import java.util.List;

public interface VendorService {
    List<VendorResponse> getAllVendors();
    VendorResponse getVendorById(Long id);
    VendorResponse createVendor(VendorRequest request);
    VendorResponse updateVendor(Long id, VendorRequest request);
    void deleteVendor(Long id);

    VendorAvailabilityResponse assignVendorToProductCity(VendorAvailabilityRequest request);
    void removeVendorFromProductCity(Long id);
    List<VendorAvailabilityResponse> getVendorAvailabilities(Long vendorId);
}
