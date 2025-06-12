package com.medical.GestionDossierMedical.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medical.GestionDossierMedical.entity.DossierMedical;
import com.medical.GestionDossierMedical.service.DossierMedicalService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DossierMedicalController.class)
class DossierMedicalControllerTest {

    private static final String BASE_URL = "/api/dossiers";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DossierMedicalService dossierMedicalService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "MEDECIN")
    void creerDossier_return201_andLocationHeader() throws Exception {
        DossierMedical dossier = new DossierMedical();
        dossier.setId(1L);

        when(dossierMedicalService.creerDossier(any())).thenReturn(dossier);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dossier)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", BASE_URL + "/" + dossier.getId()));
    }

    @Test
    @WithMockUser(roles = "MEDECIN")
    void list_shouldReturnPagedDossiers() throws Exception {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "dateCreation"));
        DossierMedical dossier = new DossierMedical();
        dossier.setId(1L);
        Page<DossierMedical> page = new PageImpl<>(List.of(dossier), pageable, 1);

        when(dossierMedicalService.getDossiers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "dateCreation")
                        .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "MEDECIN")
    void search_shouldReturnResults() throws Exception {
        DossierMedical dossier = new DossierMedical();
        dossier.setId(42L);
        Page<DossierMedical> page = new PageImpl<>(List.of(dossier));

        when(dossierMedicalService.search(eq("abc"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get(BASE_URL + "/search")
                        .param("q", "abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(42));
    }

}
