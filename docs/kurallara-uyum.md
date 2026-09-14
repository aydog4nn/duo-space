# Kurallara uyum çalışması

## 1. JWT filtresinde hata ayrımı — 14 Eylül 2026

Amaç: Geçersiz erişim tokenı 401 döndürmeli; sonraki filtre veya uygulama bileşeninin hatası JWT filtresi tarafından yakalanmamalı.

`JwtAuthenticationFilter` içindeki `try/catch` sadece token doğrulamasını kapsıyor. Kimliğin güvenlik bağlamına yerleştirilmesi ve filtre zincirinin devamı bu bloğun dışında. Endpoint yetkileri ve token süresi değiştirilmedi.

### Proje bağlamı

- Bu adımın kapsamı Spring Boot 4.1.0, Java 21 hedefi ve mevcut JWT filtresi. Yeni bağımlılık eklenmedi.
- Testler `src/test/java` altında; çıktı `target/surefire-reports` altında oluşuyor.
- Doğrulama yerel Maven 3.9.16 ile yapıldı. Gerçek JWT imzalama kullanıldı; HTTP request/response nesneleri Spring test nesneleri. Veritabanına, TMDB'ye veya çalışan sunucuya istek gönderilmedi.
- Kullanılan kurallar: `MASTER_AI_RULES`, `PROJECT_CONTEXT`, `AI_CODING_RULES`, `AI_SECURITY_RULES`, `AI_AUTH_SECURITY`, `API_SECURITY`, `TESTING_RULES`, `CODE_REVIEW`. Kaynak paket: kullanıcının `ai-rules` klasörü.
- Mobil, ödeme, yayın ve WebSocket davranışları bu değişikliğin kapsamında değil. Bu kayıt bütün projenin kurallara uyduğunu göstermiyor.

### Doğrulama

| Kural / senaryo | Sonuç | Kanıt | Kalan işlem / sorumlu |
| --- | --- | --- | --- |
| TEST-03: Önce hatayı gösterme | GEÇTİ | Düzeltme öncesi 6 filtre testinin 2'si beklenen hata yakalanmadığı için başarısız oldu | Yok |
| CODE-05: Hataları yanlış sınıflandırmama | GEÇTİ | Düzeltme sonrası sonraki bileşenden gelen `IllegalArgumentException` ve `JwtException` aynı nesne olarak dışarı aktarılıyor | Yok |
| Token doğrulama regresyonu | GEÇTİ | Geçerli, bozuk ve süresi dolmuş token ile eksik header senaryoları; toplam 7 JWT testi geçti | Yok |
| TEST-04: Gerçek HTTP yetki matrisi | DOĞRULANAMADI | Bu adımda yalnızca filtre ve JWT servis testleri çalıştırıldı | Ayrı API entegrasyon testleri gerekli |
| TEST-15: Bağımsız güvenlik incelemesi | DOĞRULANAMADI | Henüz insan incelemesi yapılmadı | Birleştirmeden önce geliştirici incelemesi |

Komut: `mvn -Dtest=JwtAuthenticationFilterTest,JwtServiceTest test`.

Windows Maven wrapper bu ortamda `Cannot index into a null array` hatasıyla başlayamadığı için bilgisayarda bulunan Maven 3.9.16 çalıştırıldı. Wrapper değiştirilmedi. Tüm test paketi ve uçtan uca akış çalıştırılmadı; deploy yapılmadı.
