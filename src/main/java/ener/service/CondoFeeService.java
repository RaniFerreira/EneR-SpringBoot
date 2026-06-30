package ener.service;

import java.util.List;

import ener.model.CondoFee;

// Interface que define o contrato da camada de serviço para taxas de condomínio
public interface CondoFeeService {

    // Salva uma nova taxa (cadastro independente, sem vínculo com unidade)
    Integer saveCondoFee(CondoFee condoFee);

    // Lista todas as taxas cadastradas
    List<CondoFee> findAllFees();

    // Busca uma taxa pelo id
    CondoFee findCondoFeeById(Integer id);

    // Atualiza uma taxa existente
    void updateCondoFee(CondoFee condoFee);

    // Remove uma taxa
    void deleteCondoFee(Integer id);

    // Altera o status da taxa (Ativa/Inativa)
    void toggleStatus(Integer id);

    // Vincula uma taxa a uma unidade
    void linkFeeToUnit(Integer feeId, Integer unitId);

    // Remove o vínculo de uma taxa com uma unidade
    void unlinkFeeFromUnit(Integer feeId, Integer unitId);
}