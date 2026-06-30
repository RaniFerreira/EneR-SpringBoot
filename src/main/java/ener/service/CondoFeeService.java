package ener.service;

import java.util.List;

import ener.model.CondoFee;

// Interface que define o contrato da camada de serviço para taxas de condomínio
public interface CondoFeeService {

    // Salva uma nova taxa
    Integer saveCondoFee(CondoFee condoFee);

    // Lista todas as taxas aplicadas a uma unidade específica
    List<CondoFee> findFeesByUnitId(Integer unitId);

    // Busca uma taxa pelo id
    CondoFee findCondoFeeById(Integer id);

    // Atualiza uma taxa existente
    void updateCondoFee(CondoFee condoFee);

    // Remove uma taxa
    void deleteCondoFee(Integer id);

    // Altera o status da taxa (Ativa/Inativa)
    void toggleStatus(Integer id);
}