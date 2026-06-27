package ener.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ener.model.MeterReading;
import ener.service.MeterReadingService;
import ener.service.UnitService;
import java.util.List;

// Controller responsável pelo gerenciamento de leituras de medidor por unidade
@Controller
@RequestMapping("/units/{unitId}/readings")
public class MeterReadingController {

    @Autowired
    private MeterReadingService meterReadingService;

    @Autowired
    private UnitService unitService;

    // Exibe a lista de leituras de uma unidade
    @GetMapping
    public String listReadings(@PathVariable Integer unitId, Model model) {
        model.addAttribute("unit", unitService.findUnitById(unitId));
        model.addAttribute("readings", meterReadingService.findReadingsByUnitId(unitId));
        return "reading/list";
    }

    // Exibe o formulário de nova leitura para uma unidade
    @GetMapping("/nova")
    public String newReading(@PathVariable Integer unitId, Model model) {
        MeterReading reading = new MeterReading();
        reading.setUnit(unitService.findUnitById(unitId));

        // Busca a leitura mais recente da unidade e pré-preenche a leitura anterior
        List<MeterReading> readings = meterReadingService.findReadingsByUnitId(unitId);
        if (!readings.isEmpty()) {
            reading.setPreviousReading(readings.get(0).getCurrentReading());
        }

        model.addAttribute("reading", reading);
        model.addAttribute("unit", unitService.findUnitById(unitId));
        return "reading/form";


    }

    // Salva a nova leitura
    @PostMapping("/salvar")
    public String saveReading(@PathVariable Integer unitId,
                              @ModelAttribute MeterReading meterReading,
                              Model model) {
        meterReading.setUnit(unitService.findUnitById(unitId));
        meterReadingService.saveMeterReading(meterReading);
        return "redirect:/units/" + unitId + "/readings";
    }

    // Exibe o formulário de edição de uma leitura existente
    @GetMapping("/editar/{id}")
    public String editReading(@PathVariable Integer unitId,
                              @PathVariable Integer id,
                              Model model) {
        model.addAttribute("reading", meterReadingService.findMeterReadingById(id));
        model.addAttribute("unit", unitService.findUnitById(unitId));
        return "reading/form";
    }

    // Atualiza a leitura
    @PostMapping("/atualizar")
    public String updateReading(@PathVariable Integer unitId,
                                @ModelAttribute MeterReading meterReading,
                                Model model) {
        meterReading.setUnit(unitService.findUnitById(unitId));
        meterReadingService.updateMeterReading(meterReading);
        return "redirect:/units/" + unitId + "/readings";
    }

    // Remove uma leitura
    @GetMapping("/excluir/{id}")
    public String deleteReading(@PathVariable Integer unitId,
                                @PathVariable Integer id) {
        meterReadingService.deleteMeterReading(id);
        return "redirect:/units/" + unitId + "/readings";
    }
}