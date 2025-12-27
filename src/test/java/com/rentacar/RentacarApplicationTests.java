package com.rentacar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Ana Uygulama Testi
 * Spring Boot context'in başarıyla yüklendiğini doğrular
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Application Context Tests")
class RentacarApplicationTests {

    @Test
    @DisplayName("Spring Boot context yüklenmeli")
    void contextLoads() {
        // Context başarıyla yüklenirse test geçer
        assertTrue(true, "Application context loaded successfully");
    }
}
