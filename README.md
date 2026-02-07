# TrabajoFinal_Arquitectura

El trabajo final, docker compose, y README, se encuentra en la carpeta trabajo_final/lms-microservices/.
Leer detenidamente el README que se encuentra en esa carpeta, para conocer todo sobre el proyecto, modo de uso, e indicaciones extras.
En esa carpeta se encuentra todos microservicios desarrollados.


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
