# DuoSpace

Spring Boot ile geliştirilmiş, iki kullanıcının ortak oda üzerinden film listesi yönetmesini sağlayan REST API. Uygulama kullanıcı kimliğini JWT ile doğrular, oda üyeliğini her işlemde kontrol eder ve film arama verisini TMDB API'den backend üzerinden alır.

## Sistem tasarımı

```text
Browser
  │  static HTML / CSS / JavaScript
  ▼
Spring Boot application
  ├── SecurityFilterChain
  │     └── JwtAuthenticationFilter
  ├── Controller
  ├── Service interface + implementation
  ├── Spring Data JPA repository
  ├── Flyway migration
  └── TMDB client (RestClient)
          │                     │
          ▼                     ▼
      PostgreSQL 17          TMDB API
```

Uygulama katmanları:

| Katman | Sorumluluk |
| --- | --- |
| `controller` | HTTP endpoint, request validation, response status |
| `service/abs` | İş kurallarının sözleşmesi |
| `service/impl` | Oda üyeliği, sahiplik ve CRUD kuralları |
| `repository` | JPA üzerinden kalıcı veri erişimi |
| `entity` | PostgreSQL tablolarının domain modeli |
| `dto` | API request/response modelleri |
| `config` | JWT, Spring Security, OpenAPI ve TMDB ayarları |
| `exception` | Merkezi hata cevabı üretimi |

## Teknoloji seti

| Alan | Kullanım |
| --- | --- |
| Runtime | Java 21, Spring Boot 4 |
| Web | Spring Web MVC, Bean Validation |
| Persistence | Spring Data JPA, PostgreSQL 17 |
| Schema management | Flyway (`ddl-auto=validate`) |
| Security | Spring Security, BCrypt, JJWT |
| External API | Spring `RestClient`, TMDB API |
| API documentation | Springdoc OpenAPI / Swagger UI |
| Container | Docker, Docker Compose |
| Client | Vanilla HTML, CSS, JavaScript |

## Veri modeli

```text
users
 ├── id (UUID, PK)
 ├── username (UNIQUE)
 ├── email (UNIQUE)
 └── password_hash

rooms
 ├── id (UUID, PK)
 ├── owner_id -> users.id
 └── invite_code (UNIQUE)

room_members
 ├── room_id -> rooms.id
 ├── user_id -> users.id
 └── role: OWNER | MEMBER

watchlist_items
 ├── room_id -> rooms.id
 ├── added_by_id -> users.id
 ├── title
 ├── source_url
 └── status: PLANNED | WATCHING | COMPLETED
```

`room_members` üzerinde `(room_id, user_id)` unique constraint'i bulunur. Bir oda en fazla iki üyeye izin verir; bu limit service katmanında kontrol edilir. Oda silindiğinde üyelik ve watchlist kayıtları foreign key `ON DELETE CASCADE` ile silinir.

Şema, `V1__create_users_table.sql` ve `V2__create_rooms_members_and_watchlist.sql` Flyway migration'larıyla oluşturulur. Hibernate şema üretmez, mevcut şemayı doğrular.

## Kimlik doğrulama ve erişim kontrolü

1. Kullanıcı `register` veya `login` endpointini çağırır.
2. Parola BCrypt hash'i ile karşılaştırılır.
3. Başarılı girişte kullanıcı UUID'sini içeren, varsayılan olarak 15 dakika geçerli access token üretilir.
4. İstemci, korumalı isteklere `Authorization: Bearer <access-token>` header'ı ekler.
5. `JwtAuthenticationFilter` tokenı doğrular ve kullanıcı UUID'sini `SecurityContext` içine koyar.
6. Controller, kullanıcı bilgisini request body'den değil `Authentication` nesnesinden alır.

Security policy:

| Route | Erişim |
| --- | --- |
| `/`, `/assets/**` | Public |
| `/api/v1/auth/**` | Public |
| `/swagger-ui/**`, `/v3/api-docs/**` | Public |
| Diğer tüm API endpointleri | Bearer JWT zorunlu |

Uygulama stateless'tir: form login, HTTP Basic ve server-side HTTP session kapalıdır. Geçersiz veya süresi dolmuş token `401 Unauthorized` döner. Odaya ait kaynaklarda üyelik; oda güncelleme ve silme işlemlerinde ayrıca sahiplik kontrol edilir.

## API sözleşmesi

Tüm korumalı endpointler aşağıdaki header'ı ister:

```http
Authorization: Bearer <access-token>
```

### Authentication

| Method | Path | Authentication | Request |
| --- | --- | --- | --- |
| `POST` | `/api/v1/auth/register` | Yok | `username`, `email`, `password` |
| `POST` | `/api/v1/auth/login` | Yok | `email`, `password` |

Kayıt kuralları: kullanıcı adı 3-50, e-posta en fazla 255, parola 8-72 karakterdir. Kullanıcı adı veya e-posta tekrar ederse `409 Conflict` döner.

```json
POST /api/v1/auth/login
{
  "email": "user@example.com",
  "password": "example-password"
}
```

Başarılı login response'u access token içerir. Bu token Swagger UI'daki `Authorize` alanına girilerek korumalı endpointler denenebilir.

### Room API

| Method | Path | Kural |
| --- | --- | --- |
| `POST` | `/api/v1/rooms` | Token sahibi kullanıcı oda sahibi olur |
| `POST` | `/api/v1/rooms/join` | Davet kodu geçerli olmalı, oda iki kişiye ulaşmamış olmalı |
| `GET` | `/api/v1/rooms` | Token sahibinin üye olduğu odaları listeler |
| `GET` | `/api/v1/rooms/{roomId}` | Yalnızca oda üyesi okuyabilir |
| `PUT` | `/api/v1/rooms/{roomId}` | Yalnızca oda sahibi günceller |
| `DELETE` | `/api/v1/rooms/{roomId}` | Yalnızca oda sahibi silebilir |

```json
POST /api/v1/rooms
{
  "name": "Hafta sonu listesi"
}
```

Oda adı boş olamaz ve en fazla 100 karakterdir. İstemci `ownerId` göndermez; owner token içinden belirlenir.

### Watchlist API

| Method | Path | Kural |
| --- | --- | --- |
| `POST` | `/api/v1/rooms/{roomId}/watchlist` | Yalnızca oda üyesi ekleyebilir |
| `GET` | `/api/v1/rooms/{roomId}/watchlist` | Yalnızca oda üyesi listeleyebilir |
| `PUT` | `/api/v1/rooms/{roomId}/watchlist/{itemId}` | Yalnızca oda üyesi güncelleyebilir |
| `DELETE` | `/api/v1/rooms/{roomId}/watchlist/{itemId}` | Yalnızca oda üyesi silebilir |

```json
POST /api/v1/rooms/{roomId}/watchlist
{
  "title": "Arrival",
  "sourceUrl": "https://www.themoviedb.org/movie/329865"
}
```

Kaydı ekleyen kullanıcı token'dan alınır. `PUT` isteğinde başlık, kaynak URL'si ve `PLANNED`, `WATCHING` veya `COMPLETED` durumlarından biri gönderilir.

### Movie catalog API

| Method | Path | Kural |
| --- | --- | --- |
| `GET` | `/api/v1/movies/search?query=arrival` | JWT gerekli, sorgu 2-100 karakter |

Endpoint backend üzerinden TMDB `/search/movie` endpointine gider, sonucu en fazla 10 kayıtla sınırlar ve şu alanları döner: `tmdbId`, `title`, `releaseYear`, `posterUrl`, `overview`, `voteAverage`.

TMDB tokenı frontend'e verilmez. Token yoksa veya dış servis ulaşılamazsa API `503 Service Unavailable` döner.

## Hata cevabı standardı

`GlobalExceptionHandler` domain exception'larını tek alanlı JSON response'a dönüştürür:

```json
{
  "message": "Bu işlem için yetkin yok."
}
```

| HTTP status | Örnek durum |
| --- | --- |
| `400` | Bean Validation başarısız |
| `401` | Hatalı kullanıcı bilgisi veya geçersiz JWT |
| `403` | Oda sahibi olmayan kullanıcının sahiplik işlemi yapması |
| `404` | Oda veya watchlist kaydı bulunamadı |
| `409` | Tekrarlanan kullanıcı veya dolu/uygunsuz oda katılımı |
| `503` | TMDB erişimi başarısız |

## Local environment ve Docker

### Gerekli environment variable'lar

| Variable | Kullanım |
| --- | --- |
| `DB_URL` | PostgreSQL JDBC URL |
| `DB_USERNAME` | PostgreSQL kullanıcı adı |
| `DB_PASSWORD` | PostgreSQL parolası |
| `JWT_SECRET` | Production access token signing secret'i |
| `JWT_ACCESS_TOKEN_EXPIRATION` | ISO-8601 duration, ör. `PT15M` |
| `TMDB_API_READ_ACCESS_TOKEN` | TMDB backend erişim tokenı |

Local Docker kurulumu için `.env.example` dosyasını `.env` olarak kopyala ve TMDB tokenını ekle:

```bash
cp .env.example .env
docker compose up --build -d
```

| URL | Açıklama |
| --- | --- |
| `http://localhost:8080` | Static frontend ve REST API |
| `http://localhost:8080/swagger-ui/api-docs.html` | OpenAPI / Swagger UI |
| `localhost:5432` | PostgreSQL portu |

`compose.yaml`, API container'ını PostgreSQL healthcheck tamamlanmadan başlatmaz. TMDB tokenı `.env` üzerinden API container'ına aktarılır. `.env` `.gitignore` içinde olduğu için repository'ye eklenmez.

Container'ları kapatmak için:

```bash
docker compose down
```

## Testler

```bash
./mvnw test
```

| Test sınıfı | Doğrulanan davranış |
| --- | --- |
| `JwtServiceTest` | Üretilen access token içinden doğru kullanıcı UUID'sinin çıkarılması |
| `RoomServiceImplTest` | Geçerli davet kodu, tekrar katılım engeli, iki kişi oda limiti |
| `TmdbMovieCatalogServiceTest` | TMDB JSON alanlarının API DTO'suna eşlenmesi; yıl ve poster URL'si üretimi |
| `ManitimleProjeApplicationTests` | Spring context, Flyway ve PostgreSQL bağlantısının açılması |

TMDB servis testi gerçek TMDB ağına bağlı değildir. Test içinde lokal HTTP server ayağa kaldırılır; böylece JSON mapping testi ağ/DNS probleminden bağımsız çalışır.
