package ener.unit;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import ener.config.SecurityConfig;
import ener.config.TestConfig;
import ener.controller.UnitController;
import ener.model.Unit;
import ener.service.ResidentService;
import ener.service.UnitService;

@WebMvcTest(UnitController.class)
@Import({TestConfig.class, SecurityConfig.class})
public class UnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UnitService unitService;

    @Autowired
    private ResidentService residentService;

    @AfterEach
    void resetMocks() {
        reset(unitService, residentService);
    }

    private List<Unit> testCreateUnitList() {
        Unit unit = new Unit();
        unit.setId(1);
        unit.setType("Casa");
        unit.setNumber("101");
        unit.setReference("Bloco A");
        return List.of(unit);
    }

    @Test
    @DisplayName("GET /units - Listar unidades sem usuário autenticado")
    void testListUnitsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/units"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("GET /units - Listar unidades com Síndico autenticado")
    void testListUnitsAuthenticated() throws Exception {
        when(unitService.findAllUnits()).thenReturn(testCreateUnitList());

        mockMvc.perform(get("/units"))
                .andExpect(status().isOk())
                .andExpect(view().name("unit/list"))
                .andExpect(model().attributeExists("units"))
                .andExpect(content().string(containsString("101")));
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("GET /units/novo - Exibe formulário de cadastro para Síndico")
    void testNewUnitFormAuthorizedUser() throws Exception {
        when(residentService.findAllResidents()).thenReturn(List.of());

        mockMvc.perform(get("/units/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("unit/form"))
                .andExpect(model().attributeExists("unit"))
                .andExpect(model().attributeExists("residents"));
    }

    @Test
    @WithMockUser(username = "morador@ener.com", authorities = {"Morador"})
    @DisplayName("GET /units/novo - Acesso negado para Morador")
    void testNewUnitFormForbiddenUser() throws Exception {
        mockMvc.perform(get("/units/novo"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("POST /units/salvar - Salva unidade válida e redireciona")
    void testSaveValidUnit() throws Exception {
        Unit unit = new Unit();
        unit.setType("Casa");
        unit.setNumber("101");
        unit.setReference("Bloco A");

        mockMvc.perform(post("/units/salvar")
                        .with(csrf())
                        .flashAttr("unit", unit))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/units"));

        verify(unitService).saveUnit(any(Unit.class));
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("POST /units/salvar - Unidade duplicada retorna formulário com erro")
    void testSaveUnitDuplicated() throws Exception {
        Unit unit = new Unit();
        unit.setType("Casa");
        unit.setNumber("101");
        unit.setReference("Bloco A");

        when(unitService.saveUnit(any(Unit.class)))
                .thenThrow(new RuntimeException("Já existe uma unidade cadastrada com este número e referência."));
        when(residentService.findAllResidents()).thenReturn(List.of());

        mockMvc.perform(post("/units/salvar")
                        .with(csrf())
                        .flashAttr("unit", unit))
                .andExpect(status().isOk())
                .andExpect(view().name("unit/form"))
                .andExpect(model().attributeExists("erro"));
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("GET /units/editar/{id} - Exibe formulário de edição")
    void testEditUnitForm() throws Exception {
        Unit unit = testCreateUnitList().get(0);
        when(unitService.findUnitById(1)).thenReturn(unit);
        when(residentService.findAllResidents()).thenReturn(List.of());

        mockMvc.perform(get("/units/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("unit/form"))
                .andExpect(model().attributeExists("unit"))
                .andExpect(model().attributeExists("residents"));
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("GET /units/excluir/{id} - Exclui unidade e redireciona")
    void testDeleteUnit() throws Exception {
        mockMvc.perform(get("/units/excluir/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/units"));

        verify(unitService).deleteUnit(1);
    }

    @Test
    @WithMockUser
    @DisplayName("POST /units/salvar - Sem CSRF retorna 403")
    void testSaveUnitWithoutCsrf() throws Exception {
        mockMvc.perform(post("/units/salvar"))
                .andExpect(status().isForbidden());

        verify(unitService, never()).saveUnit(any(Unit.class));
    }
}