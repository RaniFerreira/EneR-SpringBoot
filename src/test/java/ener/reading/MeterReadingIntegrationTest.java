package ener.reading;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import ener.model.MeterReading;
import ener.model.Unit;
import ener.repository.MeterReadingRepository;
import ener.service.MeterReadingService;
import ener.service.UnitService;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class MeterReadingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MeterReadingRepository meterReadingRepository;

    @Autowired
    private MeterReadingService meterReadingService;

    @Autowired
    private UnitService unitService;

    private Unit savedUnit() {
        Unit unit = new Unit();
        unit.setType("Casa");
        unit.setNumber("101");
        unit.setReference("Bloco A");
        unitService.saveUnit(unit);
        return unitService.findAllUnits().stream()
                .filter(u -> "101".equals(u.getNumber()))
                .findFirst()
                .orElseThrow();
    }

    @Test
    @WithMockUser(authorities = {"Sindico"})
    @DisplayName("POST /units/{unitId}/readings/salvar - Leitura é persistida no banco")
    void testSaveReadingIntegration() throws Exception {
        Unit unit = savedUnit();

        MeterReading reading = new MeterReading();
        reading.setReadingDate(LocalDate.of(2026, 6, 1));
        reading.setPreviousReading(100.0);
        reading.setCurrentReading(150.0);

        mockMvc.perform(post("/units/" + unit.getId() + "/readings/salvar")
                        .with(csrf())
                        .flashAttr("meterReading", reading))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/units/" + unit.getId() + "/readings"));

        assertTrue(meterReadingRepository.findAll()
                .stream()
                .anyMatch(r -> r.getCurrentReading().equals(150.0)));
    }

    @Test
    @WithMockUser(authorities = {"Sindico"})
    @DisplayName("GET /units/{unitId}/readings - Lista leituras persistidas no banco")
    void testListReadingsIntegration() throws Exception {
        Unit unit = savedUnit();

        MeterReading reading = new MeterReading();
        reading.setReadingDate(LocalDate.of(2026, 6, 1));
        reading.setPreviousReading(100.0);
        reading.setCurrentReading(150.0);
        reading.setUnit(unit);
        meterReadingService.saveMeterReading(reading);

        mockMvc.perform(get("/units/" + unit.getId() + "/readings"))
                .andExpect(status().isOk())
                .andExpect(view().name("reading/list"))
                .andExpect(model().attributeExists("readings"))
                .andExpect(model().attributeExists("unit"));
    }

    @Test
    @WithMockUser(authorities = {"Sindico"})
    @DisplayName("GET /units/{unitId}/readings/excluir/{id} - Leitura é removida do banco")
    void testDeleteReadingIntegration() throws Exception {
        Unit unit = savedUnit();

        MeterReading reading = new MeterReading();
        reading.setReadingDate(LocalDate.of(2026, 6, 1));
        reading.setPreviousReading(100.0);
        reading.setCurrentReading(150.0);
        reading.setUnit(unit);
        Integer id = meterReadingService.saveMeterReading(reading);

        mockMvc.perform(get("/units/" + unit.getId() + "/readings/excluir/" + id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/units/" + unit.getId() + "/readings"));

        assertFalse(meterReadingRepository.findById(id).isPresent());
    }
}