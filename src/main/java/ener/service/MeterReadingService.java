package ener.service;

import java.util.List;

import ener.model.MeterReading;

// Interface que define o contrato da camada de serviço para leituras de medidor
public interface MeterReadingService {

    // Salva uma nova leitura
    Integer saveMeterReading(MeterReading meterReading);

    // Lista todas as leituras de uma unidade específica
    List<MeterReading> findReadingsByUnitId(Integer unitId);

    // Busca uma leitura pelo id
    MeterReading findMeterReadingById(Integer id);

    // Atualiza uma leitura existente
    void updateMeterReading(MeterReading meterReading);

    // Remove uma leitura
    void deleteMeterReading(Integer id);
}