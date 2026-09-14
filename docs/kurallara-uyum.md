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

## 2. Oda ve film kaydının eşleşmesi — 14 Eylül 2026

Önceki davranış: Güncelleme ve silmede URL'deki `roomId` servise aktarılmıyordu. Kaydın gerçek odasına üyelik kontrolü vardı; fakat iki odaya üye kullanıcı yanlış odanın URL'si üzerinden işlem yapabiliyordu. Bu bulgu, üyesi olunmayan tüm odalara erişilebildiği anlamına gelmiyor.

Yeni davranış: Controller `roomId` bilgisini servise aktarıyor. Servis önce bu odaya üyeliği doğruluyor (403), ardından kayıt ile oda eşleşmesini kontrol ediyor (eşleşmiyorsa veya kayıt yoksa 404). Güncelleme ve silme aynı `findItem` kontrolünü kullanıyor. Şema, bağımlılıklar ve frontend değiştirilmedi.

| Kural / senaryo | Sonuç | Kanıt | Kalan işlem / sorumlu |
| --- | --- | --- | --- |
| TEST-03: Hatanın tekrarı | GEÇTİ | İlk çalıştırmada 8 testin 2'si başarısız: yanlış oda URL'si PUT için 200, DELETE için 204 döndürdü | Yok |
| AUTH-01, SEC-11, API-01: Oda–kayıt sınırı | GEÇTİ | Düzeltme sonrası PUT/DELETE için yanlış oda 404, oda dışındaki kullanıcı 403, olmayan kayıt 404; reddedilen işlemlerde kayıt değişmedi ve silme çağrılmadı | Mock repository sınırı geçerli |
| Yetkili üye | GEÇTİ | Aynı odadaki kayıt güncellendi (200) ve silme çağrıldı (204) | Gerçek DB sonucu ayrıca doğrulanmalı |
| Regresyon | GEÇTİ | Maven 3.9.16 ile 18 test, 0 hata, 0 atlama | Yok |
| TEST-04: Gerçek kimlik ve veritabanı ile rol matrisi | DOĞRULANAMADI | MockMvc standalone kullanıldı; SecurityFilterChain ve PostgreSQL başlatılmadı. Kimlik ve repository sonuçları test verisidir | Ayrı entegrasyon adımı; anonim ve gerçek A/B oturumları dahil |
| TEST-15: Bağımsız inceleme | DOĞRULANAMADI | İnsan incelemesi yapılmadı | Birleştirmeden önce geliştirici incelemesi |

Komut: `mvn -Dtest=WatchlistControllerTest,JwtAuthenticationFilterTest,JwtServiceTest,RoomServiceImplTest test`.

Mockito dinamik agent uyarısı verdi; testler başarısız olmadı. Uyarıyı susturmak için ayar değiştirilmedi. Bu adım yerel kod ve test değişikliğidir; push, merge veya deploy yapılmadı.

## 3. Film bağlantısının doğrulanması — 14 Eylül 2026

Önceki davranış: `sourceUrl` için yalnızca uzunluk kontrolü vardı. Yeni davranış: `@WebLink` ekleme ve güncelleme DTO'larında aynı doğrulamayı kullanıyor. Alan isteğe bağlı; dolu adresin protokolü HTTP/HTTPS olmalı, host içermeli, kullanıcı bilgisi içermemeli ve portu geçerli aralıkta olmalı. URI ayrıştırması ağ isteği yapmadan çalışıyor. Yeni bağımlılık veya migration eklenmedi.

| Kural / senaryo | Sonuç | Kanıt | Kalan işlem / sorumlu |
| --- | --- | --- | --- |
| TEST-03: Hatanın tekrarı | GEÇTİ | Düzeltmeden önce 19 doğrulama testinin 14'ü başarısız oldu; reddedilmesi beklenen adresler kabul ediliyordu | Yok |
| SEC-05, CODE-05: Giriş sınırı | GEÇTİ | DTO doğrulamasında 19 senaryo; POST/PUT için geçersiz protokol 400 döndü, repository çağrılmadı | Yok |
| Regresyon | GEÇTİ | İlgili 39 test geçti; veritabanı ve dış ağ kullanılmadı | Tam entegrasyon testleri ayrı |
| API-05: Sunucu tarafı URL erişimi | N/A | Bu alan yalnızca kaydediliyor, sunucu bağlantıyı açmıyor | İleride URL fetch eklenirse SSRF savunması ayrıca gerekli |
| SEC-13: Eski kayıtların frontend'de gösterimi | DOĞRULANAMADI | Frontend değiştirilmedi; eski veriler taranmadı veya silinmedi | Sonraki adımda link gösteriminde protokol kontrolü |
| TEST-15: Bağımsız inceleme | DOĞRULANAMADI | İnsan incelemesi yapılmadı | Birleştirmeden önce geliştirici incelemesi |

Komut: `mvn -Dtest=WatchlistRequestValidationTest,WatchlistControllerTest,JwtAuthenticationFilterTest,JwtServiceTest,RoomServiceImplTest test` (yerel Maven 3.9.16).

Bu kontrol zararlı siteleri tespit eden bir itibar filtresi değildir. Geçersiz eski adres içeren kayıtlar güncellenirken adresin düzeltilmesi veya kaldırılması gerekir. Üretim yayını, push ve merge yapılmadı.
