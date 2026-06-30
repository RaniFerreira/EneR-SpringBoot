package ener.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ener.model.CondoFee;

// Repositório responsável pelo acesso aos dados de taxas de condomínio
public interface CondoFeeRepository extends JpaRepository<CondoFee, Integer> {
}