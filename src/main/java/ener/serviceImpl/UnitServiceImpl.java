package ener.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ener.model.Unit;
import ener.repository.UnitRepository;
import ener.service.UnitService;

// Implementação da camada de serviço de Unidades
@Service
public class UnitServiceImpl implements UnitService {

    @Autowired
    private UnitRepository unitRepository;

    // Salva uma nova unidade, validando se já existe com o mesmo número e referência
    @Override
    public Integer saveUnit(Unit unit) {
        Optional<Unit> existing = unitRepository.findByNumberAndReference(unit.getNumber(), unit.getReference());

        if (existing.isPresent()) {
            throw new RuntimeException("Já existe uma unidade cadastrada com este número e referência.");
        }

        Unit saved = unitRepository.save(unit);
        return saved.getId();
    }

    // Lista todas as unidades cadastradas
    @Override
    public List<Unit> findAllUnits() {
        return unitRepository.findAll();
    }

    // Busca uma unidade pelo id
    @Override
    public Unit findUnitById(Integer id) {
        return unitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Unidade não encontrada com o id: " + id));
    }

    // Atualiza os dados de uma unidade existente
    @Override
    public void updateUnit(Unit unit) {
        unitRepository.save(unit);
    }

    // Remove uma unidade do sistema
    @Override
    public void deleteUnit(Integer id) {
        unitRepository.deleteById(id);
    }

    // Lista todas as unidades vinculadas a um Morador específico
    @Override
    public List<Unit> findUnitsByResidentId(Integer residentId) {
        return unitRepository.findByResidentsId(residentId);
    }
}