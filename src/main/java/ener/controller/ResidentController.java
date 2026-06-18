package ener.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import ener.model.Resident;
import ener.service.ResidentService;

// Controller responsável pelo gerenciamento de Moradores (acesso restrito ao Síndico)
@Controller
@RequestMapping("/residents")
public class ResidentController {

    @Autowired
    private ResidentService residentService;

    // Exibe a lista de todos os Moradores cadastrados
    @GetMapping
    public String listResidents(Model model) {
        model.addAttribute("residents", residentService.findAllResidents());
        return "resident/list";
    }

    // Exibe o formulário de cadastro de novo Morador
    @GetMapping("/novo")
    public String newResident(Model model) {
        model.addAttribute("resident", new Resident());
        return "resident/form";
    }

    // Recebe os dados do formulário e salva o novo Morador
    @PostMapping("/salvar")
    public String saveResident(@ModelAttribute Resident resident, Model model) {
        residentService.saveResident(resident);
        model.addAttribute("resident", resident);
        // Exibe a senha gerada para o Síndico copiar e repassar ao Morador
        model.addAttribute("generatedPassword", resident.getGeneratedPassword());
        model.addAttribute("msg", "Morador cadastrado com sucesso!");
        return "resident/form";
    }

    // Exibe o formulário de edição de um Morador existente
    @GetMapping("/editar/{id}")
    public String editResident(@PathVariable Integer id, Model model) {
        model.addAttribute("resident", residentService.findResidentById(id));
        return "resident/Form";
    }

    // Recebe os dados do formulário e atualiza o Morador
    @PostMapping("/atualizar")
    public String updateResident(@ModelAttribute Resident resident, Model model) {
        residentService.updateResident(resident);
        model.addAttribute("msg", "Morador atualizado com sucesso!");
        return "redirect:/residents";
    }

    // Remove um Morador do sistema
    @GetMapping("/excluir/{id}")
    public String deleteResident(@PathVariable Integer id) {
        residentService.deleteResident(id);
        return "redirect:/residents";
    }
}