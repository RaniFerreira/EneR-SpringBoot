package ener.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ener.model.MeterReading;

// Repositório responsável pelo acesso aos dados de leituras de medidor
public interface MeterReadingRepository extends JpaRepository<MeterReading, Integer> {

    // Busca todas as leituras de uma unidade específica, ordenadas por data
    List<MeterReading> findByUnitIdOrderByReadingDateDesc(Integer unitId);
}