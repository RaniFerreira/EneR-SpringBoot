package ener.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// Entidade que representa uma taxa de condomínio, cadastrada de forma independente
// e posteriormente vinculada a uma ou mais unidades
@Data
@Entity
@Table(name = "condo_fees")
public class CondoFee {

    // Identificador único da taxa, gerado automaticamente pelo banco
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fee_id")
    private Integer id;

    // Tipo da taxa (Manutenção, Limpeza, etc.)
    @NotBlank(message = "Informe o tipo da taxa")
    @Column(name = "fee_type", nullable = false)
    private String type;

    // Descrição adicional da taxa
    @Column(name = "fee_description")
    private String description;

    // Valor da taxa
    @NotNull(message = "Informe o valor da taxa")
    @Column(name = "fee_amount", nullable = false)
    private Double amount;

    // Data em que a taxa foi criada
    @Column(name = "fee_created_at")
    private LocalDate createdAt;

    // Status da taxa: Ativa ou Inativa
    @Enumerated(EnumType.STRING)
    @Column(name = "fee_status", nullable = false)
    private FeeStatus status;

    // Unidades às quais esta taxa está vinculada
    // Tabela de junção: unit_fees
    @ManyToMany(mappedBy = "fees")
    private List<Unit> units;

    // Define a data de criação automaticamente e o status padrão como Ativa
    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDate.now();
        }
        if (status == null) {
            status = FeeStatus.ATIVA;
        }
    }

    public enum FeeStatus {
        ATIVA, INATIVA
    }
}