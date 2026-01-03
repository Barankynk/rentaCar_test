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
            Vehicle v1 = new Vehicle("BMW", "M3", 2025);
            v1.setDescription("Bir arabadan daha fazlası");
            v1.setImageUrl("/images/bmwM3.jpg");
            vehicleRepository.save(v1);

            Vehicle v2 = new Vehicle("BMW", "320i", 2023);
            v2.setDescription("Sportif ve lüks sedan");
            v2.setImageUrl("/images/bmwG20.jpg");
            vehicleRepository.save(v2);

            Vehicle v3 = new Vehicle("Porsche", "GT3", 2024);
            v3.setDescription("Her devir bir hikâye");
            v3.setImageUrl("/images/gt3.jpg");
            vehicleRepository.save(v3);

            Vehicle v4 = new Vehicle("BMW", "M4", 2024);
            v4.setDescription("Her bastığında kalp hızlanır.");
            v4.setImageUrl("/images/bmwM4.jpg");
            vehicleRepository.save(v4);

            Vehicle v5 = new Vehicle("Scirocco", "1.4", 2015);
            v5.setDescription("Boşluk mu? Girer");
            v5.setImageUrl("/images/scirocco.jpg");
            vehicleRepository.save(v5);

            Vehicle v6 = new Vehicle("skyline", "gtr34", 1999);
            v6.setDescription("Efsane");
            v6.setImageUrl("/images/gtr34.jpg");
            vehicleRepository.save(v6);

            System.out.println("✅ Örnek araçlar yüklendi: " + vehicleRepository.count() + " araç");
        };
    }
}
