package ener.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ener.model.CondoFee;
import ener.model.CondoFee.FeeStatus;
import ener.model.Unit;
import ener.repository.CondoFeeRepository;
import ener.repository.UnitRepository;
import ener.service.CondoFeeService;

// Implementação da camada de serviço de taxas de condomínio
@Service
public class CondoFeeServiceImpl implements CondoFeeService {

    @Autowired
    private CondoFeeRepository condoFeeRepository;

    @Autowired
    private UnitRepository unitRepository;

    // Salva uma nova taxa (data de criação e status padrão definidos via @PrePersist)
    @Override
    public Integer saveCondoFee(CondoFee condoFee) {
        CondoFee saved = condoFeeRepository.save(condoFee);
        return saved.getId();
    }

    // Lista todas as taxas cadastradas
    @Override
    public List<CondoFee> findAllFees() {
        return condoFeeRepository.findAll();
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

    // Vincula uma taxa já cadastrada a uma unidade
    @Override
    public void linkFeeToUnit(Integer feeId, Integer unitId) {
        CondoFee fee = findCondoFeeById(feeId);
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada com o id: " + unitId));

        if (unit.getFees() == null) {
            unit.setFees(new java.util.ArrayList<>());
        }
        if (!unit.getFees().contains(fee)) {
            unit.getFees().add(fee);
        }
        unitRepository.save(unit);
    }

    // Remove o vínculo de uma taxa com uma unidade
    @Override
    public void unlinkFeeFromUnit(Integer feeId, Integer unitId) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada com o id: " + unitId));

        if (unit.getFees() != null) {
            unit.getFees().removeIf(f -> f.getId().equals(feeId));
            unitRepository.save(unit);
        }
    }
}