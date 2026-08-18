# DuoSpace

DuoSpace, iki kullanıcının davet kodu ile aynı odaya bağlanıp ortak bir izleme listesi yönetmesi için geliştirilmiş containerized bir Spring Boot REST API projesidir. Film arama verisi TMDB'den alınır; kullanıcı, oda ve izleme listesi verileri PostgreSQL'de tutulur.

## Teknik özet

- Java 21, Spring Boot 4
- Spring Web MVC, Spring Data JPA, Bean Validation
- Spring Security + stateless JWT authentication
- PostgreSQL 17, Flyway migration
- Springdoc OpenAPI / Swagger UI
- Docker ve Docker Compose
- TMDB REST API entegrasyonu (`RestClient`)
- Vanilla HTML/CSS/JavaScript istemcisi

## Mimari

Uygulama katmanlı yapı ile tasarlandı. HTTP katmanı yalnızca request/response yönetir; iş kuralları service katmanında, kalıcı veri işlemleri repository katmanında tutulur.

```text
Browser
  │
  ├── Static frontend (HTML / CSS / JavaScript)
  │
  ▼
Spring Boot API
  ├── Controller     HTTP endpointleri ve request validation
  ├── Service        iş kuralları, yetki ve oda kontrolleri
  ├── Repository     JPA tabanlı veri erişimi
  ├── Security       JWT filter ve SecurityFilterChain
  └── Integration    TMDB movie search client
          │                    │
          ▼                    ▼
     PostgreSQL               TMDB API
```

Paket yapısı:

```text
config/           JWT, Security, OpenAPI ve TMDB ayarları
controller/       REST endpointleri
dto/              API request / response modelleri
entity/           JPA entity'leri
repository/       Spring Data repository'leri
service/abs/      Service sözleşmeleri
service/impl/     İş kurallarının implementasyonları
exception/        Merkezi hata yönetimi
db/migration/     Flyway SQL migration'ları
frontend/         Uygulamanın statik istemcisi
```

## Domain modeli ve kurallar

Temel ilişki `User -> RoomMember -> Room` şeklindedir. Bir oda en fazla iki üyeye sahip olabilir. Odaya katılım benzersiz davet kodu üzerinden yapılır; aynı kullanıcının aynı odaya ikinci kez eklenmesi veritabanı kısıtıyla da engellenir.

`WatchlistItem`, bir odaya ve ekleyen kullanıcıya bağlıdır. Oda silinirse bağlı üyeler ve liste kayıtları `ON DELETE CASCADE` ile temizlenir.

```text
User 1 --- * RoomMember * --- 1 Room 1 --- * WatchlistItem
                         \
                          role: OWNER | MEMBER
```

## Kimlik doğrulama ve yetkilendirme

- Kayıt ve giriş endpointleri BCrypt ile hashlenmiş parola kullanır.
- Başarılı giriş sonrasında backend imzalı access token üretir.
- `JwtAuthenticationFilter`, her istekte `Authorization: Bearer <token>` başlığını doğrular ve kullanıcı kimliğini `SecurityContext` içine yerleştirir.
- API stateless çalışır; sunucuda HTTP session tutulmaz.
- `/api/v1/auth/**`, frontend asset'leri ve Swagger dışındaki endpointler kimlik doğrulaması ister.
- Oda ve watchlist işlemlerinde sadece oda üyesi olan kullanıcılar işlem yapabilir.
- TMDB erişim anahtarı istemciye gönderilmez; yalnızca backend environment variable'ı olarak okunur. `.env` dosyası Git tarafından takip edilmez.

## API yüzeyi

| Alan | Endpoint | Amaç |
| --- | --- | --- |
| Auth | `POST /api/v1/auth/register` | Kullanıcı oluşturur |
| Auth | `POST /api/v1/auth/login` | JWT access token döner |
| Room | `POST /api/v1/rooms` | Yeni oda oluşturur |
| Room | `POST /api/v1/rooms/join` | Davet kodu ile odaya katılır |
| Room | `GET /api/v1/rooms/{roomId}` | Oda bilgisini getirir |
| Watchlist | `/api/v1/rooms/{roomId}/watchlist` | Ortak liste CRUD işlemleri |
| Movie catalog | `GET /api/v1/movies/search?query=...` | TMDB üzerinde film arar |

Detaylı request/response şemaları için Swagger UI kullanılabilir.

## Local olarak çalıştırma

### Gereksinimler

- Docker Desktop
- TMDB API Read Access Token (film arama özelliği için)

Önce örnek dosyayı kopyalayıp kendi token'ını ekle:

```bash
cp .env.example .env
```

```env
TMDB_API_READ_ACCESS_TOKEN=your_tmdb_read_access_token
```

Ardından container'ları başlat:

```bash
docker compose up --build -d
```

| Adres | Açıklama |
| --- | --- |
| `http://localhost:8080` | Frontend ve API |
| `http://localhost:8080/swagger-ui/api-docs.html` | Swagger UI |
| `localhost:5432` | PostgreSQL |

Kapatmak için:

```bash
docker compose down
```

## Testler

```bash
./mvnw test
```

Test paketi JWT üretim/doğrulama, odaya katılma kuralları ve TMDB response mapping senaryolarını içerir. TMDB mapping testi gerçek ağa bağımlı değildir; lokal sahte HTTP sunucusundan dönen response ile çalışır.

## Notlar ve roadmap

Şu an JWT access token tabanlı authentication uygulanıyor. Production sürümünde refresh token rotasyonu, rate limiting, audit logging, CI/CD pipeline ve WebSocket tabanlı gerçek zamanlı chat / izleme senkronizasyonu eklenmesi planlanıyor.
