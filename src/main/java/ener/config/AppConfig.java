package ener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

// Classe de configuração geral da aplicação
@Configuration
public class AppConfig {

    // Bean responsável por criptografar e validar senhas (usado no cadastro e no login)
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}