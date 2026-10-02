package com.ibake.service;

import com.ibake.dto.AuthResponse;
import com.ibake.dto.LoginRequest;
import com.ibake.dto.RefreshTokenRequest;
import com.ibake.dto.RegisterRequest;
import com.ibake.entity.Role;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
    AuthResponse registerStaff(RegisterRequest request, Role role);
}
