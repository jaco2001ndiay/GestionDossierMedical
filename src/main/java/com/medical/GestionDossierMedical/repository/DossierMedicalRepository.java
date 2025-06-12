package com.medical.GestionDossierMedical.repository;

import com.medical.GestionDossierMedical.entity.DossierMedical;
import com.medical.GestionDossierMedical.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DossierMedicalRepository extends JpaRepository<DossierMedical, Long> {
    List<DossierMedical> findByPatient(User patient);
    List<DossierMedical> findByMedecin(User medecin);
    Page<DossierMedical> findByDescriptionContainingIgnoreCase(String keyword, Pageable pageable);

}
