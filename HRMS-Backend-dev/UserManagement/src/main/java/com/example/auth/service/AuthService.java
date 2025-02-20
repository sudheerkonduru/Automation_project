package com.example.auth.service;

import com.example.auth.dto.JwtAuthResponse;
import com.example.auth.dto.LoginRequest;
import com.example.auth.security.JwtTokenProvider;
import com.example.auth.model.Employee;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.auth.exception.ResourceNotFoundException;
import com.example.auth.repository.EmployeeRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmployeeRepository employeeRepository;

    public JwtAuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        Employee employee = employeeRepository.findByEmail(loginRequest.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
            
        String token = jwtTokenProvider.generateToken(authentication);
        return new JwtAuthResponse(token, employee);
    }
} 