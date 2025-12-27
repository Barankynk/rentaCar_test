package com.rentacar.controller;

import com.rentacar.model.AppointmentStatus;
import com.rentacar.service.AppointmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Admin panel controller
 * HTTP Basic Auth ile korumalı
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AppointmentService appointmentService;

    public AdminController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * Admin dashboard - Tüm randevular
     */
    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("appointments", appointmentService.getAllAppointments());
        model.addAttribute("todayAppointments", appointmentService.getTodayAppointments());
        model.addAttribute("pendingCount",
                appointmentService.getAppointmentsByStatus(AppointmentStatus.PENDING).size());
        return "admin/dashboard";
    }

    /**
     * Tüm randevuları listele
     */
    @GetMapping("/appointments")
    public String allAppointments(@RequestParam(required = false) String status, Model model) {
        if (status != null && !status.isBlank()) {
            try {
                AppointmentStatus appointmentStatus = AppointmentStatus.valueOf(status.toUpperCase());
                model.addAttribute("appointments",
                        appointmentService.getAppointmentsByStatus(appointmentStatus));
                model.addAttribute("selectedStatus", status);
            } catch (IllegalArgumentException e) {
                model.addAttribute("appointments", appointmentService.getAllAppointments());
            }
        } else {
            model.addAttribute("appointments", appointmentService.getAllAppointments());
        }
        model.addAttribute("statuses", AppointmentStatus.values());
        return "admin/appointments";
    }

    /**
     * Randevu onayla
     */
    @PostMapping("/appointments/{id}/confirm")
    public String confirmAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        appointmentService.confirmAppointment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Randevu onaylandı.");
        return "redirect:/admin/appointments";
    }

    /**
     * Randevu iptal et
     */
    @PostMapping("/appointments/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        appointmentService.cancelAppointment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Randevu iptal edildi.");
        return "redirect:/admin/appointments";
    }
}
