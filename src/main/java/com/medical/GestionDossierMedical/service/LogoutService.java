package com.medical.GestionDossierMedical.service;

import com.medical.GestionDossierMedical.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LogoutService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public void logout(UUID refreshTokenId) {
        refreshTokenRepository.deleteById(refreshTokenId);
    }
}
