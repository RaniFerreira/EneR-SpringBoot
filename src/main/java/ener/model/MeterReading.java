package ener.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

// Entidade que representa a leitura mensal do medidor de energia de uma unidade
@Data
@Entity
@Table(name = "meter_readings")
public class MeterReading {

    // Identificador único da leitura, gerado automaticamente pelo banco
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reading_id")
    private Integer id;

    // Data em que a leitura foi realizada
    @Column(name = "reading_date")
    private LocalDate readingDate;

    // Valor do medidor na leitura anterior (kWh)
    @Column(name = "reading_previous")
    private Double previousReading;

    // Valor do medidor na leitura atual (kWh)
    @Column(name = "reading_current")
    private Double currentReading;

    // Consumo calculado automaticamente (leitura atual - leitura anterior)
    @Column(name = "reading_consumption")
    private Double consumption;

    // Unidade à qual esta leitura pertence
    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    

    // Calcula o consumo automaticamente antes de salvar ou atualizar
    @PrePersist
    @PreUpdate
        public void calculateConsumption() {
        if (currentReading != null && previousReading != null) {
            this.consumption = currentReading - previousReading;
        } else {
            this.consumption = null; // primeira leitura: sem consumo calculado
        }
    }
}