package ener.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import ener.model.User;
import ener.repository.UserRepository;
import ener.service.IUserService;
import ener.serviceImpl.UserServiceImpl;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserIntegrationTest {

    
    @Autowired
    private IUserService userService;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    @DisplayName("saveUser - Usuário é persistido no banco com senha criptografada")
    void testSaveUserIntegration() {
        User user = new User();
        user.setName("Síndico Teste");
        user.setEmail("sindico@ener.com");
        user.setPassword("senha123");
        user.setRoles(List.of("Sindico"));

        Integer id = userService.saveUser(user);

        assertNotNull(id);
        assertTrue(userRepository.findById(id).isPresent());

        User saved = userRepository.findById(id).get();
        assertEquals("sindico@ener.com", saved.getEmail());
        assertTrue(passwordEncoder.matches("senha123", saved.getPassword()));
    }

    @Test
    @DisplayName("loadUserByUsername - Carrega usuário pelo email com authorities corretas")
    void testLoadUserByUsernameIntegration() {
        User user = new User();
        user.setName("Morador Teste");
        user.setEmail("morador@ener.com");
        user.setPassword("senha123");
        user.setRoles(List.of("Morador"));
        userService.saveUser(user);

        UserDetails userDetails = userServiceImpl.loadUserByUsername("morador@ener.com");

        assertNotNull(userDetails);
        assertEquals("morador@ener.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("Morador")));
    }

    @Test
    @DisplayName("loadUserByUsername - Lança exceção para email não cadastrado")
    void testLoadUserByUsernameNotFound() {
        assertThrows(UsernameNotFoundException.class, () ->
                userServiceImpl.loadUserByUsername("naoexiste@ener.com"));
    }
}