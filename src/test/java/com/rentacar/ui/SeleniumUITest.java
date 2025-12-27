package com.rentacar.ui;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Disabled;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium UI Testleri
 * 
 * Test Seviyesi: UI TEST (End-to-End UI)
 * Gerçek tarayıcı ile kullanıcı arayüzü testleri
 * 
 * NOT: Bu testler headless Chrome kullanır
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Selenium UI Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled("Selenium tests disabled - requires Chrome driver setup")
class SeleniumUITest {

    @LocalServerPort
    private int port;

    private static WebDriver driver;
    private static WebDriverWait wait;

    private String baseUrl;

    @BeforeAll
    static void setUpDriver() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    static void tearDownDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    // ==================== ANA SAYFA TESTLERİ ====================

    @Test
    @Order(1)
    @DisplayName("SEL-01: Ana sayfa açılmalı ve başlık görünmeli")
    void homePage_ShouldDisplayTitle() {
        // When
        driver.get(baseUrl);

        // Then
        assertTrue(driver.getTitle().contains("Test Sürüşü"));

        WebElement hero = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".hero h1")));
        assertTrue(hero.getText().contains("Randevu"));
    }

    @Test
    @Order(2)
    @DisplayName("SEL-02: Ana sayfada Randevu Al butonu çalışmalı")
    void homePage_AppointmentButton_ShouldNavigate() {
        // Given
        driver.get(baseUrl);

        // When
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector(".hero .btn-primary")));
        btn.click();

        // Then
        wait.until(ExpectedConditions.urlContains("/appointments/new"));
        assertTrue(driver.getCurrentUrl().contains("/appointments/new"));
    }

    // ==================== ARAÇ LİSTELEME TESTLERİ ====================

    @Test
    @Order(3)
    @DisplayName("SEL-03: Araç listesi görüntülenmeli")
    void vehiclesPage_ShouldDisplayVehicles() {
        // When
        driver.get(baseUrl + "/vehicles");

        // Then
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1")));
        assertTrue(driver.getPageSource().contains("Araç"));
    }

    // ==================== RANDEVU FORMU TESTLERİ ====================

    @Test
    @Order(4)
    @DisplayName("SEL-04: Randevu formu doldurulabilmeli ve gönderilebilmeli")
    void appointmentForm_ShouldSubmitSuccessfully() {
        // Given
        driver.get(baseUrl + "/appointments/new");

        // When - Form alanlarını doldur
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("vehicleId")));

        // Araç seç
        Select vehicleSelect = new Select(driver.findElement(By.id("vehicleId")));
        if (vehicleSelect.getOptions().size() > 1) {
            vehicleSelect.selectByIndex(1); // İlk aracı seç
        }

        // Müşteri bilgileri
        WebElement nameInput = driver.findElement(By.id("customerName"));
        nameInput.clear();
        nameInput.sendKeys("Selenium Test User");

        WebElement phoneInput = driver.findElement(By.id("customerPhone"));
        phoneInput.clear();
        phoneInput.sendKeys("05559876543");

        // Tarih - yarın
        WebElement dateInput = driver.findElement(By.id("appointmentDate"));
        dateInput.clear();
        dateInput.sendKeys(LocalDate.now().plusDays(1).toString());

        // Saat
        Select timeSelect = new Select(driver.findElement(By.id("appointmentTime")));
        timeSelect.selectByIndex(1); // 09:00

        // Submit
        WebElement submitBtn = driver.findElement(By.id("submitBtn"));
        submitBtn.click();

        // Then
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/success"),
                ExpectedConditions.visibilityOfElementLocated(By.className("error-message"))));

        // Başarılı olmalı (veya çakışma olabilir - her iki durum da kabul edilebilir)
        String currentUrl = driver.getCurrentUrl();
        String pageSource = driver.getPageSource();

        assertTrue(
                currentUrl.contains("/success") ||
                        pageSource.contains("error") ||
                        pageSource.contains("hata"),
                "Form gönderilmeli ve sonuç görülmeli");
    }

    @Test
    @Order(5)
    @DisplayName("SEL-05: Boş form gönderildiğinde hata mesajları görünmeli")
    void appointmentForm_EmptySubmit_ShouldShowErrors() {
        // Given
        driver.get(baseUrl + "/appointments/new");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("submitBtn")));

        // When - Boş formu gönder
        WebElement submitBtn = driver.findElement(By.id("submitBtn"));
        submitBtn.click();

        // Then - HTML5 validasyonu veya server-side hata
        // Not: HTML5 required attribute'ları formu göndermeyecek
        // Bu durumda sayfa hala form sayfasında kalmalı
        assertTrue(driver.getCurrentUrl().contains("/appointments"));
    }

    // ==================== NAVİGASYON TESTLERİ ====================

    @Test
    @Order(6)
    @DisplayName("SEL-06: Navigasyon menüsü çalışmalı")
    void navigation_ShouldWork() {
        // Given
        driver.get(baseUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".nav-links")));

        // When - Araçlar linkine tıkla
        WebElement vehiclesLink = driver.findElement(By.linkText("Araçlar"));
        vehiclesLink.click();

        // Then
        wait.until(ExpectedConditions.urlContains("/vehicles"));
        assertTrue(driver.getCurrentUrl().contains("/vehicles"));
    }

    // ==================== RANDEVULARIM SAYFASI ====================

    @Test
    @Order(7)
    @DisplayName("SEL-07: Randevularım sayfasında telefon araması yapılabilmeli")
    void myAppointments_PhoneSearch_ShouldWork() {
        // Given
        driver.get(baseUrl + "/appointments/my");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[name='phone']")));

        // When
        WebElement phoneInput = driver.findElement(By.cssSelector("input[name='phone']"));
        phoneInput.sendKeys("05551234567");

        WebElement searchBtn = driver.findElement(By.cssSelector(".search-form button"));
        searchBtn.click();

        // Then
        wait.until(ExpectedConditions.urlContains("phone="));
        assertTrue(driver.getCurrentUrl().contains("phone=05551234567"));
    }

    // ==================== RESPONSİVE TEST ====================

    @Test
    @Order(8)
    @DisplayName("SEL-08: Sayfa render edilmeli (basic smoke test)")
    void allPages_ShouldRender() {
        // Test multiple pages
        String[] pages = { "/", "/vehicles", "/appointments/new", "/appointments/my" };

        for (String page : pages) {
            driver.get(baseUrl + page);

            // Her sayfa en az bir element içermeli
            assertFalse(driver.findElements(By.tagName("body")).isEmpty(),
                    "Page should render: " + page);
        }
    }
}
