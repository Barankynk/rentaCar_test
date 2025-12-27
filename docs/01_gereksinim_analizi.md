# Gereksinim Analizi Dokümanı

**Proje:** Test Sürüşü Randevu Yönetim Sistemi  
**Versiyon:** 1.0  
**Tarih:** 27 Aralık 2024

---

## 1. Proje Tanımı

### 1.1 Amaç
Bu sistem, araç kiralama/satış şirketlerinin müşterilerine test sürüşü randevusu vermesini sağlayan bir web uygulamasıdır.

### 1.2 Kapsam
- Araç listeleme
- Randevu oluşturma/iptal etme
- Admin panel ile randevu yönetimi

### 1.3 Hedef Kullanıcılar
| Kullanıcı Tipi | Açıklama |
|----------------|----------|
| Müşteri | Araç listeler, randevu alır, randevusunu iptal eder |
| Admin | Tüm randevuları görür, yönetir |

---

## 2. Fonksiyonel Gereksinimler

| ID | Gereksinim | Öncelik | Açıklama |
|----|------------|---------|----------|
| FR-01 | Araç Listeleme | Yüksek | Kullanıcı mevcut araçları liste olarak görüntüleyebilmeli |
| FR-02 | Randevu Oluşturma | Yüksek | Kullanıcı seçtiği araç için tarih ve saat seçerek randevu alabilmeli |
| FR-03 | Çakışma Kontrolü | Yüksek | Aynı araç, aynı tarih, aynı saat için ikinci randevu alınamamalı |
| FR-04 | Randevu İptali | Orta | Kullanıcı kendi randevusunu iptal edebilmeli |
| FR-05 | Randevu Görüntüleme | Orta | Kullanıcı kendi randevularını listeleyebilmeli |
| FR-06 | Admin Panel | Yüksek | Admin tüm randevuları görüntüleyebilmeli |
| FR-07 | Admin Yetkilendirme | Orta | Admin paneline basit HTTP Basic Auth ile erişim |

---

## 3. Fonksiyonel Olmayan Gereksinimler

| ID | Gereksinim | Kategori | Açıklama |
|----|------------|----------|----------|
| NFR-01 | Performans | Performance | Sayfa yüklenme süresi < 3 saniye |
| NFR-02 | Eşzamanlılık | Scalability | Sistem aynı anda en az 10 kullanıcıyı desteklemeli |
| NFR-03 | Validasyon | Usability | Form hataları kullanıcıya anlaşılır mesajlarla gösterilmeli |
| NFR-04 | Test Edilebilirlik | Maintainability | Kod %80+ test coverage'a sahip olmalı |
| NFR-05 | Güvenlik | Security | Admin paneli korumalı olmalı |
| NFR-06 | Uyumluluk | Compatibility | Modern tarayıcılarda (Chrome, Firefox, Edge) çalışmalı |

---

## 4. İş Kuralları

| ID | Kural | Açıklama |
|----|-------|----------|
| BR-01 | Randevu Saatleri | Randevular sadece 09:00 - 18:00 arası alınabilir |
| BR-02 | Randevu Tarihi | Geçmiş tarihe randevu alınamaz |
| BR-03 | Müşteri Bilgileri | Ad-soyad ve telefon zorunlu, e-posta opsiyonel |
| BR-04 | Tek Randevu | Bir araç için aynı tarih ve saatte sadece 1 randevu olabilir |

---

## 5. Risk Analizi

| Risk ID | Risk | Olasılık | Etki | Risk Skoru | Önlem |
|---------|------|----------|------|------------|-------|
| R-01 | Aynı saate çift randevu | Orta | Yüksek | **Yüksek** | DB unique constraint + validation test |
| R-02 | UI form hataları | Yüksek | Orta | **Yüksek** | UI testleri (MockMvc + Selenium) |
| R-03 | Geçersiz veri girişi | Yüksek | Orta | **Yüksek** | Bean Validation + unit test |
| R-04 | Admin paneline yetkisiz erişim | Düşük | Yüksek | **Orta** | HTTP Basic Auth |
| R-05 | Performans sorunları | Düşük | Orta | **Düşük** | H2 in-memory DB kullanımı |

### Risk Matrisi

```
         │ Düşük Etki │ Orta Etki │ Yüksek Etki │
─────────┼────────────┼───────────┼─────────────┤
Yüksek   │            │ R-02, R-03│             │
Olasılık │            │           │             │
─────────┼────────────┼───────────┼─────────────┤
Orta     │            │           │ R-01        │
Olasılık │            │           │             │
─────────┼────────────┼───────────┼─────────────┤
Düşük    │            │ R-05      │ R-04        │
Olasılık │            │           │             │
```

---

## 6. Use Case Diyagramı (Metin)

```
┌─────────────────────────────────────────────────────────┐
│                    TEST SÜRÜŞÜ SİSTEMİ                  │
│                                                         │
│  ┌──────┐                           ┌──────┐           │
│  │Müşteri│                           │Admin │           │
│  └───┬───┘                           └───┬───┘          │
│      │                                   │              │
│      ├──→ [Araçları Listele]             │              │
│      │                                   │              │
│      ├──→ [Randevu Oluştur]              │              │
│      │         │                         │              │
│      │         └──→ [Çakışma Kontrolü]   │              │
│      │                                   │              │
│      ├──→ [Randevularımı Gör]            │              │
│      │                                   │              │
│      ├──→ [Randevu İptal]                │              │
│      │                                   │              │
│      │                    ├──→ [Tüm Randevuları Gör]    │
│      │                    │                             │
│      │                    ├──→ [Randevu Yönet]          │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

## 7. Kabul Kriterleri

| ID | Kriter | Doğrulama Yöntemi |
|----|--------|-------------------|
| AC-01 | Kullanıcı araç listesini görebilmeli | UI Test |
| AC-02 | Geçerli bilgilerle randevu oluşturulabilmeli | Integration Test |
| AC-03 | Çakışan randevu reddedilmeli | Unit Test |
| AC-04 | Geçmiş tarih reddedilmeli | Unit Test |
| AC-05 | Admin tüm randevuları görebilmeli | UI Test |
| AC-06 | Test coverage ≥ %80 | JaCoCo Report |
