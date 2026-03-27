package com.smartsure.auth.service;

import com.smartsure.auth.dto.AuthResponse;
import com.smartsure.auth.dto.LoginRequest;
import com.smartsure.auth.dto.RegisterRequest;
import com.smartsure.auth.entity.Role;
import com.smartsure.auth.entity.User;
import com.smartsure.auth.exception.InvalidCredentialsException;
import com.smartsure.auth.exception.UserAlreadyExistsException;
import com.smartsure.auth.repository.UserRepository;
import com.smartsure.auth.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User existingUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Rahul Sharma");
        registerRequest.setEmail("rahul@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setPhone("9876543210");
        registerRequest.setAddress("Bangalore");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("rahul@example.com");
        loginRequest.setPassword("password123");

        existingUser = User.builder()
                .id(1L)
                .name("Rahul Sharma")
                .email("rahul@example.com")
                .password("encodedPassword")
                .role(Role.CUSTOMER)
                .build();
    }

    // ─── REGISTER ───────────────────────────────────────────

    @Test
    void register_Success() {
        when(userRepository.existsByEmail("rahul@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        String result = authService.register(registerRequest);

        assertEquals("User registered successfully", result);
        verify(userRepository, times(1)).save(any(User.class));
        // Verify welcome email was sent after successful registration
        verify(emailService, times(1))
                .sendWelcomeEmail("rahul@example.com", "Rahul Sharma");
    }

    @Test
    void register_ThrowsException_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("rahul@example.com")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> authService.register(registerRequest));

        verify(userRepository, never()).save(any());
        // Email must NOT be sent if registration fails
        verify(emailService, never()).sendWelcomeEmail(anyString(), anyString());
    }

    // ─── LOGIN ───────────────────────────────────────────────

    @Test
    void login_Success() {
        when(userRepository.findByEmail("rahul@example.com"))
                .thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(true);
        when(jwtUtil.generateToken(1L, "rahul@example.com", "CUSTOMER"))
                .thenReturn("mock.jwt.token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());
        assertEquals("CUSTOMER", response.getRole());
    }

    @Test
    void login_ThrowsException_WhenEmailNotFound() {
        when(userRepository.findByEmail("rahul@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(loginRequest));
    }

    @Test
    void login_ThrowsException_WhenPasswordWrong() {
        when(userRepository.findByEmail("rahul@example.com"))
                .thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("password123", "encodedPassword"))
                .thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(loginRequest));
    }
}