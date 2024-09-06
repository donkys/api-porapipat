package com.porapipat.porapipat_api.controller;

import com.porapipat.porapipat_api.dto.auth.request.AuthRequest;
import com.porapipat.porapipat_api.dto.auth.response.AuthResponse;
import com.porapipat.porapipat_api.dto.auth.request.RefreshTokenRequest;
import com.porapipat.porapipat_api.dto.errorhandle.ControllerErrorResponse;
import com.porapipat.porapipat_api.dto.register.request.RegisterRequest;
import com.porapipat.porapipat_api.dto.users.request.CreateUserRequest;
import com.porapipat.porapipat_api.dto.users.response.CreateUserResponse;
import com.porapipat.porapipat_api.dto.users.response.UserResponse;
import com.porapipat.porapipat_api.entity.UsersEntity;
import com.porapipat.porapipat_api.repository.UsersInterfaceRepository;
import com.porapipat.porapipat_api.service.users.UsersService;
import com.porapipat.porapipat_api.service.util.JwtUtil;
import com.porapipat.porapipat_api.service.util.PatternLogControllerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    private final UsersService usersService;

    private final PatternLogControllerService patternLogControllerService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil, UsersService usersService,
                          PatternLogControllerService patternLogControllerService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usersService = usersService;
        this.patternLogControllerService = patternLogControllerService;
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

            String accessToken = jwtUtil.generateAccessToken(authRequest.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(authRequest.getUsername());

            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        if (jwtUtil.validateRefreshToken(refreshToken)) {
            String username = jwtUtil.getUsernameFromRefreshToken(refreshToken);
            String newAccessToken = jwtUtil.generateAccessToken(username);
            return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@Valid @RequestBody CreateUserRequest request,
                                        HttpServletRequest httpRequest) {
        try {
            CreateUserResponse createdUser = usersService.createUser(request);
            return ResponseEntity.ok(createdUser);
        } catch (IllegalArgumentException ex) {
            patternLogControllerService.logError(false, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ControllerErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
        } catch (Exception ex) {
            patternLogControllerService.logError(true, httpRequest, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ControllerErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
        }
    }
}
