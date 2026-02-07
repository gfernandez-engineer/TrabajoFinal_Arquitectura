
# FUNDAMENTOS TEORICOS 

## 1.1 Que Problema Resuelve Este Proyecto?

### El Problema: Arquitectura Monolitica Tradicional

Imagina una aplicacion donde todo esta junto:

```
┌─────────────────────────────────────────────────────┐
│              APLICACION MONOLITICA                  │
│                                                     │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌────────┐ │
│  │ Cursos  │──│Inscripc.│──│ Pagos   │──│Notific.│ │
│  └─────────┘  └─────────┘  └─────────┘  └────────┘ │
│         TODOS SE LLAMAN DIRECTAMENTE                │
└─────────────────────────────────────────────────────┘
```

**Problemas de este enfoque:**
- Si **Pagos falla**, todo el proceso de inscripcion falla
- Si hay **muchos usuarios**, no podemos escalar solo Pagos
- Si queremos **cambiar Notificaciones**, afectamos todo el sistema
- **Deployment**: cambiar una cosa = desplegar TODO

### La Solucion: Event-Driven Architecture (EDA)

```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│  Cursos  │     │Inscripc. │     │  Pagos   │     │ Notific. │
└────┬─────┘     └────┬─────┘     └────┬─────┘     └────┬─────┘
     │                │                │                │
     │    PUBLICA     │    PUBLICA     │    PUBLICA     │
     │    EVENTO      │    EVENTO      │    EVENTO      │
     ▼                ▼                ▼                ▼
═══════════════════════════════════════════════════════════════
                     APACHE KAFKA
              (Broker de Mensajes / "Cartero")
═══════════════════════════════════════════════════════════════
     ▲                ▲                ▲                ▲
     │   CONSUME      │   CONSUME      │   CONSUME      │
     │   EVENTOS      │   EVENTOS      │   EVENTOS      │
```

**Beneficios:**
- Los servicios **NO se conocen entre si**
- Si Pagos falla, el evento **queda guardado en Kafka** y se procesa despues
- Podemos **escalar cada servicio independientemente**
- Cada equipo puede **trabajar en su servicio** sin afectar a otros

---

## 1.2 Conceptos Clave que DEBEMOS Dominar

### Concepto 1: Evento de Dominio

> **Definicion**: Un evento es algo que YA PASO. Es un hecho historico inmutable.

**Ejemplo de la vida real:**
- "Juan compro un cafe" (ya paso, no se puede cambiar)
- "El avion despego" (ya ocurrio)

**En nuestro proyecto:**
- `EnrollmentCreatedEvent` = "Se creo una inscripcion"
- `PaymentApprovedEvent` = "Se aprobo un pago"

### Concepto 2: Productor (Publisher)

> **Definicion**: El servicio que CREA y ENVIA el evento a Kafka.

```
┌────────────────┐          ┌─────────┐
│ Enrollment     │ ─ENVIA─> │ KAFKA   │
│ Service        │  evento  │         │
│ (PRODUCTOR)    │          │         │
└────────────────┘          └─────────┘
```

### Concepto 3: Consumidor (Consumer)

> **Definicion**: El servicio que ESCUCHA y PROCESA eventos de Kafka.

```
┌─────────┐          ┌────────────────┐
│ KAFKA   │ ─ENVIA─> │ Payment        │
│         │  evento  │ Service        │
│         │          │ (CONSUMIDOR)   │
└─────────┘          └────────────────┘
```

### Concepto 4: Topic

> **Definicion**: Es como un "canal de television" en Kafka. Cada tipo de evento va a su canal.

**Nuestros Topics:**
```java
// En KafkaTopics.java
public static final String COURSE_EVENTS = "lms.course.events";      // Canal de cursos
public static final String ENROLLMENT_EVENTS = "lms.enrollment.events"; // Canal de inscripciones
public static final String PAYMENT_EVENTS = "lms.payment.events";     // Canal de pagos
```

### Concepto 5: Consumer Group

> **Definicion**: Identifica QUE SERVICIO esta escuchando. Kafka asegura que cada grupo reciba el mensaje UNA vez.

```java
@KafkaListener(topics = "lms.enrollment.events", groupId = "payment-service-group")
```

**Importante:** Si hay 3 instancias del Payment Service con el mismo `groupId`, solo UNA procesara cada mensaje.


# LMS Microservices

Sistema de Gestión de Aprendizaje (LMS) implementado con arquitectura de microservicios.

```
trabajo_final/
├── lms-microservices/           # <-- AQUI ESTAN LOS MICROSERVICIOS
│   ├── lms-shared/              # Codigo compartido (eventos, config)
│   ├── user-service/            # Servicio de usuarios
│   ├── course-service/          # Servicio de cursos
│   ├── enrollment-service/      # Servicio de inscripciones
│   ├── payment-service/         # Servicio de pagos
│   ├── notification-service/    # Servicio de notificaciones
│   └── docker-compose.yml       # Infraestructura
│
└── src/                         # Version monolitica (referencia)
```

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

## Diagrama de Secuencia Completo (Flujo de Inscripciones)

```
TIEMPO
  │
  │    ┌────────┐    ┌────────────┐    ┌─────────┐    ┌──────────┐    ┌─────────────┐
  │    │Cliente │    │ Enrollment │    │  KAFKA  │    │ Payment  │    │Notification │
  │    │  API   │    │  Service   │    │         │    │ Service  │    │  Service    │
  │    └───┬────┘    └─────┬──────┘    └────┬────┘    └────┬─────┘    └──────┬──────┘
  │        │               │                │              │                 │
  ▼        │               │                │              │                 │
           │ POST /api/    │                │              │                 │
  1 ──────>│ enrollments   │                │              │                 │
           │──────────────>│                │              │                 │
           │               │                │              │                 │
           │               │ Valida usuario │              │                 │
  2 ────── │               │ (HTTP)         │              │                 │
           │               │                │              │                 │
           │               │ Valida curso   │              │                 │
  3 ────── │               │ (HTTP)         │              │                 │
           │               │                │              │                 │
           │               │ Guarda enrollment              │                 │
  4 ────── │               │ PENDING_PAYMENT│              │                 │
           │               │                │              │                 │
           │               │ EnrollmentCreatedEvent        │                 │
  5 ────── │               │───────────────>│              │                 │
           │               │                │              │                 │
           │  202 ACCEPTED │                │              │                 │
  6 ──────<│<──────────────│                │              │                 │
           │               │                │              │                 │
           │               │                │ Consume      │                 │
  7 ────── │               │                │─────────────>│                 │
           │               │                │              │                 │
           │               │                │              │ Crea Payment    │
  8 ────── │               │                │              │ PENDING         │
           │               │                │              │                 │
           │               │                │              │ Procesa pago    │
  9 ────── │               │                │              │ (simulado)      │
           │               │                │              │                 │
           │               │                │ PaymentApprovedEvent           │
  10 ───── │               │                │<─────────────│                 │
           │               │                │              │                 │
           │               │ Consume        │              │                 │
  11 ───── │               │<───────────────│              │                 │
           │               │                │              │                 │
           │               │ Enrollment.    │              │                 │
  12 ───── │               │ confirm()      │              │                 │
           │               │                │              │                 │
           │               │ EnrollmentConfirmedEvent      │                 │
  13 ───── │               │───────────────>│──────────────────────────────>│
           │               │                │              │                 │
           │               │                │              │                 │ Notifica
  14 ───── │               │                │              │                 │ usuario
           │               │                │              │                 │
  ▼
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

# GLOSARIO RAPIDO

| Termino | Definicion Simple |
|---------|-------------------|
| **EDA** | Event-Driven Architecture. Servicios se comunican via eventos. |
| **Kafka** | Sistema de mensajeria. Como un cartero que guarda los mensajes. |
| **Topic** | Canal donde se publican eventos. Como un canal de TV. |
| **Producer** | Servicio que ENVIA eventos a Kafka. |
| **Consumer** | Servicio que RECIBE eventos de Kafka. |
| **Consumer Group** | Identificador del servicio consumidor. |
| **Offset** | Posicion del ultimo mensaje leido. |
| **DLQ** | Dead Letter Queue. Donde van los mensajes fallidos. |
| **Retry** | Reintentar procesar un mensaje si falla. |
| **Idempotencia** | Ejecutar algo N veces = mismo resultado que 1 vez. |
| **Saga** | Patron para manejar transacciones entre servicios. |
| **Pub/Sub** | Publish/Subscribe. Patron donde un productor publica y multiples consumidores reciben. |
| **Coreografia** | Tipo de Saga donde no hay orquestador central, cada servicio reacciona a eventos. |
| **Event Observer** | Servicio que escucha multiples topics (como Notification Service). |
