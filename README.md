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

## Sıradaki adım

Domain modellerini tasarlamak: önce `User` entity'si, sonra ilişkili oda yapısı.
