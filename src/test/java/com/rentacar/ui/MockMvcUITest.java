package com.rentacar.ui;

import com.rentacar.model.Vehicle;
import com.rentacar.repository.VehicleRepository;
import com.rentacar.service.VehicleService;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UI Testleri - MockMvc ile Controller Testleri
 * 
 * Test Seviyesi: UI TEST
 * Controller + View entegrasyonu test edilir
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("MockMvc UI Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MockMvcUITest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleService vehicleService;

    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        testVehicle = new Vehicle("BMW", "X5", 2024);
        testVehicle.setDescription("Test araç");
        testVehicle = vehicleRepository.save(testVehicle);
    }

    // ==================== ANA SAYFA TESTLERİ ====================

    @Test
    @Order(1)
    @DisplayName("UI-01: Ana sayfa yüklenmeli")
    void homePage_ShouldLoadSuccessfully() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("vehicles"));
    }

    @Test
    @Order(2)
    @DisplayName("UI-02: Ana sayfada araçlar listelenmeli")
    void homePage_ShouldDisplayVehicles() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("vehicles", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(content().string(containsString("BMW")));
    }

    // ==================== ARAÇ SAYFASI TESTLERİ ====================

    @Test
    @Order(3)
    @DisplayName("UI-03: Araç listesi sayfası yüklenmeli")
    void vehiclesPage_ShouldLoadSuccessfully() throws Exception {
        mockMvc.perform(get("/vehicles"))
                .andExpect(status().isOk())
                .andExpect(view().name("vehicles/list"))
                .andExpect(model().attributeExists("vehicles"));
    }

    @Test
    @Order(4)
    @DisplayName("UI-04: Araç detay sayfası yüklenmeli")
    void vehicleDetailPage_ShouldLoadSuccessfully() throws Exception {
        mockMvc.perform(get("/vehicles/{id}", testVehicle.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("vehicles/detail"))
                .andExpect(model().attribute("vehicle", hasProperty("brand", is("BMW"))));
    }

    @Test
    @Order(5)
    @DisplayName("UI-05: Araç arama çalışmalı")
    void vehicleSearch_ShouldReturnResults() throws Exception {
        mockMvc.perform(get("/vehicles/search").param("brand", "BMW"))
                .andExpect(status().isOk())
                .andExpect(view().name("vehicles/list"))
                .andExpect(model().attribute("searchTerm", "BMW"));
    }

    // ==================== RANDEVU FORMU TESTLERİ ====================

    @Test
    @Order(6)
    @DisplayName("UI-06: Randevu formu yüklenmeli")
    void appointmentForm_ShouldLoadSuccessfully() throws Exception {
        mockMvc.perform(get("/appointments/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments/form"))
                .andExpect(model().attributeExists("appointmentForm"))
                .andExpect(model().attributeExists("vehicles"))
                .andExpect(model().attributeExists("availableTimes"));
    }

    @Test
    @Order(7)
    @DisplayName("UI-07: Randevu formu vehicleId parametresi ile yüklenmeli")
    void appointmentForm_WithVehicleId_ShouldPreselect() throws Exception {
        mockMvc.perform(get("/appointments/new").param("vehicleId", testVehicle.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(model().attribute("appointmentForm",
                        hasProperty("vehicleId", is(testVehicle.getId()))));
    }

    @Test
    @Order(8)
    @DisplayName("UI-08: Geçerli randevu formu submit edilebilmeli")
    void appointmentForm_ValidSubmit_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("vehicleId", testVehicle.getId().toString())
                .param("customerName", "Test Müşteri")
                .param("customerPhone", "05551234567")
                .param("appointmentDate", java.time.LocalDate.now().plusDays(1).toString())
                .param("appointmentTime", "10:00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appointments/success"));
    }

    @Test
    @Order(9)
    @DisplayName("UI-09: Boş müşteri adı ile form hata vermeli")
    void appointmentForm_EmptyName_ShouldShowError() throws Exception {
        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("vehicleId", testVehicle.getId().toString())
                .param("customerName", "") // Boş!
                .param("customerPhone", "05551234567")
                .param("appointmentDate", java.time.LocalDate.now().plusDays(1).toString())
                .param("appointmentTime", "10:00"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("appointmentForm", "customerName"));
    }

    @Test
    @Order(10)
    @DisplayName("UI-10: Geçersiz telefon ile form hata vermeli")
    void appointmentForm_InvalidPhone_ShouldShowError() throws Exception {
        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("vehicleId", testVehicle.getId().toString())
                .param("customerName", "Test User")
                .param("customerPhone", "123") // Geçersiz!
                .param("appointmentDate", java.time.LocalDate.now().plusDays(1).toString())
                .param("appointmentTime", "10:00"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments/form"))
                .andExpect(model().attributeHasFieldErrors("appointmentForm", "customerPhone"));
    }

    @Test
    @Order(11)
    @DisplayName("UI-11: Araç seçilmeden form hata vermeli")
    void appointmentForm_NoVehicle_ShouldShowError() throws Exception {
        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("customerName", "Test User")
                .param("customerPhone", "05551234567")
                .param("appointmentDate", java.time.LocalDate.now().plusDays(1).toString())
                .param("appointmentTime", "10:00"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("appointmentForm", "vehicleId"));
    }

    // ==================== RANDEVULARIM SAYFASI TESTLERİ ====================

    @Test
    @Order(12)
    @DisplayName("UI-12: Randevularım sayfası yüklenmeli")
    void myAppointments_ShouldLoadSuccessfully() throws Exception {
        mockMvc.perform(get("/appointments/my"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments/my-appointments"));
    }

    @Test
    @Order(13)
    @DisplayName("UI-13: Telefon ile randevu arama")
    void myAppointments_SearchByPhone_ShouldWork() throws Exception {
        mockMvc.perform(get("/appointments/my").param("phone", "05551234567"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments/my-appointments"))
                .andExpect(model().attribute("searchPhone", "05551234567"));
    }

    // ==================== BAŞARI SAYFASI TESTİ ====================

    @Test
    @Order(14)
    @DisplayName("UI-14: Başarı sayfası yüklenmeli")
    void successPage_ShouldLoadSuccessfully() throws Exception {
        mockMvc.perform(get("/appointments/success"))
                .andExpect(status().isOk())
                .andExpect(view().name("appointments/success"));
    }

    // ==================== ADMIN PANEL TESTLERİ ====================

    @Test
    @Order(15)
    @DisplayName("UI-15: Admin paneli auth gerektirmeli")
    void adminDashboard_WithoutAuth_ShouldRequireLogin() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(16)
    @WithMockUser(roles = "ADMIN")
    @DisplayName("UI-16: Admin paneli yetkili kullanıcı ile erişilebilmeli")
    void adminDashboard_WithAuth_ShouldLoad() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("appointments"))
                .andExpect(model().attributeExists("todayAppointments"))
                .andExpect(model().attributeExists("pendingCount"));
    }

    @Test
    @Order(17)
    @WithMockUser(roles = "ADMIN")
    @DisplayName("UI-17: Admin randevu listesi yüklenmeli")
    void adminAppointments_ShouldLoad() throws Exception {
        mockMvc.perform(get("/admin/appointments"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/appointments"))
                .andExpect(model().attributeExists("appointments"))
                .andExpect(model().attributeExists("statuses"));
    }

    @Test
    @Order(18)
    @WithMockUser(roles = "ADMIN")
    @DisplayName("UI-18: Admin randevu filtreleme çalışmalı")
    void adminAppointments_FilterByStatus_ShouldWork() throws Exception {
        mockMvc.perform(get("/admin/appointments").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("selectedStatus", "PENDING"));
    }
}
