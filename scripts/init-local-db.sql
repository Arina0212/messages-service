-- Локальная подготовка PostgreSQL для сервиса Messages.
-- Выполнить от суперпользователя: psql -U postgres -f scripts/init-local-db.sql

CREATE ROLE messages_app LOGIN PASSWORD 'messages_app';

CREATE DATABASE messages_db OWNER messages_app;
