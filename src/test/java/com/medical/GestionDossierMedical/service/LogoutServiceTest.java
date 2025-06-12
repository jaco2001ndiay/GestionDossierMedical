package com.medical.GestionDossierMedical.service;

import com.medical.GestionDossierMedical.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private LogoutService logoutService;

    @Test
    void logout_shouldCallDeleteById() {
        UUID tokenId = UUID.randomUUID();

        logoutService.logout(tokenId);

        verify(refreshTokenRepository, times(1)).deleteById(tokenId);
    }
}
