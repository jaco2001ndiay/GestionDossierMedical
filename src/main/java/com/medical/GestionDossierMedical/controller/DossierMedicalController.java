package com.medical.GestionDossierMedical.controller;

import com.medical.GestionDossierMedical.entity.DossierMedical;
import com.medical.GestionDossierMedical.entity.User;
import com.medical.GestionDossierMedical.service.DossierMedicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dossiers")
public class DossierMedicalController {

    @Autowired
    private DossierMedicalService dossierMedicalService;

    @PreAuthorize("hasRole('MEDECIN')")
    @PostMapping
    public ResponseEntity<DossierMedical> creerDossier(@RequestBody DossierMedical dossier) {
        DossierMedical nouveauDossier = dossierMedicalService.creerDossier(dossier);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouveauDossier);
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('MEDECIN','PATIENT')")
    public ResponseEntity<Page<DossierMedical>> list(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size,
            @RequestParam(defaultValue="dateCreation") String sortBy,
            @RequestParam(defaultValue="desc") String sortDir
    ) {
        Sort.Direction dir = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));
        Page<DossierMedical> result = dossierMedicalService.getDossiers(pageable);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/mes-dossiers")
    public ResponseEntity<List<DossierMedical>> obtenirMesDossiers(Authentication authentication) {
        User patient = (User) authentication.getPrincipal();
        List<DossierMedical> dossiers = dossierMedicalService.obtenirDossiersParPatient(patient);
        return ResponseEntity.ok(dossiers);
    }

    @PreAuthorize("hasRole('MEDECIN')")
    @GetMapping("/mes-patients")
    public ResponseEntity<List<DossierMedical>> obtenirDossiersPatients(Authentication authentication) {
        User medecin = (User) authentication.getPrincipal();
        List<DossierMedical> dossiers = dossierMedicalService.obtenirDossiersParMedecin(medecin);
        return ResponseEntity.ok(dossiers);
    }
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('MEDECIN','PATIENT')")
    public ResponseEntity<Page<DossierMedical>> search(
            @RequestParam String q,
            Pageable pageable) {
        return ResponseEntity.ok(dossierMedicalService.search(q, pageable));
    }


}

