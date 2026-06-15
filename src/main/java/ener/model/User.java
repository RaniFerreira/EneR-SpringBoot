package ener.model;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

//Entidade que representa o usuario do sistema(credenciais de acesso)

@Data //lombok(getters e setters e construtor com os campos obrigatorios)
@Entity // marca a classe como entidade
@Table(name= "users") // nome da tabela no banco


public class User {
    
    // Indentificador único do úsuario, gerado automaticamente pelo banco
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Integer id;

    //Nome do usuario
    @Column(name = "user_name")
    private String name;

    // Senha do usuario (armazenada criptografada com BCrypt)
    @Column(name = "user_passwd")
    private String password;

    // Email do usuario, utilizado como Login
    @Column(name = "user_email")
    private String email;

    // Papaeis do usuario (Sindico/Morador), armazenado na tabela roles
    // FetchType.EAGER garante que os papéis sejam carregados junto com o usuário no login
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "user_role")
    private List<String> roles;


    
}
