package ener.service;

import ener.model.User;

// Interface que define o contrato da camada de serviço para usuários
public interface IUserService {

    // Salva um novo usuário no sistema, retornando o id gerado
    Integer saveUser(User user);
}