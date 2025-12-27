package com.rentacar.config;

import com.rentacar.model.Vehicle;
import com.rentacar.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Uygulama başlatılırken örnek veri yükle
 * NOT: Test ortamında çalışmaz (@Profile("!test"))
 */
@Configuration
@Profile("!test")
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(VehicleRepository vehicleRepository) {
        return args -> {
            // Örnek araçlar
            Vehicle v1 = new Vehicle("Toyota", "Corolla", 2023);
            v1.setDescription("Ekonomik ve güvenilir sedan");
            v1.setImageUrl("/images/toyota-corolla.jpg");
            vehicleRepository.save(v1);

            Vehicle v2 = new Vehicle("BMW", "320i", 2023);
            v2.setDescription("Sportif ve lüks sedan");
            v2.setImageUrl("/images/bmw-320i.jpg");
            vehicleRepository.save(v2);

            Vehicle v3 = new Vehicle("Mercedes", "C200", 2024);
            v3.setDescription("Premium segment lideri");
            v3.setImageUrl("/images/mercedes-c200.jpg");
            vehicleRepository.save(v3);

            Vehicle v4 = new Vehicle("Volkswagen", "Golf", 2023);
            v4.setDescription("Kompakt sınıfın efsanesi");
            v4.setImageUrl("/images/vw-golf.jpg");
            vehicleRepository.save(v4);

            Vehicle v5 = new Vehicle("Audi", "A3", 2024);
            v5.setDescription("Premium kompakt sedan");
            v5.setImageUrl("/images/audi-a3.jpg");
            vehicleRepository.save(v5);

            System.out.println("✅ Örnek araçlar yüklendi: " + vehicleRepository.count() + " araç");
        };
    }
}
