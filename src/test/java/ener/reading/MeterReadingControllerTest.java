package ener.reading;

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
import ener.controller.MeterReadingController;
import ener.model.MeterReading;
import ener.model.Unit;
import ener.service.MeterReadingService;
import ener.service.UnitService;

@WebMvcTest(MeterReadingController.class)
@Import({TestConfig.class, SecurityConfig.class})
public class MeterReadingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MeterReadingService meterReadingService;

    @Autowired
    private UnitService unitService;

    @AfterEach
    void resetMocks() {
        reset(meterReadingService, unitService);
    }

    private Unit testUnit() {
        Unit unit = new Unit();
        unit.setId(1);
        unit.setType("Casa");
        unit.setNumber("101");
        unit.setReference("Bloco A");
        return unit;
    }

    private List<MeterReading> testReadingList() {
        MeterReading reading = new MeterReading();
        reading.setId(1);
        reading.setReadingDate(LocalDate.of(2026, 6, 1));
        reading.setPreviousReading(100.0);
        reading.setCurrentReading(150.0);
        reading.setConsumption(50.0);
        reading.setUnit(testUnit());
        return List.of(reading);
    }

    @Test
    @DisplayName("GET /units/{unitId}/readings - Listar leituras sem autenticação")
    void testListReadingsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/units/1/readings"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("GET /units/{unitId}/readings - Listar leituras com Síndico autenticado")
    void testListReadingsAuthenticated() throws Exception {
        when(unitService.findUnitById(1)).thenReturn(testUnit());
        when(meterReadingService.findReadingsByUnitId(1)).thenReturn(testReadingList());

        mockMvc.perform(get("/units/1/readings"))
                .andExpect(status().isOk())
                .andExpect(view().name("reading/list"))
                .andExpect(model().attributeExists("unit"))
                .andExpect(model().attributeExists("readings"))
                .andExpect(content().string(containsString("101")));
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("GET /units/{unitId}/readings/nova - Exibe formulário de nova leitura")
    void testNewReadingForm() throws Exception {
        when(unitService.findUnitById(1)).thenReturn(testUnit());
        when(meterReadingService.findReadingsByUnitId(1)).thenReturn(testReadingList());

        mockMvc.perform(get("/units/1/readings/nova"))
                .andExpect(status().isOk())
                .andExpect(view().name("reading/form"))
                .andExpect(model().attributeExists("reading"))
                .andExpect(model().attributeExists("unit"));
    }

    @Test
    @WithMockUser(username = "morador@ener.com", authorities = {"Morador"})
    @DisplayName("GET /units/{unitId}/readings/nova - Acesso negado para Morador")
    void testNewReadingFormForbiddenUser() throws Exception {
        mockMvc.perform(get("/units/1/readings/nova"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("POST /units/{unitId}/readings/salvar - Salva leitura e redireciona")
    void testSaveReading() throws Exception {
        when(unitService.findUnitById(1)).thenReturn(testUnit());

        MeterReading reading = new MeterReading();
        reading.setReadingDate(LocalDate.of(2026, 6, 1));
        reading.setPreviousReading(100.0);
        reading.setCurrentReading(150.0);

        mockMvc.perform(post("/units/1/readings/salvar")
                        .with(csrf())
                        .flashAttr("meterReading", reading))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/units/1/readings"));

        verify(meterReadingService).saveMeterReading(any(MeterReading.class));
    }

    @Test
    @WithMockUser
    @DisplayName("POST /units/{unitId}/readings/salvar - Sem CSRF retorna 403")
    void testSaveReadingWithoutCsrf() throws Exception {
        mockMvc.perform(post("/units/1/readings/salvar"))
                .andExpect(status().isForbidden());

        verify(meterReadingService, never()).saveMeterReading(any(MeterReading.class));
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("GET /units/{unitId}/readings/editar/{id} - Exibe formulário de edição")
    void testEditReadingForm() throws Exception {
        when(unitService.findUnitById(1)).thenReturn(testUnit());
        when(meterReadingService.findMeterReadingById(1)).thenReturn(testReadingList().get(0));

        mockMvc.perform(get("/units/1/readings/editar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("reading/form"))
                .andExpect(model().attributeExists("reading"))
                .andExpect(model().attributeExists("unit"));
    }

    @Test
    @WithMockUser(username = "sindico@ener.com", authorities = {"Sindico"})
    @DisplayName("GET /units/{unitId}/readings/excluir/{id} - Exclui leitura e redireciona")
    void testDeleteReading() throws Exception {
        mockMvc.perform(get("/units/1/readings/excluir/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/units/1/readings"));

        verify(meterReadingService).deleteMeterReading(1);
    }
}