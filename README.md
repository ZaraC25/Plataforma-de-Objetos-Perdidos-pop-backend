# Plataforma Institucional de Objetos Perdidos (POP) - Backend

Backend REST API para la gestión de objetos perdidos en la universidad. Construido con Spring Boot 3.3.4 y Java 21.

## Stack
- Java 21, Spring Boot 3.3.4
- PostgreSQL 16, Flyway
- JWT (jjwt 0.12.6)
- Maven, Docker

## Requisitos previos
- JDK 21
- Docker y Docker Compose (para base de datos)

## Cómo levantar
1. Levantar DB: `docker-compose up -d`
2. Ejecutar app: `./mvnw spring-boot:run`

## Configuración (.env)
Copiar `.env.example` a `.env` y configurar variables.

## Tests
Ejecutar `./mvnw test` (usa Testcontainers).

## Docker / Despliegue en Render/Railway
Construir imagen: `docker build -t pop-backend .`
El Dockerfile usa multi-stage build.

## Documentación API (Swagger UI)
Una vez en ejecución: http://localhost:8080/api/docs
