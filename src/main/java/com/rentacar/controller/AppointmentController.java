package com.rentacar.controller;

import com.rentacar.dto.AppointmentForm;
import com.rentacar.exception.AppointmentConflictException;
import com.rentacar.exception.InvalidTimeException;
import com.rentacar.exception.PastDateException;
import com.rentacar.model.Appointment;
import com.rentacar.model.Vehicle;
import com.rentacar.service.AppointmentService;
import com.rentacar.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Randevu işlemleri için controller
 */
@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final VehicleService vehicleService;

    public AppointmentController(AppointmentService appointmentService, VehicleService vehicleService) {
        this.appointmentService = appointmentService;
        this.vehicleService = vehicleService;
    }

    /**
     * Randevu oluşturma formu
     */
    @GetMapping("/new")
    public String showAppointmentForm(@RequestParam(required = false) Long vehicleId, Model model) {
        AppointmentForm form = new AppointmentForm();
        if (vehicleId != null) {
            form.setVehicleId(vehicleId);
        }

        model.addAttribute("appointmentForm", form);
        model.addAttribute("vehicles", vehicleService.getAvailableVehicles());
        model.addAttribute("availableTimes", getAvailableTimeSlots());
        return "appointments/form";
    }

    /**
     * Randevu oluştur (POST)
     */
    @PostMapping
    public String createAppointment(@Valid @ModelAttribute("appointmentForm") AppointmentForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validation hataları varsa formu tekrar göster
        if (bindingResult.hasErrors()) {
            model.addAttribute("vehicles", vehicleService.getAvailableVehicles());
            model.addAttribute("availableTimes", getAvailableTimeSlots());
            return "appointments/form";
        }

        try {
            // Randevu oluştur
            Appointment appointment = appointmentService.createAppointment(
                    form.getVehicleId(),
                    form.getCustomerName(),
                    form.getCustomerPhone(),
                    form.getCustomerEmail(),
                    form.getAppointmentDate(),
                    form.getAppointmentTime());

            redirectAttributes.addFlashAttribute("successMessage",
                    "Randevunuz başarıyla oluşturuldu! Randevu No: " + appointment.getId());
            return "redirect:/appointments/success";

        } catch (PastDateException e) {
            bindingResult.rejectValue("appointmentDate", "error.pastDate", e.getMessage());
        } catch (InvalidTimeException e) {
            bindingResult.rejectValue("appointmentTime", "error.invalidTime", e.getMessage());
        } catch (AppointmentConflictException e) {
            bindingResult.rejectValue("appointmentTime", "error.conflict", e.getMessage());
        }

        model.addAttribute("vehicles", vehicleService.getAvailableVehicles());
        model.addAttribute("availableTimes", getAvailableTimeSlots());
        return "appointments/form";
    }

    /**
     * Başarılı randevu sayfası
     */
    @GetMapping("/success")
    public String appointmentSuccess() {
        return "appointments/success";
    }

    /**
     * Randevularımı görüntüle (telefon ile arama)
     */
    @GetMapping("/my")
    public String myAppointments(@RequestParam(required = false) String phone, Model model) {
        if (phone != null && !phone.isBlank()) {
            model.addAttribute("appointments", appointmentService.getAppointmentsByPhone(phone));
            model.addAttribute("searchPhone", phone);
        }
        return "appointments/my-appointments";
    }

    /**
     * Randevu iptal et
     */
    @PostMapping("/{id}/cancel")
    public String cancelAppointment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        appointmentService.cancelAppointment(id);
        redirectAttributes.addFlashAttribute("successMessage", "Randevu iptal edildi.");
        return "redirect:/appointments/my";
    }

    /**
     * Uygun saat slotlarını getir (09:00 - 18:00 arası, saatlik)
     */
    private List<LocalTime> getAvailableTimeSlots() {
        List<LocalTime> slots = new ArrayList<>();
        for (int hour = 9; hour <= 18; hour++) {
            slots.add(LocalTime.of(hour, 0));
        }
        return slots;
    }
}
