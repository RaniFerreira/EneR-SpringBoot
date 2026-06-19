package ener.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ener.model.Unit;

// Repositório responsável pelo acesso aos dados da Unidade
public interface UnitRepository extends JpaRepository<Unit, Integer> {

    // Busca uma unidade pelo número e referência, usado para validar duplicidade
    Optional<Unit> findByNumberAndReference(String number, String reference);

    // Busca todas as unidades vinculadas a um Morador específico
    List<Unit> findByResidentsId(Integer residentId);
}