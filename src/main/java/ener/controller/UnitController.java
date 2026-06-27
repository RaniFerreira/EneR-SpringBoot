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

import ener.model.Resident;
import ener.model.Unit;
import ener.repository.ResidentRepository;
import ener.service.ResidentService;
import ener.service.UnitService;

@Controller
@RequestMapping("/units")
public class UnitController {

    @Autowired
    private UnitService unitService;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private ResidentRepository residentRepository;


    // ── Síndico ───────────────────────────────────────────────────

    @GetMapping
    public String listUnits(Model model) {
        model.addAttribute("units", unitService.findAllUnits());
        return "unit/list";
    }

    @GetMapping("/novo")
    public String newUnit(Model model) {
        model.addAttribute("unit", new Unit());
        model.addAttribute("residents", residentService.findAllResidents());
        return "unit/form";
    }

    @PostMapping("/salvar")
    public String saveUnit(@ModelAttribute Unit unit, Model model) {
        try {
            unitService.saveUnit(unit);
            model.addAttribute("msg", "Unidade cadastrada com sucesso!");
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("unit", unit);
            model.addAttribute("residents", residentService.findAllResidents());
            return "unit/form";
        }
        return "redirect:/units";
    }

    @GetMapping("/editar/{id}")
    public String editUnit(@PathVariable Integer id, Model model) {
        model.addAttribute("unit", unitService.findUnitById(id));
        model.addAttribute("residents", residentService.findAllResidents());
        return "unit/form";
    }

    @PostMapping("/atualizar")
    public String updateUnit(@ModelAttribute Unit unit, Model model) {
        unitService.updateUnit(unit);
        return "redirect:/units";
    }

    @GetMapping("/excluir/{id}")
    public String deleteUnit(@PathVariable Integer id) {
        unitService.deleteUnit(id);
        return "redirect:/units";
    }

    // ── Morador ───────────────────────────────────────────────────

    @GetMapping("/myunit")
    public String minhasUnidades(Authentication auth, Model model) {
        Resident resident = residentRepository
                .findResidentByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Morador não encontrado"));

        List<Unit> units = unitService.findUnitsByResidentId(resident.getId());
        model.addAttribute("units", units);
        return "unit/myunit";
    }

   
}