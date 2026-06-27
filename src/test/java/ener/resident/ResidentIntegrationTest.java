package ener.resident;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import ener.model.Resident;
import ener.repository.ResidentRepository;
import ener.service.ResidentService;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ResidentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResidentRepository residentRepository;

    @Autowired
    private ResidentService residentService;

    @Test
    @WithMockUser(authorities = {"Sindico"})
    @DisplayName("POST /residents/salvar - Morador é persistido no banco")
    void testSaveResidentIntegration() throws Exception {
        Resident resident = new Resident();
        resident.setFullName("Ana Costa");
        resident.setCpf("111.222.333-44");
        resident.setEmail("ana@ener.com");

        mockMvc.perform(post("/residents/salvar")
                        .with(csrf())
                        .flashAttr("resident", resident)
                        .param("plainPassword", "senha123"))
                .andExpect(status().isOk())
                .andExpect(view().name("resident/form"))
                .andExpect(model().attributeExists("msg"));

        assertTrue(residentRepository.findAll()
                .stream()
                .anyMatch(r -> "Ana Costa".equals(r.getFullName())));
    }

    @Test
    @WithMockUser(authorities = {"Sindico"})
    @DisplayName("GET /residents - Lista moradores persistidos no banco")
    void testListResidentsIntegration() throws Exception {
        Resident resident = new Resident();
        resident.setFullName("João Pedro");
        resident.setCpf("222.333.444-55");
        resident.setEmail("joao@ener.com");
        residentService.saveResident(resident, "senha123");

        mockMvc.perform(get("/residents"))
                .andExpect(status().isOk())
                .andExpect(view().name("resident/list"))
                .andExpect(model().attributeExists("residents"))
                .andExpect(content().string(containsString("João Pedro")));
    }

    @Test
    @WithMockUser(authorities = {"Sindico"})
    @DisplayName("POST /residents/atualizar - Morador atualizado redireciona para /residents")
    void testUpdateResidentIntegration() throws Exception {
        Resident resident = new Resident();
        resident.setFullName("Carlos Lima");
        resident.setCpf("555.666.777-88");
        resident.setEmail("carlos@ener.com");
        residentService.saveResident(resident, "senha123");

        Resident saved = residentRepository.findAll()
                .stream()
                .filter(r -> "Carlos Lima".equals(r.getFullName()))
                .findFirst()
                .orElseThrow();

        saved.setFullName("Carlos Lima Atualizado");

        mockMvc.perform(post("/residents/atualizar")
                        .with(csrf())
                        .flashAttr("resident", saved)
                        .param("plainPassword", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/residents"));

        assertTrue(residentRepository.findAll()
                .stream()
                .anyMatch(r -> "Carlos Lima Atualizado".equals(r.getFullName())));
    }
}