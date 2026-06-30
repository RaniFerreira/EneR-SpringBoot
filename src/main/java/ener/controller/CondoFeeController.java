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
import org.springframework.web.bind.annotation.RequestParam;

import ener.model.CondoFee;
import ener.service.CondoFeeService;
import ener.service.UnitService;
import jakarta.validation.Valid;

// Controller responsável pelo gerenciamento de Taxas de Condomínio (acesso restrito ao Síndico)
// Taxas são cadastradas de forma independente e depois vinculadas a unidades
@Controller
@RequestMapping("/fees")
public class CondoFeeController {

    @Autowired
    private CondoFeeService condoFeeService;

    @Autowired
    private UnitService unitService;

    // Exibe a lista de todas as taxas cadastradas
    @GetMapping
    public String listFees(Model model) {
        model.addAttribute("fees", condoFeeService.findAllFees());
        return "fee/list";
    }

    // Exibe o formulário de cadastro de nova taxa
    @GetMapping("/novo")
    public String newFee(Model model) {
        model.addAttribute("fee", new CondoFee());
        return "fee/form";
    }

    // Salva uma nova taxa (sem vínculo com unidade ainda)
    @PostMapping("/salvar")
    public String saveFee(@ModelAttribute @Valid CondoFee fee, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("fee", fee);
            return "fee/form";
        }
        condoFeeService.saveCondoFee(fee);
        return "redirect:/fees";
    }

    // Exibe o formulário de edição de uma taxa existente
    @GetMapping("/editar/{id}")
    public String editFee(@PathVariable Integer id, Model model) {
        model.addAttribute("fee", condoFeeService.findCondoFeeById(id));
        return "fee/form";
    }

    // Atualiza os dados de uma taxa existente
    @PostMapping("/atualizar")
    public String updateFee(@ModelAttribute CondoFee fee, Model model) {
        condoFeeService.updateCondoFee(fee);
        return "redirect:/fees";
    }

    // Alterna o status da taxa (Ativa/Inativa)
    @GetMapping("/status/{id}")
    public String toggleStatus(@PathVariable Integer id) {
        condoFeeService.toggleStatus(id);
        return "redirect:/fees";
    }

    // Remove uma taxa do sistema
    @GetMapping("/excluir/{id}")
    public String deleteFee(@PathVariable Integer id) {
        condoFeeService.deleteCondoFee(id);
        return "redirect:/fees";
    }

    // Exibe o formulário para vincular uma taxa a unidades
    @GetMapping("/{id}/vincular")
    public String linkForm(@PathVariable Integer id, Model model) {
        model.addAttribute("fee", condoFeeService.findCondoFeeById(id));
        model.addAttribute("units", unitService.findAllUnits());
        return "fee/addTaxa";
    }

    // Vincula a taxa a uma unidade específica
    @PostMapping("/{id}/vincular")
    public String linkToUnit(@PathVariable Integer id, @RequestParam Integer unitId, Model model) {
        condoFeeService.linkFeeToUnit(id, unitId);
        return "redirect:/fees/" + id + "/vincular";
    }

    // Remove o vínculo da taxa com uma unidade
    @GetMapping("/{id}/desvincular/{unitId}")
    public String unlinkFromUnit(@PathVariable Integer id, @PathVariable Integer unitId) {
        condoFeeService.unlinkFeeFromUnit(id, unitId);
        return "redirect:/fees/" + id + "/vincular";
    }
}