package com.medical.GestionDossierMedical.service;

import com.medical.GestionDossierMedical.entity.DossierMedical;
import com.medical.GestionDossierMedical.entity.User;
import com.medical.GestionDossierMedical.repository.DossierMedicalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DossierMedicalService {

    @Autowired
    private DossierMedicalRepository dossierMedicalRepository;

    public DossierMedical creerDossier(DossierMedical dossier) {
        return dossierMedicalRepository.save(dossier);
    }

    public List<DossierMedical> obtenirDossiersParPatient(User patient) {
        return dossierMedicalRepository.findByPatient(patient);
    }

    public List<DossierMedical> obtenirDossiersParMedecin(User medecin) {
        return dossierMedicalRepository.findByMedecin(medecin);
    }

    public Page<DossierMedical> getDossiers(Pageable pageable) {
        return dossierMedicalRepository.findAll(pageable);
    }
    public Page<DossierMedical> search(String keyword, Pageable pageable) {
        return dossierMedicalRepository.findByDescriptionContainingIgnoreCase(keyword, pageable);
    }

}
