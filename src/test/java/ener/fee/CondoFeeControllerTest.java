package ener.fee;

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
import ener.controller.CondoFeeController;
import ener.model.CondoFee;
import ener.model.CondoFee.FeeStatus;
import ener.model.Unit;
import ener.service.CondoFeeService;
import ener.service.UnitService;

@WebMvcTest(CondoFeeController.class)
@Import({TestConfig.class, SecurityConfig.class})
public class CondoFeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CondoFeeService condoFeeService;

    @Autowired
    private UnitService unitService;

    @AfterEach
    void resetMocks() {
        reset(condoFeeService, unitService);
    }

    private CondoFee testFee() {
        CondoFee fee = new CondoFee();
        fee.setId(1);
        fee.setType("Manutenção");
        fee.setDescription("Taxa de manutenção mensal");
        fee.setAmount(150.0);
        fee.setStatus(FeeStatus.ATIVA);
        return fee;
    }

    private Unit testUnit() {
        Unit unit = new Unit();
        unit.setId(1);
        unit.setType("Casa");
        unit.setNumber("101");
        unit.setReference("Bloco A");
        return unit;
    }

    @Test
    @DisplayName("GET /fees - Listar taxas sem usuário autenticado")
    void testListFeesNotAuthenticated() throws Exception {
        mockMvc.perform(get("/fees"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("GET /fees - Listar taxas com Síndico autenticado")
    void testListFeesAuthenticated() throws Exception {
        when(condoFeeService.findAllFees()).thenReturn(List.of(testFee()));

        mockMvc.perform(get("/fees"))
                .andExpect(status().isOk())
                .andExpect(view().name("fee/list"))
                .andExpect(model().attributeExists("fees"))
                .andExpect(content().string(containsString("Manutenção")));
    }

    @Test
    @WithMockUser(username = "morador@ener.com", authorities = {"Morador"})
    @DisplayName("GET /fees - Acesso negado para Morador")
    void testListFeesForbiddenUser() throws Exception {
        mockMvc.perform(get("/fees"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("GET /fees/novo - Exibe formulário de cadastro de taxa")
    void testNewFeeForm() throws Exception {
        mockMvc.perform(get("/fees/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("fee/form"))
                .andExpect(model().attributeExists("fee"));
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("POST /fees/salvar - Salva taxa válida e redireciona")
    void testSaveValidFee() throws Exception {
        CondoFee fee = new CondoFee();
        fee.setType("Limpeza");
        fee.setAmount(80.0);

        mockMvc.perform(post("/fees/salvar")
                        .with(csrf())
                        .flashAttr("condoFee", fee))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fees"));

        verify(condoFeeService).saveCondoFee(any(CondoFee.class));
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("POST /fees/salvar - Falha de validação retorna ao formulário")
    void testSaveFeeValidationError() throws Exception {
        CondoFee fee = new CondoFee(); // sem type e amount, causa erro de validação

        mockMvc.perform(post("/fees/salvar")
                        .with(csrf())
                        .flashAttr("condoFee", fee))
                .andExpect(status().isOk())
                .andExpect(view().name("fee/form"))
                .andExpect(model().attributeHasErrors("condoFee"));

        verify(condoFeeService, never()).saveCondoFee(any(CondoFee.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /fees/salvar - Sem CSRF retorna 403")
    void testSaveFeeWithoutCsrf() throws Exception {
        mockMvc.perform(post("/fees/salvar"))
                .andExpect(status().isForbidden());

        verify(condoFeeService, never()).saveCondoFee(any(CondoFee.class));
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("GET /fees/editar/{id} - Exibe formulário de edição")
    void testEditFeeForm() throws Exception {
        when(condoFeeService.findCondoFeeById(1)).thenReturn(testFee());

        mockMvc.perform(get("/fees/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("fee/form"))
                .andExpect(model().attributeExists("fee"));
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("GET /fees/status/{id} - Alterna status e redireciona")
    void testToggleStatus() throws Exception {
        mockMvc.perform(get("/fees/status/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fees"));

        verify(condoFeeService).toggleStatus(1);
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("GET /fees/excluir/{id} - Exclui taxa e redireciona")
    void testDeleteFee() throws Exception {
        mockMvc.perform(get("/fees/excluir/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fees"));

        verify(condoFeeService).deleteCondoFee(1);
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("GET /fees/{id}/vincular - Exibe formulário de vínculo com unidades")
    void testLinkForm() throws Exception {
        when(condoFeeService.findCondoFeeById(1)).thenReturn(testFee());
        when(unitService.findAllUnits()).thenReturn(List.of(testUnit()));

        mockMvc.perform(get("/fees/1/vincular"))
                .andExpect(status().isOk())
                .andExpect(view().name("fee/addTaxa"))
                .andExpect(model().attributeExists("fee"))
                .andExpect(model().attributeExists("units"));
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("POST /fees/{id}/vincular - Vincula taxa a uma unidade e redireciona")
    void testLinkToUnit() throws Exception {
        mockMvc.perform(post("/fees/1/vincular")
                        .with(csrf())
                        .param("unitId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fees/1/vincular"));

        verify(condoFeeService).linkFeeToUnit(1, 1);
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("GET /fees/{id}/desvincular/{unitId} - Remove vínculo e redireciona")
    void testUnlinkFromUnit() throws Exception {
        mockMvc.perform(get("/fees/1/desvincular/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fees/1/vincular"));

        verify(condoFeeService).unlinkFeeFromUnit(1, 1);
    }
}