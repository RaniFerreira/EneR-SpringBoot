package ener.service;

import ener.model.Resident;

// Interface que define o contrato da camada de serviço para Moradores
public interface ResidentService {

    // Alterar as assinaturas:
    Integer saveResident(Resident resident, String plainPassword);
    void updateResident(Resident resident, String plainPassword);

    // Lista todos os Moradores cadastrados
    java.util.List<Resident> findAllResidents();
    

    // Busca um Morador pelo id
    Resident findResidentById(Integer id);


}