package ener.service;

import java.util.List;

import ener.model.Unit;

// Interface que define o contrato da camada de serviço para Unidades
public interface UnitService {

    // Salva uma nova unidade
    Integer saveUnit(Unit unit);

    // Lista todas as unidades cadastradas
    List<Unit> findAllUnits();

    // Busca uma unidade pelo id
    Unit findUnitById(Integer id);

    // Atualiza os dados de uma unidade existente
    void updateUnit(Unit unit);

    // Remove uma unidade do sistema
    void deleteUnit(Integer id);

    // Lista todas as unidades vinculadas a um Morador específico
    List<Unit> findUnitsByResidentId(Integer residentId);

    boolean isResidentUnit(Integer unitId, Integer residentId);
}