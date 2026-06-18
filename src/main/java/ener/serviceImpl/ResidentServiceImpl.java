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

        // saveResident — usa a senha do formulário
    @Override
    public Integer saveResident(Resident resident, String plainPassword) {
        User user = new User();
        user.setName(resident.getFullName());
        user.setEmail(resident.getEmail());
        user.setPassword(passwordEncoder.encode(plainPassword));

        List<String> roles = new ArrayList<>();
        roles.add("Morador");
        user.setRoles(roles);

        resident.setUser(user);

        Resident saved = residentRepository.save(resident);
        return saved.getId();
    }

    // updateResident — atualiza email no User e senha só se preenchida
    @Override
    public void updateResident(Resident resident, String plainPassword) {
        Resident existing = residentRepository.findById(resident.getId())
                .orElseThrow(() -> new RuntimeException("Morador não encontrado"));

        // Mantém o User existente e atualiza email
        User user = existing.getUser();
        user.setName(resident.getFullName());
        user.setEmail(resident.getEmail());

        // Só atualiza a senha se o campo foi preenchido
        if (plainPassword != null && !plainPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(plainPassword));
        }

        resident.setUser(user);
        residentRepository.save(resident);
    }
    // Busca um Morador pelo id
    @Override
    public Resident findResidentById(Integer id) {
        return residentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Morador não encontrado com o id: " + id));
    }

    //Lista todos os moradores
    @Override
    public List<Resident> findAllResidents() {
        return residentRepository.findAll();
    }

   

    // Remove o Morador e seu User vinculado do sistema
    @Override
    public void deleteResident(Integer id) {
        residentRepository.deleteById(id);
    }
}