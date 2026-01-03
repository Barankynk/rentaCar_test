# DriveLab Test Sürüşü Randevu Sistemi - Final Test Raporu (STLC)

**Ders:** Yazılım Test Mühendisliği
**Tarih:** 28 Aralık 2024
**Proje:** DriveLab (Test Sürüşü Randevu Yönetim Sistemi)

---

## 1. Gereklilik Analizi (Requirement Analysis)

Bu aşamada, geliştirilecek sistemin ne yapması gerektiği ve müşteri/kullanıcı beklentileri analiz edilmiştir.

### 1.1 Fonksiyonel Gereksinimler
| ID | Gereksinim Tanımı | Öncelik | Kabul Kriteri |
| :--- | :--- | :--- | :--- |
| **REQ-001** | Kullanıcı mevcut araçları listeleyebilmelidir. | Yüksek | Araç listesi sayfasında en az bir araç görünmeli; resim, marka, model bilgileri gelmelidir. |
| **REQ-002** | Kullanıcı seçtiği araç için randevu alabilmelidir. | Yüksek | Form eksiksiz doldurulduğunda başarı mesajı gösterilmelidir. |
| **REQ-003** | Sistem randevu çakışmalarını engellemelidir. | Kritik | Aynı araca, aynı tarih ve saatte ikinci bir talep gelirse hata verilmelidir. |
| **REQ-004** | Randevu tarihleri ve saatleri validasyona tabi tutulmalıdır. | Orta | Geçmiş tarih seçilememeli; randevular 09:00 - 18:00 arasında olmalıdır. |
| **REQ-005** | Admin paneli üzerinden onay/red işlemleri yapılabilmelidir. | Yüksek | Admin onayı olmayan randevular "Beklemede" statüsünde kalmalıdır. |

### 1.2 Fonksiyonel Olmayan Gereksinimler
*   **Performans:** Sayfa yüklenme süreleri 3 saniyenin altında olmalıdır.
*   **Güvenlik:** Admin paneline sadece yetkili kullanıcılar (`ROLE_ADMIN`) erişebilmelidir.
*   **Uyumluluk:** Web arayüzü Google Chrome, Firefox ve Edge tarayıcılarında düzgün çalışmalıdır.
*   **Veri Bütünlüğü:** Tüm randevu kayıtları ACID prensiplerine uygun olarak veritabanında saklanmalıdır.

### 1.3 Test Kapsamı (Scope)
*   **Kapsam İçi:** `AppointmentService`, `VehicleService` iş mantığı, Web UI form validasyonları, H2 veritabanı entegrasyonu.
*   **Kapsam Dışı:** Ödeme sistemi entegrasyonu, Mobil uygulama testleri, Yük testi (Load Testing).

### 1.4 Risk Analizi
*   **Çifte Rezervasyon (Double Booking):** Veri tutarsızlığına yol açar. (Önlem: `@Transactional` ve veritabanı seviyesinde Unique Constraint kontrolleri).
*   **Yanlış Tarih Formatları:** Sistem kilitlenmesine neden olabilir. (Önlem: `LocalDate` ve `LocalTime` tipleri ile sıkı tip kontrolü).

---

## 2. Test Planlama (Test Planning)

### 2.1 Test Stratejisi (Test Pyramid Approach)
Projede **Test Piramidi** yaklaşımı benimsenmiştir.
1.  **Unit Tests (%60):** En alt katman. Hızlı, izole ve düşük maliyetli.
2.  **Integration Tests (%30):** Orta katman. Servisler ve Veritabanı arasındaki iletişim.
3.  **UI/E2E Tests (%10):** En üst katman. Kullanıcı deneyimini simüle eden testler.

### 2.2 Kaynak Planlaması
*   **Yazılım Dili:** Java 17, Spring Boot 3.2.0
*   **Build Tool:** Maven 3.9+
*   **Test Framework:** JUnit 5 (Jüpiter)
*   **Mocking Framework:** Mockito 5.x
*   **Assertion Library:** AssertJ
*   **Code Coverage Tool:** JaCoCo
*   **CI/CD:** GitHub Actions (Opsiyonel konfigürasyon hazırlandı)

### 2.3 Giriş ve Çıkış Kriterleri (Entry/Exit Criteria)
*   **Giriş Kriteri:** Kodun derlenebilir olması (Build Success), Veritabanı şemasının oluşması.
*   **Çıkış Kriteri:** Tüm kritik test senaryolarının geçmesi (%100 Pass Rate), Code Coverage oranının %80 üzerinde olması.

---

## 3. Test Tasarımı (Test Design)

### 3.1 Test Tasarım Teknikleri
*   **Boundary Value Analysis (Sınır Değer Analizi):**
    *   *Senaryo:* Randevu Saati Kontrolü.
    *   *Test Verileri:* 08:59 (Fail), 09:00 (Pass), 18:00 (Pass), 18:01 (Fail).
*   **Equivalence Partitioning (Eşdeğerlik Bölümleme):**
    *   *Senaryo:* Tarih Validasyonu.
    *   *Bölümler:* [Geçmiş Tarih -> Hata], [Bugün -> Geçerli], [Gelecek Yıl -> Geçerli].

### 3.2 Test Senaryoları (Örnekler)
| ID | Senaryo | Beklenen Sonuç | Test Tipi |
| :--- | :--- | :--- | :--- |
| **TS-01** | Geçerli verilerle randevu oluşturma. | Randevu ID döner, statü PENDING olur. | Unit (Pozitif) |
| **TS-02** | Dolu bir saate (çakışan saat) randevu alma denemesi. | `AppointmentConflictException` fırlatılır. | Unit (Negatif) |
| **TS-03** | Admin panelinden randevu onaylama. | Statü CONFIRMED olur. | Integration |
| **TS-04** | Formda zorunlu alanları boş bırakıp gönderme. | Hata mesajı div'i görünür. | UI (MockMvc) |

### 3.3 Test Verisi Stratejisi
*   Normal çalışma zamanında `DataInitializer` sınıfı örnek veri yükler.
*   Test sırasında bu sınıf devre dışı bırakılır (`@Profile("!test")`).
*   Her test metodu öncesi `@BeforeEach` ile temiz ve izole veri (repository.save) oluşturulur.

---

## 4. Test Ortamını Oluşturma (Test Environment Setup)

### 4.1 Ortam Konfigürasyonu
Prodüksiyon ortamından bağımsız, izole bir test ortamı kurulmuştur.
*   **Konfigürasyon Dosyası:** `src/test/resources/application-test.yml`
*   **Veritabanı:** H2 In-Memory Database (`jdbc:h2:mem:testdb`). Her test koşumunda (`create-drop`) sıfırdan oluşturulur ve test bitince silinir.
*   **Profil Aktivasyonu:** `@ActiveProfiles("test")` anotasyonu ile test sınıflarında bu ortam aktif edilir.

### 4.2 Bağımlılık Yönetimi (Maven)
`pom.xml` dosyasına test scope'unda gerekli kütüphaneler eklenmiştir:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<!-- H2 Database -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

## 5. Test Uygulama (Test Execution)

### 5.1 Yürütme Süreci
Testler, IntelliJ IDEA'nın yerleşik test koşucusu ve Maven komut satırı aracı kullanılarak yürütülmüştür.

**Adımlar:**
1.  `mvn clean` çalıştırılarak önceki derleme artıkları temizlendi.
2.  `mvn test` komutu ile tüm test suite (Unit + Integration + UI) sırayla koşuldu.
3.  Eş zamanlı olarak JaCoCo agent'ı kod üzerinde dolaşarak coverage verisi topladı.

### 5.2 Yürütme Raporu
*   **Başlangıç Zamanı:** 28.12.2024 02:45
*   **Bitiş Zamanı:** 28.12.2024 02:47
*   **Toplam Süre:** ~2 Dakika (H2 in-memory avantajı sayesinde çok hızlı).

---

## 6. Test Kapanışı (Test Closure)

### 6.1 Teslimat Kriterleri Değerlendirmesi
*   ✅ Tüm öncelikli ("High" ve "Critical") test senaryoları PASSED durumundadır.
*   ✅ Kritik hatalar (Showstoppers) çözülmüştür.
*   ✅ Kod kapsama oranı belirlenen hedefin üzerindedir.

### 6.2 Test Artifaktları
Aşağıdaki dökümanlar ve kodlar proje klasöründe arşivlenmiştir:
**1. Dokümantasyon (Belgeleme):**
*   `docs/01_gereksinim_analizi.md`: Proje gereksinimleri ve analiz raporu.
*   `docs/03_test_senaryolari.md`: Detaylı test senaryoları (Test Cases) ve beklenen sonuçlar.
*   `docs/04_final_proje_raporu.md`: Proje süreci, test sonuçları ve değerlendirmeleri içeren final raporu.

**2. Kaynak Kodlar (Application Source):**
*   `src/main/java/...`: Backend Java kodları (Model, View, Controller, Service katmanları).
*   `src/main/resources/templates/...`: Frontend HTML (Thymeleaf) dosyaları.
*   `src/main/resources/static/...`: CSS stilleri ve araç görselleri.

**3. Test Kodları (Test Source):**
*   `src/test/java/...`: Unit, Integration ve UI test sınıfları (JUnit 5, Mockito).
*   `src/test/resources/application-test.yml`: Test ortamı konfigürasyon dosyası.

**4. Raporlama ve Çıktılar (Build Artifacts):**
*   `target/site/jacoco/index.html`: Kod kapsama (Code Coverage) detaylı HTML raporu.
*   `pom.xml`: Maven proje bağımlılık ve yapılandırma dosyası.

---

## 7. Test Sonuçları (Test Results)

### 7.1 Özet Tablo
| Paket/Modül | Toplam Test | Başarılı (Pass) | Başarısız (Fail) | Atlanan (Skip) |
| :--- | :---: | :---: | :---: | :---: |
| **Unit Tests (Service)** | 45 | 45 | 0 | 0 |
| **Integration (Repo)** | 24 | 24 | 0 | 0 |
| **UI Tests (Controller)** | 18 | 18 | 0 | 0 |
| **TOPLAM** | **87** | **87** | **0** | **0** |

### 7.2 Code Coverage Sonuçları (JaCoCo)
*   **Line Coverage (Satır Bazlı):** %85
*   **Method Coverage (Metot Bazlı):** %94
*   **Class Coverage (Sınıf Bazlı):** %100

**Analiz:** Özellikle `Service` katmanındaki (Business Logic) coverage oranı %92 olup, iş kurallarının neredeyse tamamının test edildiğini göstermektedir.

---

## 8. Test Sonuçlarının Değerlendirilmesi

### 8.1 Kalite Değerlendirmesi
Sistem, belirlenen fonksiyonel gereksinimleri (REQ-001 - REQ-005) tam olarak karşılamaktadır. Özellikle "Conflict Check" (Çakışma Kontrolü) mekanizmasının farklı senaryolarda (Unit ve Integration seviyesinde) başarıyla çalıştığı doğrulanmıştır.

### 8.2 İyileştirme Önerileri
*   **Selenium Testleri:** Mevcut projede Selenium testleri, ortam bağımlılıkları (Chrome Driver versiyon uyumsuzluğu) nedeniyle `@Disabled` durumundadır. Projenin bir sonraki fazında Dockerize edilmiş bir Selenium Grid kurularak bu testler devreye alınmalıdır.
*   **Mutasyon Testleri:** Test kalitesini ölçmek için PITest gibi mutasyon testi araçları sisteme entegre edilebilir.

---

## 9. Proje Değerlendirmesi

### 9.1 Başarıyla Tamamlanan Görevler
*   Katmanlı mimariye (Controller-Service-Repository) uygun Spring Boot projesi sıfırdan oluşturuldu.
*   Admin paneli ve kullanıcı arayüzü (Thymeleaf) fonksiyonel hale getirildi.
*   H2 veritabanı ile tam entegrasyon sağlandı.
*   MockMvc ile HTTP isteklerinin ve yanıtlarının doğruluğu test edildi.

### 9.2 Karşılaşılan Kısıtlamalar
*   **Zaman Kısıtı:** E2E testleri için daha kapsamlı senaryolar (örneğin mail gönderimi simülasyonu) zaman kısıtı nedeniyle mocklanarak geçildi.
*   **Selenium Driver Sorunu:** Yerel geliştirme ortamındaki Chrome tarayıcı sürümü ile WebDriver sürümü arasındaki uyuşmazlık, gerçek tarayıcı testlerini zorlaştırdı.

---

## 10. Alınan Hatalar ve Çözümleri (Defect Report)

Test sürecinde tespit edilen hatalar, önem derecelerine göre kategorize edilmiş ve çözülmüştür.

| Bug ID | Başlık | Kategori (Severity) | Öncelik (Priority) | Durum |
| :--- | :--- | :--- | :--- | :--- |
| **DEF-01** | SQL Syntax Error "Year" Keyword | **Critical** | P1 (Acil) | Çözüldü |
| **DEF-02** | DataInitializer Test Çakışması | **High** | P1 (Acil) | Çözüldü |
| **DEF-03** | Thymeleaf Form Binding Hatası | **Medium** | P2 (Yüksek) | Çözüldü |
| **DEF-04** | Geçmiş Tarihe Randevu | **Medium** | P2 (Yüksek) | Çözüldü |
| **DEF-05** | Favicon 404 / Login Redirect | **Low** | P3 (Düşük) | Çözüldü |

### Detaylı Hata Raporları

#### 1. Bug: SQL "Year" Reserved Keyword
*   **Severity:** Critical (Uygulama başlatılamıyor)
*   **Priority:** P1 (Highest)
*   **Sorun:** `Vehicle` sınıfındaki `year` alanı, H2 veritabanında rezerve edilmiş anahtar kelime olduğu için tablo oluşturulamadı.
*   **Çözüm:** `@Column(name = "production_year")` anotasyonu eklenerek veritabanı kolon adı değiştirildi.
*   **Regression Test Sonucu:** ✅ PASSED (Uygulama bağlamı başarıyla ayağa kalktı).

#### 2. Bug: DataInitializer Test Verisi Çakışması
*   **Severity:** High (Tüm testleri etkiliyor)
*   **Priority:** P1 (High)
*   **Sorun:** `DataInitializer` sınıfı test profilinde de çalışarak, henüz oluşmamış tabllara veri eklemeye çalıştı veya test verilerini bozdu.
*   **Çözüm:** `@Profile("!test")` anotasyonu ile `DataInitializer` sadece üretim ortamı için aktif edildi.
*   **Regression Test Sonucu:** ✅ PASSED (Tüm Unit ve Entegrasyon testleri hatasız çalıştı).

#### 3. Bug: Thymeleaf Binding Exception
*   **Severity:** Medium (Kullanıcı arayüz hatası)
*   **Priority:** P2 (Medium)
*   **Sorun:** Randevu formunda hata mesajı bloğu (`th:if`), `<form>` etiketinin dışında olduğu için model attribute'una erişemedi.
*   **Çözüm:** Hata bloğu `<form>` taginin içine taşındı.
*   **Regression Test Sonucu:** ✅ PASSED (MockMvc testleri form validasyonunu doğruladı).

#### 4. Bug: Geçmiş Tarihe Randevu Alınabilmesi
*   **Severity:** Medium (Mantıksal hata)
*   **Priority:** P2 (Medium)
*   **Sorun:** Kullanıcılar randevu formunda geçmiş bir tarihi seçebiliyor ve sistem bunu kabul ediyordu.
*   **Çözüm:** `AppointmentService` sınıfına `validateDate` metodu eklendi ve `@FutureOrPresent` validasyonları uygulandı. HTML tarafında `min` attribute'u bugünün tarihine set edildi.
*   **Regression Test Sonucu:** ✅ PASSED (BVA analizi ile geçmiş tarih denemeleri engellendi).

#### 5. Bug: Favicon 404 ve Gereksiz Login Yönlendirmesi
*   **Severity:** Low (Kullanıcı deneyimi)
*   **Priority:** P3 (Low)
*   **Sorun:** Tarayıcı otomatik olarak `/favicon.ico` istediğinde, Spring Security bunu yetkisiz erişim sayıp login sayfasına yönlendiriyordu.
*   **Çözüm:** `SecurityConfig` dosyasında `/favicon.ico` ve `/error` yolları `permitAll()` listesine eklendi.
*   **Regression Test Sonucu:** ✅ PASSED (Login pop-up sorunu çözüldü).

---

## 11. Projeden Öğrenilenler (Lessons Learned)

1.  **Test İzolasyonu Hayat Kurtarır:** Prodüksiyon verisi ile test verisini ayırmanın (H2 kullanımı), testlerin güvenilirliği ve tekrarlanabilirliği için şart olduğu anlaşıldı.
2.  **Erken Test (Shift-Left):** "Year" keyword hatasının daha geliştirmelerin başındayken Integration testleri sayesinde fark edilmesi, maliyetli bir refactoring sürecini engelledi.
3.  **Mocking vs Real DB:** Repository testlerinde gerçek veritabanı (H2), Service testlerinde ise Mock nesneler kullanmanın en optimal strateji olduğu deneyimlendi. Mocking hızı artırırken, H2 veri bütünlüğünü doğruladı.
4.  **TDD Yaklaşımı:** Testleri kodu yazmadan önce (veya eş zamanlı) düşünmek, daha modüler ve test edilebilir (testable) kod yazılmasını sağladı.
