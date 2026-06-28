package ener.config;

import ener.repository.ResidentRepository;
import ener.service.MeterReadingService;
import ener.service.ResidentService;
import ener.service.UnitService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@TestConfiguration
public class TestConfig {

    @Bean
    public ResidentService residentService() {
        return Mockito.mock(ResidentService.class);
    }

    @Bean
    public UnitService unitService() {
        return Mockito.mock(UnitService.class);
    }

    @Bean
    public MeterReadingService meterReadingService() {
        return Mockito.mock(MeterReadingService.class);
    }

    @Bean
    public ResidentRepository residentRepository() {
        return Mockito.mock(ResidentRepository.class);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return Mockito.mock(UserDetailsService.class);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}