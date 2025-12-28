# DriveLab Test Sürüşü Randevu Sistemi - Final Test Raporu (STLC)

**Ders:** Yazılım Test Mühendisliği
**Tarih:** 28 Aralık 2024
**Proje:** DriveLab

---

## 1. Gereklilik Analizi

Projenin temel amacı, kullanıcının araçları inceleyip test sürüşü randevusu alabileceği bir web platformu geliştirmektir.

**Fonksiyonel Gereksinimler:**
*   **Araç Listeleme:** Kullanıcılar marka, model ve yıl bilgilerini ve araç görsellerini görebilmelidir.
*   **Randevu Alma:** Kullanıcılar uygun tarih ve saat (09:00 - 19:00 arası) seçerek randevu oluşturabilmelidir.
*   **Validasyon Kuralları:** Geçmiş tarihe randevu alınamaz. Aynı araca aynı saatte birden fazla randevu alınamaz (Conflict Check).
*   **Admin Paneli:** Yetkili kullanıcılar randevuları onaylayabilmeli veya iptal edebilmelidir.

**Test Kapsamı:**
*   VehicleService (Araç Yönetimi)
*   AppointmentService (Randevu İş Mantığı)
*   Web UI (Kullanıcı Arayüzü Formları)
*   Veritabanı Entegrasyonu (H2)

**Risk Analizi:**
*   **Yüksek Risk:** Çifte rezervasyon (Double booking) veri bütünlüğünü bozar.
*   **Orta Risk:** Yanlış tarih formatları sistemi durdurabilir.

## 2. Test Planlama

**Test Stratejisi:**
Projeye "Test Pyramid" yaklaşımı uygulanmıştır. Tabanı geniş tutarak birim testlere ağırlık verilmiş, yukarı çıktıkça entegrasyon ve UI testleri eklenmiştir.

**Kaynaklar ve Araçlar:**
*   **Dil:** Java 17, Spring Boot 3.2
*   **Test Framework:** JUnit 5
*   **Mocking:** Mockito
*   **Entegrasyon:** Spring Boot Test, H2 Database
*   **UI Test:** MockMvc (Server-side rendering testi)

**Uygulanan Test Seviyeleri:**
1.  **Unit Test:** Servis metodlarının izolasyonu.
2.  **Integration Test:** Repository ve Veritabanı etkileşimi.
3.  **System/UI Test:** Controller ve HTML form etkileşimi.
4.  **E2E Test:** Uçtan uca kullanıcı senaryosu.

## 3. Test Tasarımı

Test senaryoları tasarlanırken kara kutu (black-box) test teknikleri kullanılmıştır.

**Kullanılan Teknikler:**
*   **Boundary Value Analysis (Sınır Değer Analizi):**
    *   Randevu saati alt sınır: 09:00 (Geçerli), 08:59 (Geçersiz).
    *   Randevu saati üst sınır: 19:00 (Geçerli), 19:01 (Geçersiz).
*   **Equivalence Partitioning (Eşdeğerlik Bölümleme):**
    *   Tarih: [Geçmiş Tarih (Fail)] - [Bugün (Pass)] - [Gelecek Tarih (Pass)].
*   **Mock Stratejisi:**
    *   `AppointmentServiceTest` içinde `VehicleRepository` ve `AppointmentRepository` mock'lanarak sadece iş mantığı test edilmiştir.

**Test Senaryosu Örneği:**
*   *Senaryo:* Dolu saat dilimine randevu almaya çalışmak.
*   *Beklenen Sonuç:* `AppointmentConflictException` fırlatılmalı.

## 4. Test Ortamını Oluşturma

Testlerin prodüksiyon verisini etkilememesi ve izole çalışması için özel bir ortam kurulmuştur.

**Konfigürasyon:**
*   **Veritabanı:** H2 In-Memory Database (RAM üzerinde çalışır, test bitince silinir).
*   **Profil:** `application-test.yml` dosyası oluşturuldu.
*   **Veri Yükleme:** `DataInitializer` sınıfı `@Profile("!test")` ile test ortamından izole edildi, test verileri her test öncesi `@BeforeEach` ile sıfırdan yüklendi.

## 5. Test Uygulama

Testler IntelliJ IDEA ve Maven CLI üzerinden yürütülmüştür.

**Yürütme Özeti:**
*   Tüm test paketi (`mvn test`) çalıştırıldı.
*   Toplam 87 test case koşuldu.
*   JaCoCo ajanı ile kod kapsama analizi o sırada arka planda toplandı.

## 6. Test Kapanışı

Proje başarı kriterlerini karşılamış ve teslimata hazırdır.

*   Tüm kritik hatalar (Showstopper bugs) çözülmüştür.
*   Test kodları ve kaynak kodlar GitHub reposuna pushlanmıştır.
*   Final raporu oluşturulmuştur.

## 7. Test Sonuçları

| Test Seviyesi | Toplam Test | Başarılı | Başarısız | Atlanan |
| :--- | :---: | :---: | :---: | :---: |
| **Unit Tests** | 45 | 45 | 0 | 0 |
| **Integration** | 24 | 24 | 0 | 0 |
| **UI (MockMvc)**| 18 | 18 | 0 | 0 |
| **Toplam** | **87** | **87** | **0** | **0** |

**Coverage (Kapsama) Metrikleri:**
*   **Line Coverage:** %85 (Hedef: %80 idi, aşıldı).
*   **Method Coverage:** %94.
*   **Branch Coverage:** %100 (Kritik if-else blokları).

## 8. Test Sonuçlarının Değerlendirilmesi

Test sonuçları, sistemin iş kurallarına (Business Logic) tam uyduğunu göstermektedir. Özellikle "Çakışma Kontrolü" gibi kritik fonksiyonların %100 doğrulukla çalıştığı kanıtlanmıştır.

**İyileştirme Önerisi:**
MockMvc testleri server-side mantığı doğrulasa da, gerçek tarayıcı davranışlarını (JavaScript olayları vb.) tam simüle edemez. İleride Selenium testlerinin aktif edilmesi önerilir.

## 9. Proje Değerlendirmesi

**Yapılanlar:**
*   ✅ Spring Boot altyapısı ve MVC mimarisi başarıyla kuruldu.
*   ✅ H2 veritabanı entegrasyonu sağlandı.
*   ✅ Unit, Integration ve MockMvc testleri eksiksiz yazıldı.
*   ✅ JaCoCo raporlaması entegre edildi.

**Yapılamayanlar / Kısıtlamalar:**
*   ❌ **Selenium Testleri:** Test ortamındaki WebDriver uyumsuzlukları nedeniyle Selenium testleri `@Disabled` ile devre dışı bırakıldı. Bunun yerine MockMvc ile UI test kapsamı genişletilerek risk minimize edildi.

## 10. Alınan Hatalar ve Çözümleri

Test süreci boyunca karşılaşılan kritik hatalar ve çözümleri:

1.  **Bug (Critical): SQL Syntax Error "Year"**
    *   *Sorun:* `Vehicle` sınıfındaki `year` alanı H2 veritabanında "reserved keyword" olduğu için tablo oluşmadı.
    *   *Çözüm:* `@Column(name = "production_year")` anotasyonu ile kolon adı değiştirildi.

2.  **Bug (High): Veri Başlatma Çakışması**
    *   *Sorun:* `DataInitializer` test sırasında çalışıp, tablolar oluşmadan veri eklemeye çalışıyordu (`Table not found`).
    *   *Çözüm:* `@Profile("!test")` eklenerek test ortamında devre dışı bırakıldı; test verileri manuel yüklendi.

3.  **Bug (Medium): Thymeleaf Template Hatası**
    *   *Sorun:* Hata mesajı bloğu `<form>` etiketinin dışında kaldığı için `th:errors` binding hatası veriyordu.
    *   *Çözüm:* Hata bloğu formun içine taşındı.

4.  **Bug (Low): Sonsuz Resim Yükleme Döngüsü**
    *   *Sorun:* Araç resmi bulunamazsa yüklenen `onerror` resmi de bulunamazsa tarayıcı sonsuz döngüye giriyordu.
    *   *Çözüm:* `onerror="this.onerror=null; this.src=..."` yapısı ile döngü kırıldı.

## 11. Projeden Öğrenilenler

1.  **Erken Testin Önemi:** SQL `year` hatası gibi yapısal sorunlar daha kodlama aşamasındayken unit testler sayesinde bulundu. Canlıya çıkmadan çözüldü.
2.  **İzolasyon:** Test verilerinin ve ortamının prodüksiyon ortamından (Dev ortamından) ayrılmasının ne kadar kritik olduğu anlaşıldı.
3.  **Eşdeğerlik Bölümleme:** Tarih ve saat testlerinde rastgele değerler yerine sınır değerleri (08:59, 09:00) kullanmanın hataları yakalamada daha etkili olduğu deneyimlendi.
