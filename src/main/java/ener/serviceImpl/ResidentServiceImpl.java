package ener.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import ener.model.Resident;
import ener.model.User;
import ener.repository.ResidentRepository;
import ener.service.ResidentService;



// Implementação da camada de serviço de Moradores
@Service
public class ResidentServiceImpl implements ResidentService {

    @Autowired
    private ResidentRepository residentRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Salva o Morador e cria automaticamente o User vinculado com senha gerada
    @Override
    public Integer saveResident(Resident resident) {

        // Gera uma senha aleatória para o acesso do Morador ao sistema
        String rawPassword = UUID.randomUUID().toString().substring(0, 8);

        // Cria o User vinculado ao Morador com o e-mail e a senha gerada
        User user = new User();
        user.setName(resident.getFullName());
        user.setEmail(resident.getEmail());
        user.setPassword(passwordEncoder.encode(rawPassword));

        // Atribui o papel de Morador ao User
        List<String> roles = new ArrayList<>();
        roles.add("Morador");
        user.setRoles(roles);

        // Vincula o User ao Morador e persiste
        resident.setUser(user);

        // Armazena a senha em texto plano temporariamente para exibir ao Síndico
        resident.setGeneratedPassword(rawPassword);

        Resident saved = residentRepository.save(resident);
        return saved.getId();
    }

    // Lista todos os Moradores cadastrados
    @Override
    public List<Resident> findAllResidents() {
        return residentRepository.findAll();
    }

    // Busca um Morador pelo id
    @Override
    public Resident findResidentById(Integer id) {
        return residentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Morador não encontrado com o id: " + id));
    }

    // Atualiza os dados de um Morador existente
    @Override
    public void updateResident(Resident resident) {
        residentRepository.save(resident);
    }

    // Remove o Morador e seu User vinculado do sistema
    @Override
    public void deleteResident(Integer id) {
        residentRepository.deleteById(id);
    }
}