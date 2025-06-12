package com.medical.GestionDossierMedical.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.GestionDossierMedical.dto.*;
import com.medical.GestionDossierMedical.entity.User;
import com.medical.GestionDossierMedical.repository.UserRepository;
import com.medical.GestionDossierMedical.security.JwtUtil;
import com.medical.GestionDossierMedical.service.LogoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private LogoutService logoutService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setupJwtUtil() {
        when(jwtUtil.generateToken(anyString(), anyLong(), anySet()))
                .thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(anyString(), anyLong(), anySet()))
                .thenReturn("refresh-token");
        when(jwtUtil.validateToken(anyString()))
                .thenReturn(true);
        when(jwtUtil.getUsernameFromToken(anyString()))
                .thenReturn("testuser");
        when(jwtUtil.getUserIdFromToken(anyString()))
                .thenReturn(1L);
        when(jwtUtil.getRolesFromToken(anyString()))
                .thenReturn(Set.of("MEDECIN"));
    }

    @Test
    void login_successful() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("testpass");

        Authentication auth = Mockito.mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRoles(Set.of("MEDECIN"));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void register_success() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("newpass");
        request.setRoles(Set.of("PATIENT"));

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Utilisateur enregistré avec succès !"));
    }

    @Test
    void refreshToken_valid() throws Exception {
        String refreshToken = "refresh-token";
        mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"refresh-token\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"));
    }

    @Test
    void refreshToken_invalid() throws Exception {
        when(jwtUtil.validateToken("bad-token")).thenReturn(false);

        mockMvc.perform(post("/api/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"bad-token\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Token de rafraîchissement invalide"));
    }

    @Test
    void logout_success() throws Exception {
        LogoutRequest request = new LogoutRequest();
        request.setRefreshTokenId(UUID.randomUUID());

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
