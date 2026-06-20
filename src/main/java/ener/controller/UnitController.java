package ener.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ener.model.Unit;
import ener.service.ResidentService;
import ener.service.UnitService;

// Controller responsável pelo gerenciamento de Unidades (acesso restrito ao Síndico)
@Controller
@RequestMapping("/units")
public class UnitController {

    @Autowired
    private UnitService unitService;

    @Autowired
    private ResidentService residentService;

    // Exibe a lista de todas as unidades cadastradas
    @GetMapping
    public String listUnits(Model model) {
        model.addAttribute("units", unitService.findAllUnits());
        return "unit/list";
    }

    // Exibe o formulário de cadastro de nova unidade
    @GetMapping("/novo")
    public String newUnit(Model model) {
        model.addAttribute("unit", new Unit());
        model.addAttribute("residents", residentService.findAllResidents());
        return "unit/form";
    }

    // Recebe os dados do formulário e salva a nova unidade
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

    // Exibe o formulário de edição de uma unidade existente
    @GetMapping("/editar/{id}")
    public String editUnit(@PathVariable Integer id, Model model) {
        model.addAttribute("unit", unitService.findUnitById(id));
        model.addAttribute("residents", residentService.findAllResidents());
        return "unit/form";
    }

    // Recebe os dados do formulário e atualiza a unidade
    @PostMapping("/atualizar")
    public String updateUnit(@ModelAttribute Unit unit, Model model) {
        unitService.updateUnit(unit);
        return "redirect:/units";
    }

    // Remove uma unidade do sistema
    @GetMapping("/excluir/{id}")
    public String deleteUnit(@PathVariable Integer id) {
        unitService.deleteUnit(id);
        return "redirect:/units";
    }
}