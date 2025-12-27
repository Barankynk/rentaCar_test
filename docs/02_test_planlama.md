# Test Planlama Dokümanı

**Proje:** Test Sürüşü Randevu Yönetim Sistemi  
**Versiyon:** 1.0  
**Tarih:** 27 Aralık 2024

---

## 1. Test Stratejisi

### 1.1 Test Yaklaşımı
Bu projede **hibrit test yaklaşımı** uygulanacaktır:
- Otomatik testler (Unit, Integration, UI)
- Manuel testler (Keşif testleri)

### 1.2 Test Seviyeleri

| Seviye | Açıklama | Araç | Kapsam |
|--------|----------|------|--------|
| **Unit Test** | Bireysel methodların testi | JUnit 5 + Mockito | Service katmanı |
| **Integration Test** | Birden fazla bileşenin birlikte testi | Spring Test | Repository + DB |
| **UI Test** | Kullanıcı arayüzü testleri | MockMvc + Selenium | Controller + View |
| **End-to-End Test** | Tam kullanıcı akışı | Spring Boot Test | Tüm katmanlar |
| **Regression Test** | Değişiklik sonrası tüm testler | JUnit Suite | Tüm testler |

---

## 2. Test Araçları

| Araç | Versiyon | Kullanım Amacı |
|------|----------|----------------|
| **JUnit 5** | 5.10.x | Test framework |
| **Mockito** | 5.x | Mock objeler oluşturma |
| **Spring Test** | 3.2.x | Spring context testleri |
| **MockMvc** | Built-in | Controller testleri |
| **Selenium WebDriver** | 4.x | Tarayıcı otomasyon testleri |
| **JaCoCo** | 0.8.x | Code coverage raporu |
| **H2 Database** | 2.x | Test veritabanı |

---

## 3. Test Ortamı

### 3.1 Ortam Konfigürasyonu

| Özellik | Değer |
|---------|-------|
| JDK | 17 |
| Build Tool | Maven |
| Database | H2 (in-memory) |
| Server | Embedded Tomcat |
| Profile | test |

### 3.2 Ortam İzolasyonu
- **application-test.yml** ile ayrı konfigürasyon
- H2 in-memory DB ile izole test ortamı
- Her test öncesi DB temizleme (@Transactional rollback)

---

## 4. Test Tasarım Teknikleri

| Teknik | Açıklama | Uygulama Alanı |
|--------|----------|----------------|
| **Equivalence Partitioning** | Girdileri geçerli/geçersiz gruplara ayırma | Tarih, saat validasyonu |
| **Boundary Value Analysis** | Sınır değerlerini test etme | Saat aralıkları (09:00, 18:00) |
| **Decision Table** | Koşul kombinasyonları | Randevu oluşturma kuralları |
| **Positive Testing** | Beklenen girdilerle test | Normal akış testleri |
| **Negative Testing** | Hatalı girdilerle test | Hata senaryoları |

---

## 5. Test Kapsamı (Scope)

### 5.1 Kapsam Dahilinde (In Scope)
- ✅ Araç listeleme fonksiyonu
- ✅ Randevu CRUD işlemleri
- ✅ Form validasyonları
- ✅ Admin panel işlevleri
- ✅ Çakışma kontrolü
- ✅ Tarih/saat validasyonu

### 5.2 Kapsam Dışında (Out of Scope)
- ❌ Ödeme işlemleri
- ❌ E-posta bildirimleri
- ❌ Kullanıcı kayıt/giriş
- ❌ Performans/yük testleri

---

## 6. Test Giriş/Çıkış Kriterleri

### 6.1 Giriş Kriterleri (Entry Criteria)
- [ ] Kod tamamlanmış olmalı
- [ ] Build başarılı olmalı
- [ ] Test ortamı hazır olmalı
- [ ] Test verileri hazır olmalı

### 6.2 Çıkış Kriterleri (Exit Criteria)
- [ ] Tüm planlanmış testler çalıştırılmış olmalı
- [ ] Kritik hatalar düzeltilmiş olmalı
- [ ] Code coverage ≥ %80 olmalı
- [ ] Regression testleri geçmiş olmalı

---

## 7. Test Tipleri ve Dağılımı

```
Test Piramidi:

        ┌─────────┐
        │   E2E   │  ← 2-3 test (slow)
        │  Tests  │
        ├─────────┤
        │   UI    │  ← 5-8 test (medium)
        │  Tests  │
        ├─────────┤
        │ Integr. │  ← 8-10 test (medium)
        │  Tests  │
        ├─────────┤
        │  Unit   │  ← 15-20 test (fast)
        │  Tests  │
        └─────────┘
```

---

## 8. Risk Bazlı Test Önceliklendirme

| Özellik | Risk Seviyesi | Test Önceliği | Test Sayısı |
|---------|---------------|---------------|-------------|
| Çakışma kontrolü | Yüksek | P1 | 5+ |
| Form validasyonu | Yüksek | P1 | 5+ |
| Tarih kontrolü | Yüksek | P1 | 3+ |
| Araç listeleme | Orta | P2 | 2-3 |
| Admin panel | Orta | P2 | 3-4 |
| Randevu iptal | Düşük | P3 | 2 |

---

## 9. Rol ve Sorumluluklar

| Rol | Sorumluluk |
|-----|------------|
| Developer | Unit test yazma, bug fix |
| Tester (Sen) | Tüm test seviyelerini yazma ve çalıştırma |
| Reviewer | Code review, test review |

---

## 10. Çıktılar (Deliverables)

- [ ] Test senaryoları dokümanı
- [ ] Unit test class'ları
- [ ] Integration test class'ları  
- [ ] UI test class'ları
- [ ] E2E test class'ları
- [ ] JaCoCo coverage raporu
- [ ] Test sonuç raporu
