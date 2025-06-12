package com.medical.GestionDossierMedical.service;

import com.medical.GestionDossierMedical.entity.DossierMedical;
import com.medical.GestionDossierMedical.entity.User;
import com.medical.GestionDossierMedical.repository.DossierMedicalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DossierMedicalServiceTest {

    private DossierMedicalRepository dossierRepo;
    private DossierMedicalService dossierService;

    @BeforeEach
    void setUp() {
        dossierRepo = mock(DossierMedicalRepository.class);
        dossierService = new DossierMedicalService();
        // Utilisation de l'injection manuelle ici
        dossierService.getClass().getDeclaredFields()[0].setAccessible(true);
        try {
            dossierService.getClass().getDeclaredFields()[0].set(dossierService, dossierRepo);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCreerDossier() {
        DossierMedical dossier = new DossierMedical();
        when(dossierRepo.save(any())).thenReturn(dossier);

        DossierMedical result = dossierService.creerDossier(dossier);

        assertNotNull(result);
        verify(dossierRepo, times(1)).save(dossier);
    }

    @Test
    void testObtenirDossiersParPatient() {
        User patient = new User();
        List<DossierMedical> dossiers = List.of(new DossierMedical(), new DossierMedical());

        when(dossierRepo.findByPatient(patient)).thenReturn(dossiers);

        List<DossierMedical> result = dossierService.obtenirDossiersParPatient(patient);

        assertEquals(2, result.size());
    }

    @Test
    void testGetDossiersPagines() {
        Page<DossierMedical> page = new PageImpl<>(List.of(new DossierMedical()));
        when(dossierRepo.findAll(any(Pageable.class))).thenReturn(page);

        Page<DossierMedical> result = dossierService.getDossiers(Pageable.ofSize(10));

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testSearchKeyword() {
        Page<DossierMedical> page = new PageImpl<>(List.of(new DossierMedical()));
        when(dossierRepo.findByDescriptionContainingIgnoreCase(eq("test"), any())).thenReturn(page);

        Page<DossierMedical> result = dossierService.search("test", Pageable.ofSize(5));

        assertEquals(1, result.getContent().size());
    }
}
