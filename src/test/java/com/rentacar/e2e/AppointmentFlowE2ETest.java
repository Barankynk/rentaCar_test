package com.rentacar.e2e;

import com.rentacar.model.Appointment;
import com.rentacar.model.AppointmentStatus;
import com.rentacar.model.Vehicle;
import com.rentacar.repository.AppointmentRepository;
import com.rentacar.repository.VehicleRepository;
import com.rentacar.service.AppointmentService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-End (E2E) Testleri
 * 
 * Test Seviyesi: END-TO-END TEST
 * Tam kullanıcı akışını test eder:
 * Araç Seç → Randevu Al → Sonuç Gör
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("End-to-End Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AppointmentFlowE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentService appointmentService;

    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        // Temizlik
        appointmentRepository.deleteAll();

        // Test aracı
        testVehicle = new Vehicle("E2E", "TestCar", 2024);
        testVehicle.setAvailable(true);
        testVehicle = vehicleRepository.save(testVehicle);
    }

    @Test
    @Order(1)
    @DisplayName("E2E-01: Tam Akış - Araç Seç → Form Doldur → Randevu Al → Sonuç Gör")
    void fullFlow_CreateAppointment_ShouldComplete() throws Exception {
        LocalDate appointmentDate = LocalDate.now().plusDays(1);
        String customerPhone = "05559991111";

        // 1. Ana sayfayı aç ve araçları gör
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("vehicles"));

        // 2. Araç detay sayfasına git
        mockMvc.perform(get("/vehicles/{id}", testVehicle.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("vehicle"));

        // 3. Randevu formunu aç (araç seçili)
        mockMvc.perform(get("/appointments/new")
                .param("vehicleId", testVehicle.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("appointmentForm"));

        // 4. Formu doldur ve gönder
        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("vehicleId", testVehicle.getId().toString())
                .param("customerName", "E2E Test Müşteri")
                .param("customerPhone", customerPhone)
                .param("customerEmail", "e2e@test.com")
                .param("appointmentDate", appointmentDate.toString())
                .param("appointmentTime", "10:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appointments/success"));

        // 5. Başarı sayfasını gör
        mockMvc.perform(get("/appointments/success"))
                .andExpect(status().isOk());

        // 6. Veritabanında randevu oluşturulmuş mu kontrol et
        List<Appointment> appointments = appointmentRepository.findByCustomerPhone(customerPhone);
        assertEquals(1, appointments.size());
        assertEquals("E2E Test Müşteri", appointments.get(0).getCustomerName());
        assertEquals(AppointmentStatus.PENDING, appointments.get(0).getStatus());
    }

    @Test
    @Order(2)
    @DisplayName("E2E-02: Randevularımı Görüntüle Akışı")
    void viewMyAppointments_ShouldShowUserAppointments() throws Exception {
        String customerPhone = "05559992222";
        LocalDate date = LocalDate.now().plusDays(2);

        // 1. Önce bir randevu oluştur
        appointmentService.createAppointment(
                testVehicle.getId(),
                "My Appointments Test",
                customerPhone,
                null,
                date,
                LocalTime.of(11, 0));

        // 2. Randevularım sayfasına git
        mockMvc.perform(get("/appointments/my"))
                .andExpect(status().isOk());

        // 3. Telefon ile ara
        mockMvc.perform(get("/appointments/my")
                .param("phone", customerPhone))
                .andExpect(status().isOk())
                .andExpect(model().attribute("searchPhone", customerPhone))
                .andExpect(model().attributeExists("appointments"));
    }

    @Test
    @Order(3)
    @DisplayName("E2E-03: Randevu İptal Akışı")
    void cancelAppointment_ShouldUpdateStatus() throws Exception {
        String customerPhone = "05559993333";
        LocalDate date = LocalDate.now().plusDays(3);

        // 1. Randevu oluştur
        Appointment created = appointmentService.createAppointment(
                testVehicle.getId(),
                "Cancel Flow Test",
                customerPhone,
                null,
                date,
                LocalTime.of(14, 0));

        assertEquals(AppointmentStatus.PENDING, created.getStatus());

        // 2. Randevuyu iptal et
        mockMvc.perform(post("/appointments/{id}/cancel", created.getId()))
                .andExpect(status().is3xxRedirection());

        // 3. Veritabanında durum güncellenmiş mi kontrol et
        Appointment updated = appointmentRepository.findById(created.getId()).orElseThrow();
        assertEquals(AppointmentStatus.CANCELLED, updated.getStatus());
    }

    @Test
    @Order(4)
    @WithMockUser(roles = "ADMIN")
    @DisplayName("E2E-04: Admin Akışı - Randevuları Gör ve Onayla")
    void adminFlow_ViewAndConfirmAppointment() throws Exception {
        String customerPhone = "05559994444";
        LocalDate date = LocalDate.now().plusDays(4);

        // 1. Randevu oluştur
        Appointment created = appointmentService.createAppointment(
                testVehicle.getId(),
                "Admin Flow Test",
                customerPhone,
                null,
                date,
                LocalTime.of(15, 0));

        // 2. Admin paneline git
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("appointments"));

        // 3. Randevu listesini gör
        mockMvc.perform(get("/admin/appointments"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("appointments"));

        // 4. Randevuyu onayla
        mockMvc.perform(post("/admin/appointments/{id}/confirm", created.getId()))
                .andExpect(status().is3xxRedirection());

        // 5. Durum güncellenmiş mi
        Appointment confirmed = appointmentRepository.findById(created.getId()).orElseThrow();
        assertEquals(AppointmentStatus.CONFIRMED, confirmed.getStatus());
    }

    @Test
    @Order(5)
    @DisplayName("E2E-05: Çakışma Senaryosu - Aynı Slot İkinci Kez Alınamamalı")
    void conflictScenario_ShouldPreventDoubleBooking() throws Exception {
        LocalDate date = LocalDate.now().plusDays(5);
        LocalTime time = LocalTime.of(16, 0);

        // 1. İlk randevuyu oluştur
        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("vehicleId", testVehicle.getId().toString())
                .param("customerName", "First Customer")
                .param("customerPhone", "05551111111")
                .param("appointmentDate", date.toString())
                .param("appointmentTime", time.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appointments/success"));

        // 2. Aynı slot için ikinci randevu dene
        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("vehicleId", testVehicle.getId().toString())
                .param("customerName", "Second Customer")
                .param("customerPhone", "05552222222")
                .param("appointmentDate", date.toString())
                .param("appointmentTime", time.toString()))
                .andExpect(status().isOk()) // Form tekrar gösterilir
                .andExpect(view().name("appointments/form"))
                .andExpect(model().hasErrors()); // Hata mesajı var

        // 3. Veritabanında sadece 1 randevu olmalı
        List<Appointment> appointments = appointmentRepository.findByVehicleIdAndAppointmentDate(
                testVehicle.getId(), date);
        assertEquals(1, appointments.size());
        assertEquals("First Customer", appointments.get(0).getCustomerName());
    }

    @Test
    @Order(6)
    @DisplayName("E2E-06: Farklı Saatlerde Birden Fazla Randevu Alınabilmeli")
    void multipleAppointments_DifferentTimes_ShouldSucceed() throws Exception {
        LocalDate date = LocalDate.now().plusDays(6);

        // Farklı saatlerde 3 randevu oluştur
        String[] times = { "09:00", "12:00", "17:00" };
        String[] names = { "User1", "User2", "User3" };

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/appointments")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("vehicleId", testVehicle.getId().toString())
                    .param("customerName", names[i])
                    .param("customerPhone", "0555000000" + i)
                    .param("appointmentDate", date.toString())
                    .param("appointmentTime", times[i]))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/appointments/success"));
        }

        // Veritabanında 3 randevu olmalı
        List<Appointment> appointments = appointmentRepository.findByVehicleIdAndAppointmentDate(
                testVehicle.getId(), date);
        assertEquals(3, appointments.size());
    }
}
