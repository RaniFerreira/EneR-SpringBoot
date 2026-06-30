package ener.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
 import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
// Entidade que representa uma unidade (casa, apartamento ou lote) do condomínio
@Data
@Entity
@Table(name = "units")
public class Unit {

    // Identificador único da unidade, gerado automaticamente pelo banco
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id")
    private Integer id;

    // Tipo da unidade (ex: Casa, Apartamento, Lote)
    @Column(name = "unit_type")
    private String type;

    // Número identificador da unidade (ex: 101, 202)
    @Column(name = "unit_number")
    private String number;

    // Referência adicional da unidade (ex: bloco, rua, quadra)
    @Column(name = "unit_reference")
    private String reference;

   

    // Lista de leituras de medidor desta unidade
    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL)
    private List<MeterReading> readings;

    // Lista de Moradores responsáveis pela unidade (pode ter mais de um)
    // Tabela de junção: unit_residents
    @ManyToMany
    @JoinTable(
        name = "unit_residents",
        joinColumns = @JoinColumn(name = "unit_id"),
        inverseJoinColumns = @JoinColumn(name = "resident_id")
    )
    private List<Resident> residents;

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL)
    private List<CondoFee> fees;
}