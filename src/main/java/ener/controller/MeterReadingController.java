package ener.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ener.model.MeterReading;
import ener.model.Resident;
import ener.repository.ResidentRepository;
import ener.service.MeterReadingService;
import ener.service.UnitService;

@Controller
@RequestMapping("/units/{unitId}/readings")
public class MeterReadingController {

    @Autowired
    private MeterReadingService meterReadingService;

    @Autowired
    private UnitService unitService;

    @Autowired
    private ResidentRepository residentRepository;

    // ── Síndico ───────────────────────────────────────────────────

    @GetMapping
    public String listReadings(@PathVariable Integer unitId, Model model) {
        model.addAttribute("unit", unitService.findUnitById(unitId));
        model.addAttribute("readings", meterReadingService.findReadingsByUnitId(unitId));
        return "reading/list";
    }

    @GetMapping("/nova")
    public String newReading(@PathVariable Integer unitId, Model model) {
        MeterReading reading = new MeterReading();
        reading.setUnit(unitService.findUnitById(unitId));

        List<MeterReading> readings = meterReadingService.findReadingsByUnitId(unitId);
        if (!readings.isEmpty()) {
            reading.setPreviousReading(readings.get(0).getCurrentReading());
        }

        model.addAttribute("reading", reading);
        model.addAttribute("unit", unitService.findUnitById(unitId));
        return "reading/form";
    }

    @PostMapping("/salvar")
    public String saveReading(@PathVariable Integer unitId,
                              @ModelAttribute MeterReading meterReading,
                              Model model) {
        meterReading.setUnit(unitService.findUnitById(unitId));
        meterReadingService.saveMeterReading(meterReading);
        return "redirect:/units/" + unitId + "/readings";
    }

    @GetMapping("/editar/{id}")
    public String editReading(@PathVariable Integer unitId,
                              @PathVariable Integer id,
                              Model model) {
        model.addAttribute("reading", meterReadingService.findMeterReadingById(id));
        model.addAttribute("unit", unitService.findUnitById(unitId));
        return "reading/form";
    }

    @PostMapping("/atualizar")
    public String updateReading(@PathVariable Integer unitId,
                                @ModelAttribute MeterReading meterReading,
                                Model model) {
        meterReading.setUnit(unitService.findUnitById(unitId));
        meterReadingService.updateMeterReading(meterReading);
        return "redirect:/units/" + unitId + "/readings";
    }

    @GetMapping("/excluir/{id}")
    public String deleteReading(@PathVariable Integer unitId,
                                @PathVariable Integer id) {
        meterReadingService.deleteMeterReading(id);
        return "redirect:/units/" + unitId + "/readings";
    }

    // ── Morador ───────────────────────────────────────────────────

    @GetMapping("/myreading")
    public String minhasLeituras(@PathVariable Integer unitId,
                                Authentication auth,
                                Model model) {
        Resident resident = residentRepository
                .findResidentByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Morador não encontrado"));

        if (!unitService.isResidentUnit(unitId, resident.getId())) {
            return "redirect:/accessDenied";
        }

        model.addAttribute("unit", unitService.findUnitById(unitId));
        model.addAttribute("readings", meterReadingService.findReadingsByUnitId(unitId));
        return "reading/myreading";
    }
}