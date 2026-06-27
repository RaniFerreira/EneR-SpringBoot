package ener.resident;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
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
import ener.controller.ResidentController;
import ener.model.Resident;
import ener.service.ResidentService;

@WebMvcTest(ResidentController.class)
@Import({TestConfig.class, SecurityConfig.class})
public class ResidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ResidentService residentService;

    @AfterEach
    void resetMocks() {
        reset(residentService);
    }

    private List<Resident> testCreateResidentList() {
        Resident resident = new Resident();
        resident.setId(1);
        resident.setFullName("Maria Silva");
        resident.setCpf("123.456.789-00");
        resident.setEmail("maria@email.com");
        resident.setBirthDate(LocalDate.of(1990, 5, 20));
        resident.setPhone("34999999999");
        return List.of(resident);
    }

    @Test
    @DisplayName("GET /residents - Listar moradores sem usuário autenticado")
    void testListResidentsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/residents"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("GET /residents - Listar moradores com Síndico autenticado")
    void testListResidentsAuthenticated() throws Exception {
        when(residentService.findAllResidents()).thenReturn(testCreateResidentList());

        mockMvc.perform(get("/residents"))
                .andExpect(status().isOk())
                .andExpect(view().name("resident/list"))
                .andExpect(model().attributeExists("residents"))
                .andExpect(content().string(containsString("Maria Silva")));
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("GET /residents/novo - Exibe formulário para Síndico")
    void testNewResidentFormAuthorizedUser() throws Exception {
        mockMvc.perform(get("/residents/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("resident/form"))
                .andExpect(model().attributeExists("resident"));
    }

    @Test
    @WithMockUser(username = "morador@ener.com", authorities = {"Morador"})
    @DisplayName("GET /residents/novo - Acesso negado para Morador")
    void testNewResidentFormForbiddenUser() throws Exception {
        mockMvc.perform(get("/residents/novo"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("POST /residents/salvar - Salva morador válido com sucesso")
    void testSaveValidResident() throws Exception {
        Resident resident = new Resident();
        resident.setFullName("João Souza");
        resident.setCpf("987.654.321-00");
        resident.setEmail("joao@email.com");

        mockMvc.perform(post("/residents/salvar")
                        .with(csrf())
                        .flashAttr("resident", resident)
                        .param("plainPassword", "senha123"))
                .andExpect(status().isOk())
                .andExpect(view().name("resident/form"))
                .andExpect(model().attributeExists("msg"));

        verify(residentService).saveResident(any(Resident.class), anyString());
    }

    @Test
    @WithMockUser
    @DisplayName("POST /residents/salvar - Sem CSRF retorna 403")
    void testSaveResidentWithoutCsrf() throws Exception {
        mockMvc.perform(post("/residents/salvar")
                        .param("plainPassword", "senha123"))
                .andExpect(status().isForbidden());

        verify(residentService, never()).saveResident(any(Resident.class), anyString());
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("GET /residents/editar/{id} - Exibe formulário de edição")
    void testEditResidentForm() throws Exception {
        Resident resident = testCreateResidentList().get(0);
        when(residentService.findResidentById(1)).thenReturn(resident);

        mockMvc.perform(get("/residents/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("resident/form"))
                .andExpect(model().attributeExists("resident"));
    }
}