package ener.model;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

// Entidade que representa os dados pessoais do Morador do condomínio
@Data
@Entity
@Table(name = "residents")
public class Resident {

    // Identificador único do Morador, gerado automaticamente pelo banco
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resident_id")
    private Integer id;

    // Nome completo do Morador
    @Column(name = "resident_full_name")
    private String fullName;

    // CPF do Morador
    @Column(name = "resident_cpf", unique = true)
    private String cpf;

    // Data de nascimento do Morador
    @Column(name = "resident_birth_date")
    private LocalDate birthDate;

    // Cidade de nascimento do Morador
    @Column(name = "resident_birth_city")
    private String birthCity;

    // Profissão do Morador
    @Column(name = "resident_profession")
    private String profession;

    // Telefone do Morador
    @Column(name = "resident_phone")
    private String phone;

    // WhatsApp do Morador
    @Column(name = "resident_whatsapp")
    private String whatsapp;

    // E-mail do Morador — usado para criar o acesso ao sistema (User)
    @Column(name = "resident_email", unique = true)
    private String email;

    // Vínculo com o User do sistema — criado automaticamente no cadastro do Morador
    // CascadeType.ALL garante que o User seja criado/removido junto com o Morador
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private Resident resident;
}