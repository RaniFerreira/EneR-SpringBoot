package ener.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ener.model.CondoFee;
import ener.model.CondoFee.FeeStatus;
import ener.repository.CondoFeeRepository;
import ener.service.CondoFeeService;

// Implementação da camada de serviço de taxas de condomínio
@Service
public class CondoFeeServiceImpl implements CondoFeeService {

    @Autowired
    private CondoFeeRepository condoFeeRepository;

    // Salva uma nova taxa (data de criação e status padrão definidos via @PrePersist)
    @Override
    public Integer saveCondoFee(CondoFee condoFee) {
        CondoFee saved = condoFeeRepository.save(condoFee);
        return saved.getId();
    }

    // Lista todas as taxas de uma unidade específica
    @Override
    public List<CondoFee> findFeesByUnitId(Integer unitId) {
        return condoFeeRepository.findByUnitId(unitId);
    }

    // Busca uma taxa pelo id
    @Override
    public CondoFee findCondoFeeById(Integer id) {
        return condoFeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Taxa não encontrada com o id: " + id));
    }

    // Atualiza uma taxa existente
    @Override
    public void updateCondoFee(CondoFee condoFee) {
        condoFeeRepository.save(condoFee);
    }

    // Remove uma taxa pelo id
    @Override
    public void deleteCondoFee(Integer id) {
        condoFeeRepository.deleteById(id);
    }

    // Alterna o status da taxa entre Ativa e Inativa
    @Override
    public void toggleStatus(Integer id) {
        CondoFee fee = findCondoFeeById(id);
        fee.setStatus(fee.getStatus() == FeeStatus.ATIVA ? FeeStatus.INATIVA : FeeStatus.ATIVA);
        condoFeeRepository.save(fee);
    }
}