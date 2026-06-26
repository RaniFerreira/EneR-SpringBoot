package ener.config;

import ener.service.ResidentService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public ResidentService residentService() {
        return Mockito.mock(ResidentService.class);
    }
}