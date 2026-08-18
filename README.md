# DuoSpace

DuoSpace, iki kişinin ortak film listesi hazırlayabilmesi için yaptığım Spring Boot projesi. Kullanıcılar kayıt olur, kendi odasını oluşturur veya davet koduyla odaya katılır. Aynı odadaki kişiler ortak listeyi birlikte yönetir.

## Şu an neler var?

- Kayıt olma ve giriş yapma
- JWT ile korunan endpointler
- Oda oluşturma, davet kodu ile odaya katılma
- Ortak izleme listesine ekleme, listeleme, güncelleme ve silme
- TMDB üzerinden film arama
- Swagger ile API dokümantasyonu
- PostgreSQL, Flyway ve Docker Compose kurulumu

## Teknolojiler

- Java 21, Spring Boot
- Spring Security, Spring Data JPA, Validation
- PostgreSQL ve Flyway
- Springdoc OpenAPI / Swagger
- Docker Compose
- HTML, CSS, JavaScript

## Hızlı başlangıç

Önce `.env.example` dosyasını kopyalayıp proje kökünde `.env` oluştur:

```env
TMDB_API_READ_ACCESS_TOKEN=buraya-tmdb-read-token
```

TMDB token olmadan uygulama çalışır; sadece film araması sonuç vermez.

Sonra Docker Desktop açıkken:

```bash
docker compose up --build -d
```

- Uygulama: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui/api-docs.html`

Kapatmak için:

```bash
docker compose down
```

## Test

Film arama servisinin TMDB'den gelen veriyi doğru dönüştürdüğünü, gerçek TMDB'ye bağlanmadan test ediyoruz. Test sırasında lokal bir sahte TMDB cevabı kullanılıyor.

```bash
./mvnw test
```

## Uygulama akışı

1. Ana sayfadan kayıt ol veya giriş yap.
2. Yeni oda oluştur ya da gelen davet kodunu kullan.
3. Ortak liste kartındaki `+` butonuna bas.
4. TMDB'de bir film ara, sonucu seç ve listeye ekle.
5. Eklenen kayıt PostgreSQL içindeki `watchlist_items` tablosunda tutulur.

## API kısa özeti

| İş | Endpoint |
| --- | --- |
| Kayıt ol | `POST /api/v1/auth/register` |
| Giriş yap | `POST /api/v1/auth/login` |
| Oda işlemleri | `/api/v1/rooms` |
| Odaya katıl | `POST /api/v1/rooms/join` |
| Ortak liste | `/api/v1/rooms/{roomId}/watchlist` |
| Film ara | `GET /api/v1/movies/search?query=...` |

`/auth/**` dışındaki endpointler JWT ister. Swagger'da önce giriş endpointini çalıştırıp dönen `accessToken` değerini `Authorize` alanına ekleyebilirsin.

## Güvenlik notları

- Kullanıcı şifreleri BCrypt ile saklanır.
- JWT ile kimlik doğrulama yapılır; oda ve liste işlemlerinde üyelik kontrol edilir.
- TMDB token sadece backend environment variable'ında bulunur. `.env` dosyası Git'e eklenmez.
- Film arama endpointi de JWT olmadan çağrılamaz.

## Klasör yapısı

```text
frontend/                 Arayüz dosyaları
src/main/java/
  config/                 JWT, Swagger ve dış servis ayarları
  controller/             HTTP endpointleri
  dto/                    Request ve response sınıfları
  entity/                 Veritabanı modelleri
  repository/             Veritabanı sorguları
  service/abs/            Service interface'leri
  service/impl/           Service implementasyonları
  exception/              Hata cevapları
src/main/resources/db/migration/  Flyway migration dosyaları
```

## Sonraki işler

- Watchlist kartlarında film afişi ve izleme durumu arayüzü
- Chat için WebSocket
- Aynı anda izleme senkronu
- Refresh token ve şifre yenileme
- CI/CD ve deploy
