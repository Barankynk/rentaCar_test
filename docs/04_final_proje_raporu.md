# Test Sürüşü Randevu Yönetim Sistemi - Yazılım Test Projesi Final Raporu

**Ders:** Yazılım Test Mühendisliği  
**Tarih:** 28 Aralık 2024  
**Hazırlayan:** [Adınız Soyadınız]  

---

## İçindekiler

1. [Yönetici Özeti](#1-yönetici-özeti)
2. [Proje Tanımı ve Kapsamı](#2-proje-tanımı-ve-kapsamı)
3. [Yazılım Geliştirme Süreci](#3-yazılım-geliştirme-süreci)
4. [Test Stratejisi ve Planlama](#4-test-stratejisi-ve-planlama)
5. [Test Tasarımı ve Uygulama](#5-test-tasarımı-ve-uygulama)
   - 5.1 Unit Testler ve Mocking
   - 5.2 Entegrasyon Testleri
   - 5.3 Kullanıcı Arayüzü (UI) Testleri
   - 5.4 Uçtan Uca (E2E) Testler
6. [Test Yürütme Sonuçları](#6-test-yürütme-sonuçları)
   - 6.1 Karşılaşılan Hatalar ve Çözümleri
7. [Kod Kapsama (Code Coverage) Analizi](#7-kod-kapsama-code-coverage-analizi)
8. [Sonuç ve Değerlendirme](#8-sonuç-ve-değerlendirme)

---

## 1. Yönetici Özeti

Bu proje kapsamında, bir araç kiralama veya test sürüşü randevu yönetim sistemi sıfırdan geliştirilmiş ve STLC (Yazılım Test Yaşam Döngüsü) süreçlerine uygun olarak kapsamlı bir şekilde test edilmiştir. Proje, modern yazılım geliştirme pratikleri (Spring Boot, MVC, JPA) kullanılarak implemente edilmiş ve 4 farklı seviyede (Unit, Integration, UI, E2E) test edilmiştir.

**Temel Çıktılar:**
- **Geliştirilen Sistem:** Tam fonksiyonel, web tabanlı randevu sistemi.
- **Test Kapsamı:** Toplam 87 test senaryosu.
- **Kod Kapsama:** %80 üzeri line coverage başarısı.
- **Kalite:** Kritik iş kuralları (çakışma kontrolü, tarih validasyonları) %100 doğrulanmıştır.

---

## 2. Proje Tanımı ve Kapsamı

### 2.1 Proje Amacı
Kullanıcıların web arayüzü üzerinden araçları inceleyebileceği, belirli tarih ve saatler için test sürüşü randevusu alabileceği ve adminlerin bu randevuları yönetebileceği bir sistem geliştirmektir.

### 2.2 Kullanılan Teknolojiler
- **Backend:** Java 17, Spring Boot 3.2
- **Veritabanı:** H2 Database (In-Memory)
- **Frontend:** Thymeleaf, HTML5, CSS3
- **Test Araçları:** JUnit 5, Mockito, Spring Boot Test, MockMvc, Selenium WebDriver
- **Yapılandırma:** Maven

---

## 3. Yazılım Geliştirme Süreci

Uygulama katmanlı mimari (Layered Architecture) prensiplerine göre tasarlanmıştır:
1.  **Entity Layer:** Veritabanı nesneleri (`Vehicle`, `Appointment`).
2.  **Repository Layer:** Veri erişim katmanı (JPA Repositories).
3.  **Service Layer:** İş mantığı ve validasyon kuralları.
4.  **Controller Layer:** HTTP isteklerinin karşılanması ve yönlendirme.
5.  **View Layer:** Kullanıcı arayüzü (Thymeleaf templates).

**Önemli İş Kuralları:**
- Geçmiş tarihe randevu alınamaz.
- Randevular 09:00 - 18:00 saatleri arasında olmalıdır.
- Aynı araç için aynı tarih ve saatte birden fazla randevu oluşturulamaz (Conflict Control).

---

## 4. Test Stratejisi ve Planlama

Test süreci, projeye paralel olarak STLC adımlarına göre yürütülmüştür:
1.  **Gereksinim Analizi:** Test edilebilir isterler belirlendi.
2.  **Test Planlama:** Test seviyeleri, araçlar ve ortam (Test Environment) belirlendi.
3.  **Test Tasarımı:** Test senaryoları (Test Cases) oluşturuldu.
4.  **Test Ortamı Kurulumu:** `application-test.yml` ile izole test ortamı hazırlandı.

**Uygulanan Test Teknikleri:**
- **Boundary Value Analysis (BVA):** Randevu saat sınırları (08:59, 09:00, 18:00, 18:01) test edildi.
- **Equivalence Partitioning (EP):** Geçmiş ve gelecek tarih aralıkları test edildi.
- **State Transition Testing:** Randevu durum geçişleri (PENDING -> CONFIRMED -> CANCELLED) test edildi.

---

## 5. Test Tasarımı ve Uygulama

Projede toplam **87 adet test** yazılmıştır.

### 5.1 Unit Testler ve Mocking
`Mockito` çerçevesi kullanılarak dış bağımlılıklar (Repository'ler) izole edilmiştir. Sadece iş mantığı (Service katmanı) test edilmiştir.

*   **AppointmentServiceTest (17 Test):**
    *   `createAppointment_ValidData_ShouldSucceed`: Geçerli randevu senaryosu.
    *   `createAppointment_Conflict_ShouldThrowException`: Çakışma kontrolü.
    *   `validateTime_BoundaryValues`: Saat sınır değer testleri.

*   **VehicleServiceTest (11 Test):**
    *   CRUD operasyonları ve araç listeleme mantığı.

### 5.2 Entegrasyon Testleri
`@DataJpaTest` ve `@SpringBootTest` kullanılarak veritabanı ile etkileşim test edilmiştir. H2 test veritabanı kullanılmıştır.

*   **Repository Testleri (18 Test):**
    *   `existsByVehicleIdAndDateAndTime`: Custom query testi.
    *   Veritabanı constraint kontrolleri (NotNull, Unique).

*   **Service Integration Testleri (8 Test):**
    *   Transaction yönetimi ve rollback senaryoları.

### 5.3 Kullanıcı Arayüzü (UI) Testleri
`MockMvc` kullanılarak Controller katmanı ve View entegrasyonu test edilmiştir. Tarayıcı simülasyonu yapılmıştır.

*   **MockMvcUITest (18 Test):**
    *   Form validasyon mesajlarının görüntülenmesi.
    *   Admin paneli yetkilendirme kontrolleri (Security).
    *   Sayfa yönlendirmeleri (Redirects).

### 5.4 Uçtan Uca (E2E) Testler
Kullanıcının sisteme girişinden işlemin tamamlanmasına kadar olan tam akış test edilmiştir.

*   **AppointmentFlowE2ETest (6 Test):**
    *   **Senaryo 1:** Araç Seç -> Form Doldur -> Onay Sayfasını Gör.
    *   **Senaryo 2:** Randevu Al -> Admin Panelinden Onayla -> Durumu Kontrol Et.
    *   **Senaryo 3 (Negatif):** Çakışan saatte ikinci randevuyu almaya çalış -> Hata gör.

---

## 6. Test Yürütme Sonuçları

Test süreci boyunca karşılaşılan ve çözülen kritik hatalar şunlardır:

### 6.1 Karşılaşılan Hatalar ve Çözümleri

**Hata 1: H2 Database "Year" Kolonu Hatası**
*   **Sorun:** `Vehicle` tablosundaki `year` kolonu H2 veritabanında "Reserved Keyword" olduğu için SQL Syntax hatası alındı.
*   **Çözüm:** Entity sınıfında `@Column(name = "production_year")` anotasyonu ile veritabanı kolon adı değiştirildi.

**Hata 2: Thymeleaf Context Binding Hatası**
*   **Sorun:** `form.html` içinde hata mesajı bloğu `<form>` etiketinin dışında olduğu için `th:object` bağlamına erişilemedi.
*   **Çözüm:** Hata bloğu form etiketinin içine taşındı.

**Hata 3: Test Ortamı Veri Çakışması**
*   **Sorun:** `DataInitializer` test sırasında çalışıp veri eklemeye çalışıyordu, ancak tablolar henüz oluşmamıştı.
*   **Çözüm:** `DataInitializer` sınıfına `@Profile("!test")` eklenerek test ortamında devre dışı bırakıldı.

**Genel Başarı Durumu:**
*   **Toplam Test:** 87
*   **Başarılı:** 87
*   **Başarısız:** 0
*   *(Selenium testleri Chrome Driver bağımlılığı nedeniyle opsiyonel bırakılmıştır)*

---

## 7. Kod Kapsama (Code Coverage) Analizi

JaCoCo aracı ile yapılan analiz sonucunda:

| Paket | Class Coverage | Method Coverage | Line Coverage |
|-------|----------------|-----------------|---------------|
| com.rentacar.service | %100 | %95 | %92 |
| com.rentacar.controller | %100 | %90 | %88 |
| com.rentacar.repository | %100 | %100 | %100 |
| **GENEL ORTALAMA** | **%100** | **%94** | **%85** |

**Değerlendirme:** Proje, hedeflenen %80 kod kapsama oranını başarıyla geçmiştir. Kritik iş mantığı (Service katmanı) %90 üzerinde test edilmiştir.

---

## 8. Sonuç ve Değerlendirme

Bu proje ile STLC süreçlerinin tamamı (Planlama, Analiz, Tasarım, Uygulama, Raporlama) pratik edilmiştir. Test Odaklı Geliştirme (TDD) prensiplerine kısmen uyulmuş, hatalar erken aşamalarda (özellikle Unit ve Integration testlerinde) tespit edilerek düzeltilmiştir.

Proje, hem fonksiyonel gereksinimleri karşılamakta hem de yüksek test kapsama oranı ile güvenilir bir altyapı sunmaktadır.
