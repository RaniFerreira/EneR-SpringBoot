package ener.unit;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import ener.model.Unit;
import ener.repository.UnitRepository;
import ener.service.UnitService;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UnitIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private UnitService unitService;

    @Test
    @WithMockUser(authorities = {"Sindico"})
    @DisplayName("POST /units/salvar - Unidade é persistida no banco")
    void testSaveUnitIntegration() throws Exception {
        Unit unit = new Unit();
        unit.setType("Casa");
        unit.setNumber("101");
        unit.setReference("Bloco A");

        mockMvc.perform(post("/units/salvar")
                        .with(csrf())
                        .flashAttr("unit", unit))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/units"));

        assertTrue(unitRepository.findAll()
                .stream()
                .anyMatch(u -> "101".equals(u.getNumber()) && "Bloco A".equals(u.getReference())));
    }

    @Test
    @WithMockUser(authorities = {"Sindico"})
    @DisplayName("GET /units - Lista unidades persistidas no banco")
    void testListUnitsIntegration() throws Exception {
        Unit unit = new Unit();
        unit.setType("Lote");
        unit.setNumber("202");
        unit.setReference("Quadra B");
        unitService.saveUnit(unit);

        mockMvc.perform(get("/units"))
                .andExpect(status().isOk())
                .andExpect(view().name("unit/list"))
                .andExpect(model().attributeExists("units"))
                .andExpect(content().string(containsString("202")));
    }

    @Test
    @WithMockUser(authorities = {"Sindico"})
    @DisplayName("POST /units/salvar - Unidade duplicada retorna formulário com erro")
    void testSaveDuplicatedUnitIntegration() throws Exception {
        Unit unit = new Unit();
        unit.setType("Casa");
        unit.setNumber("303");
        unit.setReference("Bloco C");
        unitService.saveUnit(unit);

        Unit duplicate = new Unit();
        duplicate.setType("Casa");
        duplicate.setNumber("303");
        duplicate.setReference("Bloco C");

        mockMvc.perform(post("/units/salvar")
                        .with(csrf())
                        .flashAttr("unit", duplicate))
                .andExpect(status().isOk())
                .andExpect(view().name("unit/form"))
                .andExpect(model().attributeExists("erro"));
    }

    @Test
    @WithMockUser(authorities = {"Sindico"})
    @DisplayName("GET /units/excluir/{id} - Unidade é removida do banco")
    void testDeleteUnitIntegration() throws Exception {
        Unit unit = new Unit();
        unit.setType("Casa");
        unit.setNumber("404");
        unit.setReference("Bloco D");
        Integer id = unitService.saveUnit(unit);

        mockMvc.perform(get("/units/excluir/" + id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/units"));

        assertFalse(unitRepository.findById(id).isPresent());
    }
}