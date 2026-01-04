# Gereksinim Analizi Raporu

**Proje:** DriveLab - Test Sürüşü Randevu Sistemi  

## 1. Giriş
Bu belge, DriveLab projesinin işlevsel ve işlevsel olmayan gereksinimlerini tanımlar. Sistem, kullanıcıların araçları inceleyip test sürüşü randevusu almasını ve yöneticilerin bu süreci takip etmesini sağlar.

## 2. İşlevsel Gereksinimler (Functional Requirements)

### 2.1. Araç İşlemleri
*   **REQ-01:** Kullanıcılar sistemdeki tüm araçları listeleyebilmelidir.
*   **REQ-02:** Araç detaylarında marka, model, yıl, resim ve açıklama bilgileri bulunmalıdır.
*   **REQ-03:** Yöneticiler yeni araç ekleyebilmeli, güncelleyebilmeli ve silebilir.

### 2.2. Randevu İşlemleri
*   **REQ-04:** Kullanıcılar seçtikleri bir araç için randevu formu doldurabilmelidir.
*   **REQ-05:** Randevu formunda İsim, Telefon, Tarih ve Saat bilgileri zorunludur.
*   **REQ-06:** Kullanıcı geçmiş bir tarihe randevu alamamalıdır.
*   **REQ-07:** Aynı araç için aynı saatte mükerrer randevu oluşturulamaz.

### 2.3. Yönetim Paneli
*   **REQ-08:** Yöneticiler bekleyen randevuları listeleyebilmelidir.
*   **REQ-09:** Yöneticiler randevuları "Onayla" veya "İptal Et" durumuna getirebilmelidir.

## 3. İşlevsel Olmayan Gereksinimler (Non-Functional Requirements)

### 3.1. Performans ve Güvenilirlik
*   **NFR-01:** Sayfa yüklenme süreleri 3 saniyenin altında olmalıdır.
*   **NFR-02:** Veritabanı tutarlılığı (ACID) tam olarak sağlanmalıdır.

### 3.2. Kullanılabilirlik (Usability)
*   **NFR-03:** Arayüz modern, duyarlı (responsive) ve kullanıcı dostu olmalıdır ('Night Drive' teması).
*   **NFR-04:** Hata mesajları kullanıcıya anlaşılır bir dilde gösterilmelidir.

### 3.3. Güvenlik
*   **NFR-05:** Admin paneline sadece yetkili erişim olmalıdır (Simülasyon ortamında URL bazlı ayrım).
