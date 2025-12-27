package com.rentacar.controller;

import com.rentacar.model.Vehicle;
import com.rentacar.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Araç işlemleri için controller
 */
@Controller
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * Tüm araçları listele
     */
    @GetMapping
    public String listVehicles(Model model) {
        model.addAttribute("vehicles", vehicleService.getAllVehicles());
        return "vehicles/list";
    }

    /**
     * Araç detay sayfası
     */
    @GetMapping("/{id}")
    public String vehicleDetail(@PathVariable Long id, Model model) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        model.addAttribute("vehicle", vehicle);
        return "vehicles/detail";
    }

    /**
     * Marka ile arama
     */
    @GetMapping("/search")
    public String searchVehicles(@RequestParam(required = false) String brand, Model model) {
        if (brand != null && !brand.isBlank()) {
            model.addAttribute("vehicles", vehicleService.searchByBrand(brand));
            model.addAttribute("searchTerm", brand);
        } else {
            model.addAttribute("vehicles", vehicleService.getAllVehicles());
        }
        return "vehicles/list";
    }
}
