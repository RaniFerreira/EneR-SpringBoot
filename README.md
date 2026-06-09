<div align="center">

# ⚡ EneR

### Sistema web para controle de gasto de energia e gestão de condomínio rural

![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-yellow?style=for-the-badge)

</div>

---

## 📋 Sobre o Projeto

O **EneR** é um sistema web desenvolvido em **Spring Boot** para gerenciamento de condomínios rurais, com **foco principal no controle do gasto de energia elétrica**. O sistema registra e acompanha o consumo de energia de cada casa/lote, e complementa a gestão com a **cobrança da taxa de condomínio** e o **controle de moradores e suas unidades** (casas e lotes), centralizando toda a administração do condomínio em um só lugar.

### 🎯 Objetivos

- **Controlar o gasto de energia elétrica** de cada casa/lote do condomínio (foco principal do sistema)
- Registrar **leituras de medidores** e acompanhar o histórico de consumo por unidade
- Gerenciar a **cobrança da taxa de condomínio** dos moradores
- Manter o **cadastro de moradores** e o vínculo com suas casas e lote
- Controlar **acesso por perfis** (administrador/síndico e morador)

---

## ✨ Funcionalidades

| Módulo | Descrição |
|--------|-----------|
| ⚡ **Controle de Energia** | Registro de leituras dos medidores e acompanhamento do gasto de energia por casa/lote *(módulo principal)* |
| 💰 **Taxa de Condomínio** | Geração e controle da cobrança da taxa condominial dos moradores |
| 👥 **Moradores** | Cadastro, edição e listagem de moradores vinculados às unidades |
| 🏡 **Casas e Lotes** | Gerenciamento das casas e lotes que compõem o condomínio |
| 🔐 **Autenticação** | Login com Spring Security e controle de acesso por perfil (síndico/morador) |

---

## 🛠️ Tecnologias Utilizadas

- **Java 17+**
- **Spring Boot 3.x** — Spring Web, Spring Data JPA, Spring Security
- **Thymeleaf** — renderização das páginas web
- **MySQL** — banco de dados relacional
- **BCrypt** — criptografia de senhas
- **Maven** — gerenciamento de dependências

---

## 📁 Estrutura do Projeto

```
src/main/java/com/ener/
├── config/          # Configurações (Spring Security, etc.)
├── controller/      # Controllers web e REST
├── model/           # Entidades JPA
├── repository/      # Interfaces Spring Data JPA
├── service/         # Regras de negócio
└── EnerApplication.java
```

---

## 🗺️ Roadmap

- [ ] Cadastro de moradores, casas e lotes
- [ ] Registro de leituras e controle do gasto de energia
- [ ] Histórico e comparativo de consumo por unidade
- [ ] Cobrança da taxa de condomínio


---

## 👩‍💻 Autora

Desenvolvido por **Ranielly** — estudante de Análise e Desenvolvimento de Sistemas (IFTM Campus Patrocínio)

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.
