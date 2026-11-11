# Profiles: dependencias al retirar `events` y `security`

Se mantuvieron eliminadas las cuatro carpetas solicitadas de `profiles`.
Las clases Java de `application/internal/commandservices`, `queryservices`,
los controladores y tests ya no importan esos paquetes. Se retiró la publicación
`publishAfterCommit` del servicio de estudiantes y la prueba del publisher.

## Importante: acceso a datos de menores

`profiles/application/internal/access` contiene el contrato mínimo de identidad
(`ActorIdentity`), la política de permisos por tutor/estudiante (`ProfilesAuthorization`)
y la excepción de acceso. **No** es un sistema IAM ni una nueva capa de seguridad;
son reglas necesarias para que los servicios mantengan sus invariantes y compilación.

La implementación temporal `UnavailableActorIdentity` **rechaza todos los intentos de
identificar una cuenta**, porque esta rama carece de autenticación verificada. No confía
en IDs enviados por clientes, headers arbitrarios ni cuentas fabricadas. Esto significa
que los endpoints personales quedan intencionalmente bloqueados hasta que se conecte
un proveedor real de identidad. El catálogo de escuelas no necesita identidad.
No se debe sustituir este comportamiento por aceptar cualquier `profileId` del cliente.

La dependencia `spring-boot-starter-security` permanece en el POM del proyecto original.
En ausencia de configuración propia, Spring Boot puede imponer autenticación básica
predeterminada; no prueba que IAM esté integrado.

Los eventos dentro de `domain/model/events` no se borraron, porque la decisión del
usuario fue quitar la integración `application/events` e `infrastructure/events`.
Las notificaciones de autorizaciones/revocaciones están pendientes de integración.

## Verificación pendiente

Compilar y probar con **JDK 27** en IntelliJ (`mvn -v`, luego `./mvnw test`).
El entorno de edición no dispone de Maven/JDK 27 para una regresión completa.
No se ejecutó SQL ni se modificó PostgreSQL.
