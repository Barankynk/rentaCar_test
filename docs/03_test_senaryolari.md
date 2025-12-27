# Test Senaryoları Dokümanı

**Proje:** Test Sürüşü Randevu Yönetim Sistemi  
**Versiyon:** 1.0  
**Tarih:** 28 Aralık 2024

---

## 1. Test Özeti

| Test Seviyesi | Test Dosyası | Test Sayısı |
|---------------|--------------|-------------|
| Unit Test | AppointmentServiceTest.java | 17 |
| Unit Test | VehicleServiceTest.java | 11 |
| Integration Test | AppointmentRepositoryIntegrationTest.java | 8 |
| Integration Test | VehicleRepositoryIntegrationTest.java | 10 |
| Integration Test | ServiceIntegrationTest.java | 8 |
| UI Test (MockMvc) | MockMvcUITest.java | 18 |
| UI Test (Selenium) | SeleniumUITest.java | 8 |
| E2E Test | AppointmentFlowE2ETest.java | 6 |
| **TOPLAM** | **8 dosya** | **86+ test** |

---

## 2. Test Teknikleri Kullanımı

### 2.1 Boundary Value Analysis (BVA)

| Test ID | Sınır | Değer | Beklenen Sonuç |
|---------|-------|-------|----------------|
| BVA-01 | Saat alt sınır | 09:00 | ✅ Geçerli |
| BVA-02 | Saat üst sınır | 18:00 | ✅ Geçerli |
| BVA-03 | Saat alt sınır altı | 08:59 | ❌ InvalidTimeException |
| BVA-04 | Saat üst sınır üstü | 18:01 | ❌ InvalidTimeException |
| BVA-05 | Saat orta değer | 12:00 | ✅ Geçerli |

### 2.2 Equivalence Partitioning (EP)

| Test ID | Sınıf | Örnek Değer | Beklenen Sonuç |
|---------|-------|-------------|----------------|
| EP-01 | Geçerli tarih (gelecek) | Bugün + 7 gün | ✅ Kabul |
| EP-02 | Geçerli tarih (bugün) | Bugün | ✅ Kabul |
| EP-03 | Geçersiz tarih (dün) | Bugün - 1 gün | ❌ PastDateException |
| EP-04 | Geçersiz tarih (geçmiş hafta) | Bugün - 7 gün | ❌ PastDateException |

---

## 3. Unit Test Senaryoları

### 3.1 AppointmentService

| ID | Senaryo | Tip | Teknik | Beklenen |
|----|---------|-----|--------|----------|
| UT-01 | Geçerli randevu oluşturma | Pozitif | - | Başarılı kayıt |
| UT-02 | Tüm randevuları listeleme | Pozitif | - | Liste dönmeli |
| UT-03 | ID ile randevu getirme | Pozitif | - | Randevu dönmeli |
| UT-04 | Randevu iptal etme | Pozitif | - | Status: CANCELLED |
| UT-05 | Randevu onaylama | Pozitif | - | Status: CONFIRMED |
| UT-06 | Geçmiş tarih kontrolü | Negatif | EP | PastDateException |
| UT-07 | Çakışma kontrolü | Negatif | - | ConflictException |
| UT-08 | Olmayan ID | Negatif | - | NotFoundException |

### 3.2 VehicleService

| ID | Senaryo | Tip | Beklenen |
|----|---------|-----|----------|
| VS-01 | Tüm araçları listeleme | Pozitif | Liste dönmeli |
| VS-02 | Boş liste durumu | Pozitif | Boş liste |
| VS-03 | Mevcut araçları listeleme | Pozitif | Sadece available=true |
| VS-04 | ID ile araç getirme | Pozitif | Araç dönmeli |
| VS-05 | Olmayan ID | Negatif | NotFoundException |

---

## 4. Integration Test Senaryoları

### 4.1 AppointmentRepository

| ID | Senaryo | Test |
|----|---------|------|
| IT-01 | Randevu kaydetme | save() + ID check |
| IT-02 | Çakışma kontrolü (var) | existsBy... = true |
| IT-03 | Çakışma kontrolü (yok) | existsBy... = false |
| IT-04 | Telefon ile arama | findByCustomerPhone() |
| IT-05 | Durum ile arama | findByStatus() |

### 4.2 VehicleRepository

| ID | Senaryo | Test |
|----|---------|------|
| VIT-01 | Araç kaydetme | save() + ID check |
| VIT-02 | ID ile getirme | findById() |
| VIT-03 | Marka ile arama | findByBrand() |
| VIT-04 | Available filtreleme | findByAvailableTrue() |

---

## 5. UI Test Senaryoları

### 5.1 MockMvc Testleri

| ID | Sayfa | Senaryo | Assert |
|----|-------|---------|--------|
| UI-01 | Ana sayfa | Yükleme | status 200, view "index" |
| UI-02 | Ana sayfa | Araç listesi | model contains vehicles |
| UI-06 | Randevu formu | Yükleme | form + vehicles + times |
| UI-08 | Randevu formu | Geçerli submit | redirect /success |
| UI-09 | Randevu formu | Boş ad | validation error |
| UI-10 | Randevu formu | Geçersiz telefon | validation error |
| UI-15 | Admin panel | Auth gerekliliği | status 401 |
| UI-16 | Admin panel | Yetkili erişim | status 200 |

### 5.2 Selenium Testleri

| ID | Senaryo | Test |
|----|---------|------|
| SEL-01 | Ana sayfa | Başlık görünürlüğü |
| SEL-02 | Navigasyon | Randevu Al butonu |
| SEL-04 | Form | Doldur ve gönder |
| SEL-05 | Form | Validasyon hataları |

---

## 6. E2E Test Senaryoları

| ID | Akış | Adımlar |
|----|------|---------|
| E2E-01 | Tam Randevu Akışı | Ana Sayfa → Araç Seç → Form Doldur → Submit → Başarı |
| E2E-02 | Randevularım | Randevu Oluştur → Telefon ile Ara → Liste Gör |
| E2E-03 | İptal Akışı | Randevu Oluştur → İptal Et → Status Kontrol |
| E2E-04 | Admin Akışı | Randevu Oluştur → Admin Panel → Onayla |
| E2E-05 | Çakışma | İlk Randevu → Aynı Slot → Hata |
| E2E-06 | Çoklu Randevu | Aynı Gün Farklı Saatler → Başarı |

---

## 7. Test Çalıştırma Komutları

```bash
# Tüm testleri çalıştır
mvn test

# Sadece unit testleri
mvn test -Dtest=*ServiceTest

# Sadece integration testleri
mvn test -Dtest=*IntegrationTest

# JaCoCo coverage raporu
mvn test jacoco:report
# Rapor: target/site/jacoco/index.html

# Belirli bir test sınıfı
mvn test -Dtest=AppointmentServiceTest
```

---

## 8. Coverage Hedefi

| Metrik | Hedef | Durum |
|--------|-------|-------|
| Line Coverage | ≥ 80% | 🎯 Hedef |
| Branch Coverage | ≥ 70% | 🎯 Hedef |
| Class Coverage | ≥ 90% | 🎯 Hedef |
