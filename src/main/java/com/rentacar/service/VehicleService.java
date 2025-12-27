package com.rentacar.service;

import com.rentacar.exception.ResourceNotFoundException;
import com.rentacar.model.Vehicle;
import com.rentacar.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Araç Service
 * Araç ile ilgili iş mantığı
 */
@Service
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Tüm araçları listele
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    /**
     * Mevcut (available) araçları listele
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findByAvailableTrue();
    }

    /**
     * ID ile araç getir
     */
    @Transactional(readOnly = true)
    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Araç", id));
    }

    /**
     * Marka ile araç ara
     */
    @Transactional(readOnly = true)
    public List<Vehicle> searchByBrand(String brand) {
        return vehicleRepository.findByBrandContainingIgnoreCase(brand);
    }

    /**
     * Yeni araç ekle
     */
    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    /**
     * Araç güncelle
     */
    public Vehicle updateVehicle(Long id, Vehicle vehicleDetails) {
        Vehicle vehicle = getVehicleById(id);
        vehicle.setBrand(vehicleDetails.getBrand());
        vehicle.setModel(vehicleDetails.getModel());
        vehicle.setYear(vehicleDetails.getYear());
        vehicle.setImageUrl(vehicleDetails.getImageUrl());
        vehicle.setDescription(vehicleDetails.getDescription());
        vehicle.setAvailable(vehicleDetails.isAvailable());
        return vehicleRepository.save(vehicle);
    }

    /**
     * Araç sil
     */
    public void deleteVehicle(Long id) {
        Vehicle vehicle = getVehicleById(id);
        vehicleRepository.delete(vehicle);
    }
}
