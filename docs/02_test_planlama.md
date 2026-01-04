# Test Planlama Dokümanı (Test Plan)

**Proje:** DriveLab - Test Sürüşü Randevu Sistemi

## 1. Testin Kapsamı (Scope)
### 1.1. Kapsam Dahili (In-Scope)
*   **Modüller:** Araç Listeleme, Randevu Oluşturma, Admin Paneli.
*   **Test Seviyeleri:** Unit Test, Integration Test, UI Validation (MockMvc).
*   **Fonksiyonlar:** CRUD işlemleri, Validasyon kuralları, Hata yönetimi.

### 1.2. Kapsam Harici (Out-Scope)
*   **Performans Testi:** Yük ve stres testleri bu fazda yapılmayacaktır.
*   **E2E (Selenium) Testleri:** WebDriver uyumluluk sorunları nedeniyle testler MockMvc ile simüle edilecektir.
*   **Mobil Uygulama Testi:** Sadece web arayüzü test edilecektir.

## 2. Test Stratejisi
*   **Yaklaşım:** Önce backend servis katmanı (Unit), sonra veritabanı (Integration), en son arayüz (UI/Controller) test edilecektir.
*   **Veri Yönetimi:** Testler için H2 in-memory veritabanı kullanılacak, her test öncesi veriler sıfırlanacaktır.

## 3. Test Ortamı (Environment)
*   **İşletim Sistemi:** Windows / Linux / MacOS
*   **Dil:** Java 17
*   **Framework:** Spring Boot 3.2.1
*   **DB:** H2 Database
*   **Araçlar:** JUnit 5, Mockito, JaCoCo, Maven

## 4. Test Kriterleri
### 4.1. Başlangıç Kriterleri (Entry Criteria)
*   Kaynak kodun derlenebilir olması.
*   GitHub repository'sinin güncel olması.

### 4.2. Bitiş Kriterleri (Exit Criteria)
*   Kritik (Critical) ve Yüksek (High) seviyeli bug kalmaması.
*   Kod kapsama (Code Coverage) oranının %80 üzerinde olması.
*   Tüm regresyon testlerinin başarıyla (Green) geçmesi.

## 5. Riskler ve Önlemler
| Risk | Etki | Önlem |
| :--- | :--- | :--- |
| Tarayıcı sürücü uyumsuzluğu | Testlerin çalışmaması | Selenium yerine MockMvc kullanılması |
| Veri tutarsızlığı | Hatalı test sonuçları | @Transactional ve H2 kullanımı |
