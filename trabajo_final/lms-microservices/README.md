# LMS Microservices

Sistema de Gestión de Aprendizaje (LMS) implementado con arquitectura de microservicios.

## Arquitectura

```
┌─────────────┐     REST      ┌─────────────┐
│   user-     │◄─────────────►│ enrollment- │
│   service   │               │   service   │
│   :8081     │               │   :8083     │
└─────────────┘               └──────┬──────┘
                                     │
┌─────────────┐               ┌──────▼──────┐
│   course-   │◄─────────────►│             │
│   service   │     REST      └─────────────┘
│   :8082     │
└──────┬──────┘
       │ Kafka
       ▼
┌──────────────────────────────────────────────────┐
│                    KAFKA :9092                    │
│  lms.course.events | lms.enrollment.events       │
│  lms.payment.events                              │
└───────┬─────────────────┬─────────────────┬──────┘
        │                 │                 │
        ▼                 ▼                 ▼
┌─────────────┐   ┌─────────────┐   ┌─────────────┐
│  payment-   │   │notification-│   │ enrollment- │
│   service   │   │   service   │   │   service   │
│   :8084     │   │   :8085     │   │   :8083     │
└─────────────┘   └─────────────┘   └─────────────┘
```

## Servicios

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| user-service | 8081 | Gestión de usuarios |
| course-service | 8082 | Gestión de cursos |
| enrollment-service | 8083 | Inscripciones de estudiantes |
| payment-service | 8084 | Procesamiento de pagos |
| notification-service | 8085 | Notificaciones |
| Kafka UI | 8090 | Interfaz web para Kafka |

## Requisitos

- Java 21
- Maven 3.8+
- Docker y Docker Compose

## Ejecución

### 1. Iniciar Kafka

```bash
docker-compose up -d
```

Verificar en http://localhost:8090 (Kafka UI)

### 2. Compilar el proyecto

```bash
mvn clean install
```

### 3. Iniciar los microservicios

En terminales separadas:

```bash
# Terminal 1 - User Service
cd user-service
mvn spring-boot:run

# Terminal 2 - Course Service
cd course-service
mvn spring-boot:run

# Terminal 3 - Enrollment Service
cd enrollment-service
mvn spring-boot:run

# Terminal 4 - Payment Service
cd payment-service
mvn spring-boot:run

# Terminal 5 - Notification Service
cd notification-service
mvn spring-boot:run
```

## Flujo de Prueba Completo

### 1. Crear un usuario

```bash
curl -X POST http://localhost:8081/api/users \
  -H "Content-Type: application/json" \
  -d '{"fullName": "Juan Perez", "email": "juan@email.com"}'
```

Respuesta esperada:
```json
{
  "id": 1,
  "fullName": "Juan Perez",
  "email": "juan@email.com",
  "status": "ACTIVE"
}
```

### 2. Crear un curso

```bash
curl -X POST http://localhost:8082/api/courses \
  -H "Content-Type: application/json" \
  -d '{"title": "Spring Boot Masterclass", "description": "Aprende Spring Boot", "instructor": "Maria Garcia"}'
```

### 3. Publicar el curso

```bash
curl -X PUT http://localhost:8082/api/courses/1/publish
```

### 4. Inscribir al estudiante

```bash
curl -X POST http://localhost:8083/api/enrollments \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "courseId": 1}'
```

Respuesta esperada (status 202 Accepted):
```json
{
  "id": 1,
  "userId": 1,
  "courseId": 1,
  "status": "PENDING_PAYMENT"
}
```

### 5. Verificar el flujo

#### Ver estado de la inscripción (después de unos segundos):
```bash
curl http://localhost:8083/api/enrollments/1
```

El estado cambiará a `CONFIRMED` o `CANCELLED` dependiendo del resultado del pago.

#### Ver pagos:
```bash
curl http://localhost:8084/api/payments
```

#### Ver notificaciones:
```bash
curl http://localhost:8085/api/notifications
```

#### Ver eventos en Kafka UI:
Abrir http://localhost:8090 y revisar los topics:
- `lms.course.events`
- `lms.enrollment.events`
- `lms.payment.events`

## API Endpoints

### User Service (8081)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /api/users | Crear usuario |
| GET | /api/users/{id} | Obtener usuario |
| GET | /api/users/{id}/validate | Validar usuario |
| GET | /api/users | Listar usuarios |

### Course Service (8082)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /api/courses | Crear curso |
| GET | /api/courses/{id} | Obtener curso |
| GET | /api/courses/{id}/validate | Validar curso |
| PUT | /api/courses/{id}/publish | Publicar curso |
| GET | /api/courses | Listar cursos |

### Enrollment Service (8083)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /api/enrollments | Inscribir estudiante |
| GET | /api/enrollments/{id} | Obtener inscripción |
| GET | /api/enrollments | Listar inscripciones |
| GET | /api/enrollments/user/{userId} | Inscripciones por usuario |

### Payment Service (8084)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /api/payments/{id} | Obtener pago |
| GET | /api/payments/enrollment/{id} | Pago por inscripción |
| GET | /api/payments | Listar pagos |

### Notification Service (8085)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /api/notifications | Listar notificaciones |
| GET | /api/notifications/{id} | Obtener notificación |
| GET | /api/notifications/user/{userId} | Notificaciones por usuario |

## Eventos Kafka

| Topic | Eventos |
|-------|---------|
| lms.course.events | CourseCreatedEvent, CoursePublishedEvent |
| lms.enrollment.events | EnrollmentCreatedEvent, EnrollmentConfirmedEvent, EnrollmentCancelledEvent |
| lms.payment.events | PaymentApprovedEvent, PaymentRejectedEvent |

## Detener los servicios

```bash
# Detener Kafka
docker-compose down

# Para eliminar volúmenes también
docker-compose down -v
```
