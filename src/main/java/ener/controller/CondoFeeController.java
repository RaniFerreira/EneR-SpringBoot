package ener.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ener.model.CondoFee;
import ener.service.CondoFeeService;
import ener.service.UnitService;
import jakarta.validation.Valid;

// Controller responsável pelo gerenciamento de Taxas de Condomínio (acesso restrito ao Síndico)
@Controller
@RequestMapping("/units/{unitId}/fees")
public class CondoFeeController {

    @Autowired
    private CondoFeeService condoFeeService;

    @Autowired
    private UnitService unitService;

    // Exibe a lista de taxas aplicadas a uma unidade
    @GetMapping
    public String listFees(@PathVariable Integer unitId, Model model) {
        model.addAttribute("unit", unitService.findUnitById(unitId));
        model.addAttribute("fees", condoFeeService.findFeesByUnitId(unitId));
        return "fee/list";
    }

    // Exibe o formulário para aplicar uma nova taxa na unidade
    @GetMapping("/nova")
    public String newFee(@PathVariable Integer unitId, Model model) {
        CondoFee fee = new CondoFee();
        fee.setUnit(unitService.findUnitById(unitId));
        model.addAttribute("fee", fee);
        model.addAttribute("unit", unitService.findUnitById(unitId));
        return "fee/form";
    }

    // Salva a nova taxa aplicada à unidade
    @PostMapping("/salvar")
    public String saveFee(@PathVariable Integer unitId,
                           @ModelAttribute @Valid CondoFee fee,
                           BindingResult result,
                           Model model) {
        if (result.hasErrors()) {
            fee.setUnit(unitService.findUnitById(unitId));
            model.addAttribute("fee", fee);
            model.addAttribute("unit", unitService.findUnitById(unitId));
            return "fee/form";
        }

        fee.setUnit(unitService.findUnitById(unitId));
        condoFeeService.saveCondoFee(fee);
        return "redirect:/units/" + unitId + "/fees";
    }

    // Exibe o formulário de edição de uma taxa existente
    @GetMapping("/editar/{id}")
    public String editFee(@PathVariable Integer unitId, @PathVariable Integer id, Model model) {
        model.addAttribute("fee", condoFeeService.findCondoFeeById(id));
        model.addAttribute("unit", unitService.findUnitById(unitId));
        return "fee/form";
    }

    // Atualiza os dados de uma taxa existente
    @PostMapping("/atualizar")
    public String updateFee(@PathVariable Integer unitId, @ModelAttribute CondoFee fee, Model model) {
        fee.setUnit(unitService.findUnitById(unitId));
        condoFeeService.updateCondoFee(fee);
        return "redirect:/units/" + unitId + "/fees";
    }

    // Alterna o status da taxa (Ativa/Inativa)
    @GetMapping("/status/{id}")
    public String toggleStatus(@PathVariable Integer unitId, @PathVariable Integer id) {
        condoFeeService.toggleStatus(id);
        return "redirect:/units/" + unitId + "/fees";
    }

    // Remove uma taxa do sistema
    @GetMapping("/excluir/{id}")
    public String deleteFee(@PathVariable Integer unitId, @PathVariable Integer id) {
        condoFeeService.deleteCondoFee(id);
        return "redirect:/units/" + unitId + "/fees";
    }
}