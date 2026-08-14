# Duo Space API

Çiftlerin birlikte izleyip, sohbet edebileceği ve oyun oynayabileceği uygulamanın Spring Boot backend'i.

## Geliştirme ortamı

- Java 21
- Spring Boot
- PostgreSQL 17 (Docker)

## PostgreSQL'i başlatma

```bash
docker compose up -d
```

Bu komut PostgreSQL ile Spring API'yi birlikte çalıştırır. Arayüzü `http://localhost:8080` adresinde açabilirsin. Veritabanı `localhost:5432` üzerinde çalışır.

## Branch düzeni

Her küçük backend adımı ayrı bir `feature/...` branch'inde geliştirilir. Tamamlanan her adımda README güncellenir ve ayrı commit atılır.

## Package düzeni

```text
controller/       REST controller sınıfları
service/abs/      Service interface'leri
service/impl/     Service implementasyonları
repository/       Spring Data JPA repository'leri
entity/           JPA entity ve enum sınıfları
dto/              Request/response DTO'ları
exception/        Global exception handler ve özel exception'lar
config/           Security ve uygulama konfigürasyonu
```

## Domain modeli

İlk model `User`'dır. UUID kimlik, benzersiz kullanıcı adı ve e-posta, parola özeti ile oluşturulma/güncellenme zamanlarını tutar. Tablo şeması Flyway ile `V1__create_users_table.sql` migration'ında yönetilir.

## Kayıt endpoint'i

`POST /api/v1/auth/register` kullanıcı oluşturur. Parolalar BCrypt ile hashlenir; yalın parola hiçbir zaman veritabanına yazılmaz.

```json
{
  "username": "akin",
  "email": "akin@example.com",
  "password": "guclu-bir-parola"
}
```

## Temel CRUD

- `POST/GET/PUT/DELETE /api/v1/rooms`
- `POST/GET/PUT/DELETE /api/v1/rooms/{roomId}/watchlist`

Bu geçici CRUD aşamasında sahiplik kimliği request ile gelir. JWT eklendiğinde bu bilgi token'dan alınacak ve endpoint'ler yetkilendirilecek.

## Sıradaki adım

CRUD endpoint testlerini eklemek; ardından JWT ile giriş ve endpoint yetkilendirmesine geçmek.
