# Rumbo — Profiles & Relationship Management: implementación integral por capas

**Ámbito exclusivamente implementado:** `com.aipaca.rumbo.platform.profiles`.
**Tecnología configurada por el proyecto:** Java 27, Spring Boot 4, Spring Data JPA, PostgreSQL.
**Fuentes de referencia:** modelo corregido *Rumbo · Profiles & Relationship Management* de Claude (decisiones D1–D14 y diagrama de la sección 7); Project Report Rumbo (US06, US07, US08, US12); libro del profesor Juan Antonio Flores Moroco, *Desarrollo de servicios web* (capítulos 4–7: D1–D6, I1–I6, A1–A3, R1–R3); transcripciones de clase aportadas.

## Alcance por capas

- **Domain:** cinco Aggregate Roots: `Profile`, `Student`, `School`, `ServiceEnrollment`, `DataDeletionRequest`; `TutorStudentRelationship` es entidad interna de `Student`. Commands y Queries tipados, value objects, repositorios por raíz y records de eventos.
- **Infrastructure:** persistencia JPA, entidades, assemblers, repositorios Spring Data y adaptadores para las cinco raíces. Relaciones con tutores se guardan solamente a través de Student; se conserva su historial.
- **Application:** interfaces de CQRS e implementaciones `handle(Command/Query)` para Profile, Student, School, ServiceEnrollment y DataDeletionRequest; transacciones, comprobaciones de tipo y autorización, prevención lógica de duplicidad y resultados del modelo `Result`/`ApplicationError` compartido.
- **Interfaces REST:** resources validados con `@Valid`, assemblers y cinco controladores en `/api/v1/` para perfiles, estudiantes, colegios, afiliaciones y solicitudes de eliminación. No se expone una colección global de perfiles ni de menores.
- **Seguridad de Profiles:** configuración `SecurityFilterChain` limitada a las cinco rutas REST de Profiles; identidad del actor obtiene un AccountId numérico de un principal **ya autenticado** por Spring Security. No hay credenciales aceptadas por cabeceras inventadas. Sin IAM conectado no existen accesos reales; la autenticación falla de forma segura.

## Operaciones REST

| Ruta (prefijo `/api/v1`) | Operaciones | Regla de acceso |
|---|---|---|
| `/profiles` | POST crear, GET `/me`, GET `/{profileId}`, PATCH `/me/contact` | Cuenta autenticada; lectura/modificación solo de perfil propio |
| `/students` | POST registrar, GET propios, GET `/{id}`, PUT `/{id}`, GET `/{id}/tutors`, POST `/{id}/tutors`, POST `/{id}/tutors/{tutorId}/revoke` | Tutor con relación vigente; no revocar último autorizado; autorización inicial automática |
| `/schools` | GET colección y `/{schoolId}` | Catálogo de solo lectura; sin CRUD inventado |
| `/service-enrollments` | POST solicitar, GET `/drivers/me`, GET `/drivers/me/students`, GET `/students/{studentId}`, POST `/{id}/accept`, `/cancel`, `/end` | Tutor o conductor habilitado para cada transición; listado de menores para conductor minimizado |
| `/data-deletion-requests` | POST solicitud, GET `/mine`, GET `/{id}`, POST `/{id}/reject`, `/approve` | Tutor autorizado para presentar; administrador verificado para resolver; aprobación bloqueada sin verificación de viajes ni supresión efectiva |

## Invariantes del nuevo diagrama preservadas

- D1–D3: IDs `Long`, con `AccountId` como único identificador foráneo envuelto en value object.
- D4–D8: nombres de dominio existentes, `School` en Profiles, un `Profile` con tipo `TUTOR`/`DRIVER`, sin `preferredLanguage` ni `educationLevel`, y sin inventar `resolvedBy`.
- D9–D11: revocación terminal de relación; nueva autorización = nueva fila histórica; autorizador vigente; no revocar la última autorización.
- D12–D13: `ServiceEnrollment` permite `PENDING→ACCEPTED→ENDED` y `PENDING→CANCELLED`; solo solicitante cancela y solo puede haber una `PENDING` o `ACCEPTED` por estudiante-conductor, pero pueden coexistir conductores diferentes.
- La consulta de unicidad está implementada en Application+JPA como prerrequisito; **la garantía concurrente requiere el índice parcial PostgreSQL** antes de abrir el endpoint a escrituras reales.
- Domain Events declarados: `TutorAuthorized`, `TutorAuthorizationRevoked`, `DataDeletionRequestApproved`, `StudentAnonymized`. Las autorizaciones y revocaciones se publican mediante un publicador dentro de Profiles después de commit. **No se han implementado consumidores de otros bounded contexts**.
- Registro de solicitud US08 no depende de viajes. La resolución `APPROVED` se bloquea porque sin consultar Trip Execution y sin una política validada de eliminación de PII no se puede asegurar la supresión. La resolución `REJECTED` sí está implementada con verificación de administrador.

## Dependencias externas deliberadamente no implementadas

1. **IAM:** la dependencia se representa mediante el puerto `ActorIdentity` y el adaptador `SpringSecurityActorIdentity`, que exige un principal confiable con identificador `AccountId` numérico. **No es un proveedor de JWT**. La configuración de seguridad no modifica otro contexto.
2. **Trip Execution:** no hay gateway de consulta de viajes en curso en esta rama. El endpoint de aprobación está expuesto pero devuelve una respuesta de conflicto y no cambia el estado de la solicitud.
3. **Política de supresión:** `Student.anonymize()` revoca autorizaciones y fija `INACTIVE`, pero deja los campos de nombre y nacimiento; por eso el Command Service no lo toma como borrado efectivo y no completa aprobaciones.
4. **Notification Management:** los eventos locales están disponibles para integración futura, pero aquí no hay consumidores, colas, brokers ni garantía de mensajería durable. Un `ApplicationEventPublisher` después de commit no sustituye un outbox.

## Revisión de datos antes del primer arranque

No ejecutar `spring-boot:run` contra una base existente con `spring.jpa.hibernate.ddl-auto=update` sin revisar el esquema: `students.version`, `students.authorization_revision`, `tutor_student_relationships.student_id`, `service_enrollments.cancelled_at`, las restricciones antiguas y las migraciones necesarias. Se adjunta un borrador **NO EJECUTADO** en `docs/profiles-schema-review.sql`.

## Pruebas y nivel de certeza

- Se añadieron pruebas Mockito/JUnit de Application (registros, autorizaciones, revocaciones, afiliaciones, solicitudes) y de publicación post-commit. Se mantienen los tests de Domain, persistencia JPA y mapeo del proyecto.
- **Ejecutado en este entorno:** compilación aislada con `javac --release 21` de copias temporales de 85 archivos Domain+Application con sustitutos de Spring/Lombok, sin tocar fuentes del proyecto; compilación aislada de 139 archivos incluyendo REST y dobles mínimos del framework; 21 aserciones de invariantes del dominio correctas. No equivale a Maven real.
- **NO ejecutado aquí:** Maven/Java 27 (solo hay JDK 21 y no está instalado Maven), tests JUnit reales, arranque Spring Boot, consultas PostgreSQL, migraciones, carga de metadatos Hibernate real ni comprobaciones de concurrencia con BD.
- Java configurado en el `pom.xml`: **27**, sin cambios de versión.

### Comando para probar con JDK 27 en IntelliJ IDEA

```powershell
java -version
.\mvnw.cmd --batch-mode --no-transfer-progress '-Dtest=ProfilesDomainConsistencyTests,SchoolTests,ServiceEnrollmentTests,DataDeletionRequestTests,*PersistenceAssemblerTests,*RepositoryImplTests,*PersistenceMappingTests,AccountIdPersistenceConverterTests,*CommandServiceImplTests,SpringProfilesEventPublisherTests' test
```

No incluye `RumboWebServicesApplicationTests`, cuyo error de localización de `@SpringBootConfiguration` consta en la auditoría histórica y no se modificó por estar fuera de Profiles. Después deberá verificarse el contexto Spring y la base **en un entorno de pruebas seguro y con el esquema aprobado**.
