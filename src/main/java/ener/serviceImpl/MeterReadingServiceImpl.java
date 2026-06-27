package ener.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ener.model.MeterReading;
import ener.repository.MeterReadingRepository;
import ener.service.MeterReadingService;

// Implementação da camada de serviço de leituras de medidor
@Service
public class MeterReadingServiceImpl implements MeterReadingService {

    @Autowired
    private MeterReadingRepository meterReadingRepository;

    // Salva uma nova leitura (consumo calculado automaticamente via @PrePersist)
    @Override
    public Integer saveMeterReading(MeterReading meterReading) {
        MeterReading saved = meterReadingRepository.save(meterReading);
        return saved.getId();
    }

    // Lista todas as leituras de uma unidade, ordenadas da mais recente para a mais antiga
    @Override
    public List<MeterReading> findReadingsByUnitId(Integer unitId) {
        return meterReadingRepository.findByUnitIdOrderByReadingDateDesc(unitId);
    }

    // Busca uma leitura pelo id
    @Override
    public MeterReading findMeterReadingById(Integer id) {
        return meterReadingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leitura não encontrada com o id: " + id));
    }

    // Atualiza uma leitura existente (consumo recalculado automaticamente via @PreUpdate)
    @Override
    public void updateMeterReading(MeterReading meterReading) {
        meterReadingRepository.save(meterReading);
    }

    // Remove uma leitura pelo id
    @Override
    public void deleteMeterReading(Integer id) {
        meterReadingRepository.deleteById(id);
    }
}