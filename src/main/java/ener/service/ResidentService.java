package ener.service;

import ener.model.Resident;

// Interface que define o contrato da camada de serviço para Moradores
public interface ResidentService {

    // Salva um novo Morador e cria automaticamente o User vinculado
    Integer saveResident(Resident resident);

    // Lista todos os Moradores cadastrados
    java.util.List<Resident> findAllResidents();

    // Busca um Morador pelo id
    Resident findResidentById(Integer id);

    // Atualiza os dados de um Morador existente
    void updateResident(Resident resident);

    // Remove um Morador e seu User vinculado do sistema
    void deleteResident(Integer id);
}