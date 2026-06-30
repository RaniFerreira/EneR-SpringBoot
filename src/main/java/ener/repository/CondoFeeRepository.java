package ener.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ener.model.CondoFee;

// Repositório responsável pelo acesso aos dados de taxas de condomínio
public interface CondoFeeRepository extends JpaRepository<CondoFee, Integer> {

    // Busca todas as taxas aplicadas a uma unidade específica
    List<CondoFee> findByUnitId(Integer unitId);
}