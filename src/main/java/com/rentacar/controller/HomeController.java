package com.rentacar.controller;

import com.rentacar.service.VehicleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Ana sayfa ve genel sayfalar için controller
 */
@Controller
public class HomeController {

    private final VehicleService vehicleService;

    public HomeController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    /**
     * Ana sayfa
     */
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("vehicles", vehicleService.getAvailableVehicles());
        return "index";
    }
}
