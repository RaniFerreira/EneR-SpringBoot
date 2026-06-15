package ener.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ener.model.User;

// Repositório responsável pelo acesso aos dados do usuário
public interface UserRepository extends JpaRepository<User, Integer> {

    // Busca o usuário pelo e-mail, usado pelo Spring Security para autenticação no login
    Optional<User> findUserByEmail(String email);
}