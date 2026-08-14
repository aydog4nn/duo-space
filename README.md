# DuoSpace

DuoSpace, çiftlerin birlikte film izleyebileceği, ortak listeler oluşturabileceği, mesajlaşabileceği ve mini oyunlar oynayabileceği bir web uygulaması temelidir.

Şu an proje; görsel bir çift dashboard'u, Spring Boot REST API altyapısı, PostgreSQL şeması ve Docker ile tek komutta çalışan geliştirme ortamını içerir.

## Özellikler

- Birlikte izleme için film ekranı ve ortak izleme listesi arayüzü
- Oda ve watchlist CRUD API altyapısı
- Kullanıcı kaydı ve BCrypt parola hashleme
- Flyway ile versiyonlanmış PostgreSQL migration'ları
- Responsive, romantik çift sitesi frontend'i
- Spring API + PostgreSQL için Docker Compose paketi

## Teknolojiler

- Java 21 / Spring Boot
- Spring Data JPA, Spring Security, Validation, WebSocket
- PostgreSQL 17 / Flyway
- Docker & Docker Compose
- Vanilla HTML, CSS ve JavaScript

## Proje yapısı

```text
frontend/                 Arayüz kaynak dosyaları
src/main/java/
  config/                 Uygulama ve security ayarları
  controller/             REST controller'lar
  dto/                    Request/response DTO'ları
  entity/                 JPA entity ve enum'ları
  exception/              Global hata yönetimi
  repository/             Spring Data repository'leri
  service/abs/            Service interface'leri
  service/impl/           Service implementasyonları
src/main/resources/
  db/migration/           Flyway SQL migration'ları
Dockerfile                Spring API image tanımı
compose.yaml              API + PostgreSQL geliştirme ortamı
```

## Hızlı başlangıç

Docker Desktop açıkken:

```bash
docker compose up --build -d
```

Ardından uygulamayı aç:

```text
http://localhost:8080
```

Container durumunu görmek için:

```bash
docker compose ps
```

Kapatmak için:

```bash
docker compose down
```

## Geliştirme notları

- Frontend kaynakları `frontend/` altındadır. Docker build sürecinde Spring Boot static kaynaklarına eklenir.
- Yerelde Spring Boot çalışırken `frontend/` dosyaları doğrudan servis edilir.
- Veritabanı `localhost:5432` üzerinde çalışır; yerel geliştirme bilgileri `compose.yaml` içindedir.
- Yeni şema değişiklikleri için mevcut migration dosyası değiştirilmez; yeni bir `V{n}__description.sql` dosyası eklenir.

## API özeti

| Alan | Endpoint |
| --- | --- |
| Kayıt | `POST /api/v1/auth/register` |
| Odalar | `POST/GET/PUT/DELETE /api/v1/rooms` |
| Ortak liste | `POST/GET/PUT/DELETE /api/v1/rooms/{roomId}/watchlist` |

> JWT henüz eklenmediği için oda ve watchlist işlemlerindeki kullanıcı bilgisi geçici olarak request üzerinden alınır. JWT aşamasında bu bilgi doğrulanmış token'dan gelecektir.

## Yol haritası

1. CRUD endpoint integration testleri
2. JWT tabanlı giriş ve yetkilendirme
3. WebSocket ile chat ve izleme senkronizasyonu
4. Ortak oyun odaları
5. CI/CD ve production deployment

## Branch yaklaşımı

Her bağımsız iş `feature/...`, `refactor/...`, `docs/...` veya `chore/...` branch'inde geliştirilir; testten sonra commitlenir ve `main`e alınır.
