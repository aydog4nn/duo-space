# DuoSpace

Bu proje, çiftlerin birlikte film izleyebilmesi, izlenecekler listesi hazırlaması ve ileride beraber oyun oynayabilmesi için başladığım bir web projesi.

Şu an backend tarafında kullanıcı kaydı, giriş işlemi, JWT ile korunan endpointler, oda ve watchlist işlemleri var. Frontend üzerinden kayıt/giriş yapılabiliyor; oda oluşturup davet kodu ile eşini odaya alabiliyor ve ortak listeye film ya da oyun ekleyebiliyorsun.

## Kullanılan teknolojiler

- Java 21 ve Spring Boot
- Spring Data JPA, Spring Security, Validation
- PostgreSQL ve Flyway
- Docker Compose
- HTML, CSS ve JavaScript

## Klasör yapısı

```text
frontend/                 Frontend dosyaları
src/main/java/
  config/                 Security ve JWT ayarları
  controller/             Endpointler
  dto/                    Request ve response sınıfları
  entity/                 Veritabanı entity sınıfları
  exception/              Hata yönetimi
  repository/             Repository sınıfları
  service/abs/            Service interface'leri
  service/impl/           Service implementasyonları
src/main/resources/
  db/migration/           Flyway migration dosyaları
Dockerfile                Uygulama image dosyası
compose.yaml              API ve PostgreSQL ayarları
```

## Çalıştırma

Docker Desktop açıkken proje klasöründe şunu çalıştırmak yeterli:

```bash
docker compose up --build -d
```

Uygulama: `http://localhost:8080`

Swagger API dokümantasyonu: `http://localhost:8080/swagger-ui/api-docs.html`

Swagger üzerinden korumalı endpointleri denemek için önce `/api/v1/auth/login` ile giriş yap. Dönen `accessToken` değerini sağ üstteki `Authorize` butonuna `Bearer <token>` şeklinde ekle.

Tarayıcıda ilk açılışta kayıt ol veya giriş yap. Ardından oda oluşturup ekranda çıkan davet kodunu diğer kullanıcıyla paylaşabilirsin. Odaya katılan kullanıcı aynı ortak listeyi görür; `+` ile eklenen seçimler veritabanına yazılır.

Kapatmak için:

```bash
docker compose down
```

## API tarafı

| İşlem | Endpoint |
| --- | --- |
| Kullanıcı kaydı | `POST /api/v1/auth/register` |
| Giriş | `POST /api/v1/auth/login` |
| Odalar | `POST/GET/PUT/DELETE /api/v1/rooms` |
| Odaya katılma | `POST /api/v1/rooms/join` |
| Watchlist | `POST/GET/PUT/DELETE /api/v1/rooms/{roomId}/watchlist` |

Giriş yaptıktan sonra dönen token, korunan endpointlere giderken header'a eklenmeli:

```text
Authorization: Bearer <access-token>
```

Oda oluştururken `ownerId`, watchlist eklerken de `addedById` göndermiyoruz. Bunlar token içindeki kullanıcıdan alınıyor. Oda sahibinin odayı düzenleme ve silme yetkisi var; oda üyeleri ise odayı ve ortak listeyi görebiliyor.

Odaya katılmak için `POST /api/v1/rooms/join` endpointine `inviteCode` gönderilir. Bir odaya en fazla iki kullanıcı katılabilir.

## JWT ayarı

Yerelde varsayılan bir anahtar ile çalışıyor. Deploy ederken kendi Base64 JWT anahtarını environment variable olarak vermek gerekiyor:

```bash
JWT_SECRET=<base64-secret>
JWT_ACCESS_TOKEN_EXPIRATION=PT15M
```

## Sonraki işler

1. Refresh token ve şifre yenileme
2. Chat için WebSocket
3. Aynı anda film izleme senkronu
4. Basit oyun odaları
5. CI/CD ve deploy

## Branch düzeni

Her işi ayrı bir branch'te yapıp bitince `main` branch'ine alıyorum.
