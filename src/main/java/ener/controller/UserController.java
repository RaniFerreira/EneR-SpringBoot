package ener.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Controller responsável pelas páginas de login e acesso negado
@Controller
public class UserController {

    // Exibe a página de login
    @GetMapping("/login")
    public String login() {
        return "login";
    }

        // Exibe a página inicial (index) após login
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Exibe a página de acesso negado
    @GetMapping("/accessDenied")
    public String getAccessDeniedPage() {
        return "accessDenied";
    }
}