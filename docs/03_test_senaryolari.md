# Test Senaryoları (Test Cases)

**Proje:** DriveLab - Test Sürüşü Randevu Sistemi

Bu doküman, sistemin doğrulanması için yürütülen detaylı test senaryolarını içerir.

## 1. Randevu Modülü Testleri

| Test Case ID | Senaryo Tanımı | Girdi Verileri (Input) | Beklenen Sonuç (Expected) | Test Tipi |
| :--- | :--- | :--- | :--- | :--- |
| **TC-01** | Geçerli Randevu Oluşturma | Araç: BMW M3, İsim: Baran, Tel: 555..., Tarih: Yarın, Saat: 14:00 | Sistem randevuyu kaydeder ve başarı sayfasına yönlendirir. Durum: PENDING. | Positive |
| **TC-02** | Geçmiş Tarihe Randevu | Tarih: Dün | Sistem "Geçmiş bir tarihe randevu alamazsınız" hatası verir. | Negative |
| **TC-03** | Boş Form Gönderimi | Tüm alanlar boş | Sistem "Zorunlu alan" uyarılarını gösterir, kayıt yapmaz. | Negative |
| **TC-04** | Çakışan Randevu | Aynı araç, aynı tarih, aynı saat (Daha önce alınmış) | Sistem "Bu saatte araç doludur" hatası verir. | Negative |
| **TC-05** | Mesaiden Önce Randevu | Saat: 08:59 | Sistem "Randevular 09:00 - 18:00 arasıdır" hatası verir. | Boundary |
| **TC-06** | Mesaiden Sonra Randevu | Saat: 18:01 | Sistem "Randevular 09:00 - 18:00 arasıdır" hatası verir. | Boundary |

## 2. Araç Modülü Testleri

| Test Case ID | Senaryo Tanımı | Girdi Verileri (Input) | Beklenen Sonuç (Expected) | Test Tipi |
| :--- | :--- | :--- | :--- | :--- |
| **TC-07** | Araç Listeleme | URL: /vehicles | Tüm araçlar (BMW, Porsche, vs.) listede görünür. | Positive |
| **TC-08** | Olmayan Araç Detayı | URL: /vehicle/999 | Sistem 404 sayfasına veya uygun bir hata sayfasına yönlendirir. | Negative |
| **TC-09** | Araç Ekleme (Admin) | Marka: Audi, Model: A5, Yıl: 2023 | Yeni araç veritabanına eklenir ve listede görünür. | Positive |
| **TC-10** | Araç Silme (Admin) | Mevcut bir araç ID | Araç veritabanından silinir. (Randevusu yoksa) | Positive |

## 3. Yönetim Paneli Testleri

| Test Case ID | Senaryo Tanımı | Girdi Verileri (Input) | Beklenen Sonuç (Expected) | Test Tipi |
| :--- | :--- | :--- | :--- | :--- |
| **TC-11** | Randevu Onaylama | Bekleyen bir randevu | Randevu durumu 'CONFIRMED' olarak güncellenir. | Positive |
| **TC-12** | Randevu İptal Etme | Bekleyen bir randevu | Randevu durumu 'CANCELLED' olarak güncellenir. | Positive |
