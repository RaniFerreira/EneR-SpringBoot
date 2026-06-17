package ener.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ener.model.Resident;

// Repositório responsável pelo acesso aos dados do Morador
public interface ResidentRepository extends JpaRepository<Resident, Integer> {

    // Busca o Morador pelo e-mail, usado no cadastro para evitar duplicidade
    Optional<Resident> findResidentByEmail(String email);

    // Busca o Morador pelo CPF, usado no cadastro para evitar duplicidade
    Optional<Resident> findResidentByCpf(String cpf);

    // Busca o Morador pelo User vinculado, usado para carregar os dados do Morador autenticado
    Optional<Resident> findResidentByUserId(Integer userId);
}