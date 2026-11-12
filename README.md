# Rumbo — Web Services (Open Source)

Backend del proyecto Rumbo. Sigue la separación por bounded contexts, REST Controllers,
Application Services y Repository JPA utilizada en Learning Center UPC.

## Requisitos
- JDK 27
- PostgreSQL 17 (local o instancia de desarrollo autorizada)
- Maven Wrapper incluido (`mvnw.cmd`): no instalar Maven aparte

## Compilar en cualquier PC (sin levantar la base de datos)
En PowerShell, desde esta carpeta:

```powershell
java -version
.\mvnw.cmd -DskipTests package
```

Compilar no exige que el servidor PostgreSQL esté encendido. Las pruebas de integración
que utilizan base de datos se ejecutan en GitHub Actions con PostgreSQL.

## Ejecutar el backend
Usa tu propia base `rumbo` (creada con pgAdmin o PostgreSQL) y configura su conexión
sin guardar contraseñas en GitHub:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/rumbo"
$env:SPRING_DATASOURCE_USERNAME = "rumbo"
$env:SPRING_DATASOURCE_PASSWORD = "TU_CONTRASEÑA"
$env:SPRING_JPA_DDL_AUTO = "update"
.\mvnw.cmd spring-boot:run
```

Se inicia con perfil `dev` por defecto. Para una base de datos compartida/autorizada,
cambia `SPRING_DATASOURCE_URL` y las credenciales en **tu terminal**, no en los archivos.

- API: `http://localhost:8080/api/v1/`
- Swagger: `http://localhost:8080/swagger-ui/index.html`
- Health: `http://localhost:8080/actuator/health`

`ddl-auto=update` es solo para bases de desarrollo vacías; en producción administra
el esquema mediante migraciones y utiliza `validate`.

## Probar con el frontend
En Angular ejecuta `npm start`. El proxy apunta a `localhost:8080` por defecto.
Si tienes un backend de demostración **remoto** desplegado, configura
`RUMBO_API_URL` desde la terminal de Angular. No es necesario copiar el
backend a todas las computadoras para compilar Angular.

## Perfiles y seguridad
- `dev`: desarrollo normal con PostgreSQL propio o remoto.
- `local`: pruebas con usuarios **ficticios** y cabecera `X-Rumbo-Demo-Role`,
  exclusivamente en la propia computadora; requiere habilitación explícita.
- `prod`: Render, PostgreSQL configurado por variables y APIs privadas protegidas
  provisionalmente por una clave de vista previa. **No equivale a IAM/JWT**.

No almacenar datos reales de estudiantes ni distribuir contraseñas de producción
mientras IAM y los permisos por rol no estén implementados.

## Bounded contexts
Routes & Trip Planning, Profiles/Students, Billing, Vehicles y Alerting.

### Catálogo público del curso

`RUMBO_COURSE_CATALOG_ENABLED=true` importa los planes Freemium, Premium y Plus del prototipo a PostgreSQL. El inicializador usa el servicio de comandos de Billing, respeta los planes existentes y se puede desactivar tras confirmar la importación.
