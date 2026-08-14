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

Veritabanı `localhost:5432` üzerinde çalışır. Yerel varsayılan bağlantı bilgileri `compose.yaml` içindedir.

## Branch düzeni

Her küçük backend adımı ayrı bir `feature/...` branch'inde geliştirilir. Tamamlanan her adımda README güncellenir ve ayrı commit atılır.

## Domain modeli

İlk model `User`'dır. UUID kimlik, benzersiz kullanıcı adı ve e-posta, parola özeti ile oluşturulma/güncellenme zamanlarını tutar. Tablo şeması Flyway ile `V1__create_users_table.sql` migration'ında yönetilir.

## Sıradaki adım

`UserRepository` ve kayıt için service katmanını eklemek.
