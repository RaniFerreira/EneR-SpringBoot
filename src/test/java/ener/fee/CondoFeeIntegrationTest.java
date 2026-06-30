package ener.fee;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import ener.model.CondoFee;
import ener.model.CondoFee.FeeStatus;
import ener.model.Unit;
import ener.repository.CondoFeeRepository;
import ener.repository.UnitRepository;
import ener.service.CondoFeeService;
import ener.service.UnitService;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CondoFeeIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CondoFeeRepository condoFeeRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private CondoFeeService condoFeeService;

    @Autowired
    private UnitService unitService;

    private Unit savedUnit(String number) {
        Unit unit = new Unit();
        unit.setType("Casa");
        unit.setNumber(number);
        unit.setReference("Bloco A");
        Integer id = unitService.saveUnit(unit);
        return unitRepository.findById(id).orElseThrow();
    }

    @Test
    @WithMockUser(authorities = {"Sindico"})
    @DisplayName("POST /fees/salvar - Taxa é persistida no banco")
    void testSaveFeeIntegration() throws Exception {
        CondoFee fee = new CondoFee();
        fee.setType("Manutenção");
        fee.setAmount(150.0);

        mockMvc.perform(post("/fees/salvar")
                        .with(csrf())
                        .flashAttr("condoFee", fee))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fees"));

        assertTrue(condoFeeRepository.findAll()
                .stream()
                .anyMatch(f -> "Manutenção".equals(f.getType()) && f.getAmount().equals(150.0)));
    }

    @Test
    @WithMockUser(authorities = {"Sindico"})
    @DisplayName("GET /fees - Lista taxas persistidas no banco")
    void testListFeesIntegration() throws Exception {
        CondoFee fee = new CondoFee();
        fee.setType("Limpeza");
        fee.setAmount(80.0);
        condoFeeService.saveCondoFee(fee);

        mockMvc.perform(get("/fees"))
                .andExpect(status().isOk())
                .andExpect(view().name("fee/list"))
                .andExpect(model().attributeExists("fees"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Limpeza")));
    }

    @Test
    @WithMockUser(authorities = {"Sindico"})
    @DisplayName("POST /fees/{id}/vincular - Taxa é vinculada à unidade no banco")
    void testLinkFeeToUnitIntegration() throws Exception {
        CondoFee fee = new CondoFee();
        fee.setType("Segurança");
        fee.setAmount(200.0);
        Integer feeId = condoFeeService.saveCondoFee(fee);

        Unit unit = savedUnit("101");

        mockMvc.perform(post("/fees/" + feeId + "/vincular")
                        .with(csrf())
                        .param("unitId", unit.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fees/" + feeId + "/vincular"));

        Unit updatedUnit = unitRepository.findById(unit.getId()).orElseThrow();
        assertTrue(updatedUnit.getFees().stream().anyMatch(f -> f.getId().equals(feeId)));
    }

    @Test
    @WithMockUser(authorities = {"Sindico"})
    @DisplayName("GET /fees/{id}/desvincular/{unitId} - Remove vínculo da taxa com a unidade")
    void testUnlinkFeeFromUnitIntegration() throws Exception {
        CondoFee fee = new CondoFee();
        fee.setType("Manutenção");
        fee.setAmount(150.0);
        Integer feeId = condoFeeService.saveCondoFee(fee);

        Unit unit = savedUnit("202");
        condoFeeService.linkFeeToUnit(feeId, unit.getId());

        mockMvc.perform(get("/fees/" + feeId + "/desvincular/" + unit.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fees/" + feeId + "/vincular"));

        Unit updatedUnit = unitRepository.findById(unit.getId()).orElseThrow();
        assertFalse(updatedUnit.getFees().stream().anyMatch(f -> f.getId().equals(feeId)));
    }

    @Test
    @WithMockUser(authorities = {"Sindico"})
    @DisplayName("GET /fees/status/{id} - Alterna status da taxa no banco")
    void testToggleStatusIntegration() throws Exception {
        CondoFee fee = new CondoFee();
        fee.setType("Limpeza");
        fee.setAmount(80.0);
        Integer feeId = condoFeeService.saveCondoFee(fee);

        mockMvc.perform(get("/fees/status/" + feeId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fees"));

        CondoFee updated = condoFeeRepository.findById(feeId).orElseThrow();
        assertEquals(FeeStatus.INATIVA, updated.getStatus());
    }
}