package com.medical.GestionDossierMedical.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class LogoutRequest {
    private UUID refreshTokenId;

}

