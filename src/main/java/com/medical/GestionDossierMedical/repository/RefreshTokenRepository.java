package com.medical.GestionDossierMedical.repository;

import com.medical.GestionDossierMedical.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findById(UUID id);
    void deleteById(UUID id);
}
