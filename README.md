# Message Service

Message Service

## Запуск (Docker)

```bash
docker compose up --build -d
```

| Контейнер | Порт | Назначение |
|-----------|------|------------|
| `messages-postgres` | `5434` | PostgreSQL (`messages_db`) |
| `messages-service` | `9001` | Message Service |

Сервис: `http://localhost:9001`  
БД: `localhost:5434`, user/password `messages_app`/`messages_app`

При старте Flyway применяет миграции (таблицы `messages`, `subscriptions`).

Подписка проверяет автора, а лента подтягивает имена через сервис Users на `http://localhost:9000`.
В Docker адрес задаётся переменной `USERS_SERVICE_URL`.

Локальный запуск без Docker-образа сервиса (Postgres всё равно через compose):

```bash
docker compose up -d db
DB_R2DBC_URL=r2dbc:postgresql://localhost:5434/messages_db \
DB_JDBC_URL=jdbc:postgresql://localhost:5434/messages_db \
mvn spring-boot:run
```

## API

| Method | Path | Auth | Описание |
|--------|------|------|----------|
| `POST` | `/messages` | JWT + `producer` | опубликовать сообщение |
| `GET` | `/messages/{id}` | нет | одно сообщение |
| `PATCH` | `/messages/{id}` | JWT, автор | изменить текст |
| `DELETE` | `/messages/{id}` | JWT, автор или `admin` | удалить сообщение |
| `GET` | `/messages` | нет | общая лента, `limit` и `before_id` |
| `GET` | `/messages?author_id=5` | нет | сообщения одного автора |
| `GET` | `/messages/feed` | JWT | лента подписок |
| `PUT` | `/subscriptions/{producerId}` | JWT | подписаться |
| `DELETE` | `/subscriptions/{producerId}` | JWT | отписаться |
| `GET` | `/subscriptions/my` | JWT | на кого я подписан |
| `GET` | `/subscribers/my` | JWT | кто подписан на меня |

Токен выпускает сервис Users, Messages проверяет подпись тем же секретом.

### Публикация

```http
POST /messages
Authorization: Bearer <token>
Content-Type: application/json

{
  "text": "Первое сообщение"
}
```

Ответ `201 Created`:

```json
{
  "code": "OK",
  "data": { "id": 1, "authorId": 7, "text": "Первое сообщение", "createdAt": "2026-11-24T10:00:00Z" }
}
```

### Лента

```http
GET /messages/feed?limit=20
Authorization: Bearer <token>
```

```json
{
  "code": "OK",
  "data": {
    "items": [
      {
        "id": 1,
        "authorId": 7,
        "text": "Первое сообщение",
        "author": { "id": 7, "firstname": "Мария", "lastname": "Орлова" },
        "createdAt": "2026-11-24T10:00:00Z"
      }
    ],
    "next": 1,
    "limit": 20
  }
}
```

`next` равен id последнего элемента страницы или отсутствует на последней странице.
Если Users недоступен, сообщения приходят без поля `author`.
