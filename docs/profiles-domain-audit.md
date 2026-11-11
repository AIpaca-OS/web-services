# Auditoría de Profiles & Relationship Management

Fecha: 10 de noviembre de 2026. Alcance actualizado: diagnóstico, aggregates revisados, contratos de repositorio de Domain (D5) y persistencia de Profile, Student, School y TutorStudentRelationship (I1–I6). No se implementaron Application ni REST ni se aplicaron cambios a la base de datos. Los apartados iniciales conservan el diagnóstico histórico; las continuaciones registran los avances.

## Reglas vigentes de ServiceEnrollment confirmadas por el usuario

Estas decisiones explícitas sustituyen los pendientes sobre afiliaciones registrados en las etapas anteriores de esta auditoría:

- Ciclo permitido: **PENDING -> ACCEPTED -> ENDED**. No se añaden rechazo ni cancelación de solicitudes pendientes en esta etapa.
- Una pareja **studentId + driverProfileId** no puede tener más de una afiliación en PENDING o ACCEPTED. Una PENDING también entra en conflicto con una ACCEPTED de esa misma pareja, y viceversa.
- Una afiliación ENDED conserva su historial y no bloquea una nueva solicitud de la misma pareja. Se crea otra afiliación; la anterior no se reabre. La regla no impone exclusividad entre conductores diferentes.
- Puede finalizar una afiliación ACCEPTED **un tutor con autorización vigente sobre ese estudiante o el conductor referenciado por la afiliación**. No basta con ser el solicitante original ni con tener rol DRIVER. requestedByProfileId del Command de finalización identifica al actor de esa operación, que puede diferir del solicitante original guardado en el aggregate.

La cronología y las transiciones ya están implementadas y probadas en el aggregate. Duplicidades y autorización quedan especificadas para Application y para la futura persistencia; declarar los puertos no ejecuta esas comprobaciones ni protege escrituras concurrentes.

## Reglas vigentes de TutorStudentRelationship confirmadas por el usuario

- relationship_type usa **VARCHAR(14)**, el mínimo que admite LEGAL_GUARDIAN. Esta decisión explícita corrige el VARCHAR(10) del diagrama de datos, incompatible con el enum existente. Se conservan los nombres completos; no se abreviaron valores ni se añadió un converter.
- Existe **una sola fila por pareja tutorProfileId–studentId**, tanto si está autorizada como revocada. Revocar y reautorizar reutiliza esa misma identidad. Se declara unicidad compuesta por tutor_profile_id y student_id, sin incluir authorization_status. Esto permite varios tutores por estudiante y varios estudiantes por tutor.
- Estas declaraciones de mapeo no se aplicaron a la base. Antes de aplicarlas se deberá revisar cualquier esquema o dato existente; la verificación real de unicidad y referencias queda pendiente de PostgreSQL.

## Fuentes y alcance de lectura

- Instrucciones del usuario y confirmación expresa de `Instant` en esta sesión; futura persistencia en `TIMESTAMPTZ`.
- `C:/Users/otoni/Downloads/project-report-develop.zip`: `project-report-develop/README.md`, US06–US08 (líneas 1001–1003), modelo de Profiles (§4.7.1, texto en líneas 1909–1914), Database Design (líneas 1983–1988) y endpoints de solicitudes (líneas 2914–2922). Se inspeccionaron visualmente `assets/chapter04/class-diagrams/new-profiles-class-diagram.png` y `assets/chapter04/database-diagrams/profile-data-diagram.png`.
- `C:/Desarrollo/Personal/Escritura de libros académicos/libro-open-source/`: inventario de capítulos, apéndices, componentes y notas editoriales; consulta técnica focalizada del capítulo 4, su propuesta y trazabilidad, el apéndice D, los resúmenes y autoevaluaciones del recorrido 1–7 y el inventario de eventos/ACL. No se afirma haber leído literalmente cada línea del libro ni revisado visualmente todas sus páginas, fuentes tipográficas o archivos de compilación.
- `C:/Users/otoni/Downloads/os10.2ALT.VTT`: contraste directo del tramo 00:44:08–00:49:17, aggregates sin anotaciones de persistencia y contratos CQRS. También se localizaron os9.1, os9.2, os10.1, os11.1 y os11.2; no se afirma haber reproducido todas las grabaciones.
- Apuntes propios/Granola en `C:/Desarrollo/Personal/Escritura de libros académicos/fuentes-referencia/apuntes-estudiante-y-granola.txt`.
- Referencia en `C:/Desarrollo/UPC/Open Source/spring-boot/daos-learning-center-platform-v2620`: estructura, ADRs, `profiles/domain/model/aggregates/Profile`, `learning/domain/model/aggregates/Enrollment`, `ProfileRepository`, `ProfilePersistenceAssembler` y los command services de Profile y Enrollment.

La descripción de `reconstitute()` en las notas del libro se identifica como complemento del código de referencia. No se atribuye su explicación literal al profesor en el VTT.

## Configuración real y arquitectura

El POM declara Spring Boot **4.1.2**, Java **27**, Lombok **1.18.48**, Springdoc **3.1.1**, JJWT **0.13.0** y pluralize **1.0.0**. Incluye Data JPA, Validation, Web MVC, PostgreSQL, Security, DevTools y starters de pruebas. Las versiones administradas de Hibernate y del driver provienen del parent; no se modificaron dependencias.

El código actual contiene únicamente `profiles` y `shared`. Tener dependencias de Security/JJWT no significa que IAM o la autorización funcional estén implementados. No se encontraron clases IAM ni de ejecución de viajes en esta rama.

`shared` ya aporta raíz de dominio, resultados de aplicación, publicación de eventos, base persistente auditable, estrategia de nombres, documentación, configuración de hilos virtuales, i18n y traducción HTTP de errores. Los aggregates concretos conservan su propio ID; la raíz compartida aporta eventos y no mapeo JPA.

La referencia separa dominio y persistencia (ADR-004), usa reconstitución (ADR-008) y declara servicios en `application/commandservices` y `application/queryservices`, con implementaciones en `application/internal/...`. Los adapters, assemblers y repositorios JPA corresponden a Infrastructure. No debe copiarse el mapeo JPA del Mini al Domain de Rumbo.

Orden del libro: D1 valores, D2 entidades internas si existen, D3 intenciones, D4 aggregates, D5 puertos de repositorio, D6 excepciones necesarias; luego I1 converters, I2 embeddables, I3 persistence entities, I4 assemblers, I5 repositorios JPA e I6 adapters; después A1–A3 y R1–R3. Referencias concretas: capítulo 4, D4; apéndice D, «Definir el modelo y sus puertos».

## Estado inicial

Había cuatro aggregates con cambios locales, incluso diferencias entre índice y working tree; se preservó ese trabajo. `qodana.yaml` ya estaba sin seguimiento. No se hicieron commits, staging ni cambios en otros bounded contexts.

| Componente | Estado encontrado | Evaluación |
|---|---|---|
| 7 Value Objects | AccountId, PersonName y cinco enums | Definidos; AccountId encapsula Long positivo. |
| 11 Commands | Validaciones en constructores compactos | Definidos; las cuatro eliminaciones de parámetros solicitadas ya estaban aplicadas. |
| 14 Queries | Perfiles, estudiantes, relaciones, afiliaciones, colegios y solicitudes | Definidas; una admitía IDs inválidos. No son por sí solas una comprobación de autorización. |
| Profile | Construcción desde Command, reconstitución y actualización de teléfono | Base coherente; unicidad por cuenta requiere persistencia y Application. |
| Student | Registro, actualización y reconstitución | Falta la operación del diagrama `markAnonymized`; no existe aún una política comprobada de anonimización ni un ciclo completo para StudentStatus. |
| TutorStudentRelationship | Autorización, revocación, reautorización y reconstitución | Faltaba comprobar orden temporal al reconstituir. La identidad de quien autoriza debe verificarse en Application. |
| ServiceEnrollment | Solicitar, aceptar, finalizar y reconstituir | Implementa String PENDING/ACCEPTED/ENDED; el diagrama solo declara String y accept/end. El conjunto exacto y las duplicidades compatibles aún necesitan definición funcional. |
| DataDeletionRequest | Ausente | Implementado en esta etapa tras aclarar las fechas. |
| School | Ausente | Siguiente aggregate: id, name, address y district según diagrama. No se inventaron operaciones CRUD. |
| Domain repositories | Ausentes | Etapa siguiente a cerrar el modelo. |
| Persistencia, servicios y REST de Profiles | Ausentes | No hay aún funcionalidades completas de usuario. |

`preferredLanguage` y `educationLevel` no aparecen en el código Java actual. No existen todavía resources ni persistence entities de Profiles de los cuales retirarlos. No se ejecutó ninguna modificación de esquema.

## Correspondencia de intenciones y modelo

| Aggregate | Commands existentes | Queries existentes |
|---|---|---|
| Profile | CreateProfile, UpdateProfileContact | GetProfileById, GetProfileByAccountId |
| Student | RegisterStudent, UpdateStudentInformation | GetStudentById, GetAllStudentsByTutorId |
| TutorStudentRelationship | AuthorizeTutorStudentRelationship, RevokeTutorStudentRelationship | GetTutorStudentRelationshipById, GetTutorStudentRelationshipByTutorIdAndStudentId, GetAllTutorStudentRelationshipsByTutorId, GetAllTutorStudentRelationshipsByStudentId |
| ServiceEnrollment | RequestServiceEnrollment, AcceptServiceEnrollment, EndServiceEnrollment | GetAllServiceEnrollmentsByDriverId, GetAllServiceEnrollmentsByStudentId |
| DataDeletionRequest | RequestStudentDataDeletion, ResolveDataDeletionRequest | GetDataDeletionRequestById, GetAllDataDeletionRequestsByRequestedByProfileId |
| School | Ninguno | GetSchoolById, GetAllSchools |

Se omiten los sufijos Command/Query en la tabla. Los Commands de actualización y transición no necesitan convertirse en constructores: el handler debe buscar el aggregate por ID y llamar a su método de negocio. Los nombres tutorId/driverId en Queries deberán documentarse como IDs de Profile al definir sus servicios; no son AccountId.

US06 requiere que Student y su relación inicial autorizada se guarden en una misma transacción. US07 exige autorización vigente del actor y revocación efectiva del acceso; además menciona dejar de recibir notificaciones. Esa última consecuencia requerirá integración con el contexto correspondiente. US08 registra una solicitud pendiente aun cuando exista un viaje sin terminar; no debe rechazarse automáticamente por esa condición ni completarse la supresión antes del cierre.

## Inconsistencias y decisiones

1. **Identidades y atributos de diagramas antiguos:** UUID, preferredLanguage y educationLevel quedan sustituidos por las decisiones explícitas del usuario: Long y eliminación de esos atributos. No se migró ninguna tabla.
2. **Fechas de supresión:** el diagrama muestra LocalTime y TIME; incluye expresamente una nota de validar TIMESTAMPTZ. US08 exige fecha y el contrato JSON muestra fecha/hora UTC. El usuario confirmó Instant para Domain y TIMESTAMPTZ para persistencia futura. Se conserva el instante completo incluso si la resolución sucede al día siguiente.
3. **Contrato de resolución:** el diagrama muestra `resolve(note)` sin resultado, mientras el Command existente distingue APPROVED y REJECTED y exige nota de hasta 500 caracteres. La implementación recibe resultado, nota e instante; así evita inferir un resultado o una fecha silenciosamente. No agrega un handler de Application.
4. **APIs históricas demasiado generales:** el reporte muestra GET globales y edición directa de estado/fechas. El prompt actual exige acceso por identidad autenticada y autorización. No se implementaron esos endpoints generales ni se dio autoridad al cliente para elegir su identidad.
5. **Alumno anonimizado:** el constructor de Student admite combinaciones de status/anonymizedAt y conserva nombre/fecha. No se inventó un estado ANONYMIZED ni valores sustitutos; falta acordar qué datos suprimir y cómo conservar historial de viajes (US06). Queda pendiente antes de implementar eliminación efectiva.
6. **Afiliaciones:** inicialmente faltaba consolidar estados, duplicidades y quién puede finalizar. El usuario resolvió esos puntos; consultar «Reglas vigentes de ServiceEnrollment» al inicio de este documento. No se añadieron estados de Learning Center.
7. **Actores y viajes:** faltan IAM y un contrato verificable de consulta a viajes. La futura comprobación no puede sustituirse por un booleano constante ni por IDs aportados sin comprobar.

## Correcciones mínimas realizadas

- `GetProfileByAccountIdQuery`: rechaza null, cero y negativos, como AccountId.
- `Student.updateInformation`: valida todos los nuevos datos antes de modificar el estado. Una fecha nula ya no deja cambiado el nombre.
- `TutorStudentRelationship`: reconstitución rechaza revocación anterior a autorización, igual que el método de negocio revoke.
- Nuevo `DataDeletionRequest`: hereda de AbstractDomainAggregateRoot, ID Long, referencias por Long, enum existente, requestedAt/resolvedAt como Instant y nota. Alta pendiente sin ID, constructor con Command, variante con fecha suministrada y fábrica reconstitute que conserva identidad y valida estado.
- La solicitud solo pasa de PENDING a APPROVED o REJECTED. Un resultado final no se reemplaza; una resolución inválida no modifica el objeto. Una solicitud pendiente no puede reconstituirse con datos de resolución; una resuelta requiere fecha no anterior a la solicitud y nota válida. No hay setters, anotaciones JPA, acceso a repositorios, eventos inventados ni anonimización automática.
- Pruebas unitarias en `ProfilesDomainConsistencyTests` y `DataDeletionRequestTests`, sin Spring ni PostgreSQL.

Las invariantes de una solicitud son responsabilidad del aggregate. La autorización, las consultas a otros aggregates/contextos y la decisión de permitir completar la atención pertenecen al Command Service. Esta separación permite probar el dominio sin infraestructura y evita introducir consultas de BD dentro del modelo.

## Verificación ejecutada

- Compilación inicial: `BUILD SUCCESS`, 50 archivos, release 27.
- Suite completa durante las correcciones iniciales: cuatro pruebas unitarias correctas y un error en `RumboWebServicesApplicationTests`. No encuentra @SpringBootConfiguration: el test está en com.aipaca.rumbo y la aplicación en com.aipaca.rumbo.platform. Es previo al aggregate nuevo; no llegó a conectarse a PostgreSQL. No se cambió ese test de integración en esta etapa de Domain.
- Verificación final: compilación de main/test y **15 pruebas unitarias correctas**, 0 fallos, 0 errores: 11 casos de solicitudes y 4 de consistencia. Maven terminó con `BUILD SUCCESS`.
- `git diff --check`: sin errores de whitespace.
- No se probó arranque real ni REST ni PostgreSQL. Domain aún no tiene adapters/controladores. El perfil dev usa ddl-auto=update y prod usa validate; no se arrancó contra una base existente para evitar modificaciones de esquema fuera de esta etapa.

La terminal seleccionaba Java 17; se utilizó el JDK ya instalado en `C:/Users/otoni/.jdks/openjdk-27.0.1.0.3` y Maven 3.10.0 ya instalado. Se resolvieron dependencias faltantes, sin cambiar pom.xml ni configuración del IDE. Para repetir las pruebas con ese entorno:

```powershell
$env:JAVA_HOME = 'C:/Users/otoni/.jdks/openjdk-27.0.1.0.3'
$profilesMaven = 'C:/Users/otoni/.m2/wrapper/dists/apache-maven-3.10.0-bin/39q0qgmngdu329psje7bjgne79/apache-maven-3.10.0/bin/mvn.cmd'
& $profilesMaven --batch-mode --no-transfer-progress '-Dmaven.repo.local=C:/Users/otoni/.m2/repository' '-Dtest=ProfilesDomainConsistencyTests,DataDeletionRequestTests' test
```

## Orden recomendado tras la auditoría inicial (histórico)

1. Construir School con sus atributos confirmados y reconstitución; verificar límites de longitud con el diagrama de datos.
2. Acordar el ciclo de ServiceEnrollment y la política exacta de anonimización de Student. Definir quién resuelve solicitudes y mediante qué identidad autenticada. Antes de eliminación efectiva, concretar el contrato de consulta a viajes y la coordinación transaccional.
3. Cerrar Domain y sus pruebas. Diseñar puertos por necesidades reales: lecturas de vistas autorizadas, búsquedas internas, existencia y unicidad. No crear findAll de estudiantes para exponerlo globalmente.
4. Implementar I1–I6 con correspondencia de columnas y restricciones. Documentar cualquier cambio de tipos frente al esquema existente antes de ejecutarlo.
5. Implementar A1–A3 con handle, Result y transacciones para operaciones de varios aggregates; no asumir que capturar una excepción como Failure provoca rollback automáticamente.
6. Implementar R1–R3 con identidad autenticada, control de acceso y tratamiento de errores de shared. Después verificar REST y PostgreSQL en un entorno de prueba explícito.
7. Corregir la localización de configuración de contextLoads y preparar una prueba de arranque con base de pruebas antes de afirmar que la suite completa y el backend funcionan.

## Continuación: School completado

En la siguiente etapa de esta misma sesión se implementaron `School.java` y `SchoolTests.java`. Este apartado actualiza el pendiente de School del diagnóstico inicial y el primer punto del orden recomendado anterior.

Se cotejaron nuevamente los diagramas de clases y de datos de Profiles dentro de `project-report-develop.zip`. School conserva `Long id` por decisión expresa del usuario, y `String name`, `String address` y `String district` según el modelo. Los máximos de 150, 255 y 100 caracteres proceden respectivamente de las columnas VARCHAR del diagrama de datos. Se exige contenido no nulo ni blanco en los tres textos como validación de construcción; no se recortan ni normalizan silenciosamente los valores.

Se aplicó el patrón de D4 del libro y de `Profile` en Learning Center: herencia de `AbstractDomainAggregateRoot<School>`, constructor de alta con ID nulo y `reconstitute` con ID positivo existente. Ambos caminos comparten las validaciones del estado. Lombok aporta getters. No se añadieron setters, JPA, eventos, Commands nuevos ni operaciones de actualización que el diagrama de School no declara. Las consultas existentes GetSchoolById y GetAllSchools se conservaron. Student sigue referenciando al colegio por schoolId; comprobar su existencia exige el futuro repositorio y servicio de aplicación.

Verificación ejecutada con JDK 27: compilación de 52 fuentes principales y 4 fuentes de prueba; `SchoolTests` pasó 21 casos de creación, reconstitución, IDs inválidos, valores ausentes/blancos y límites de longitud. Junto con las 15 pruebas anteriores de Domain, el resultado fue **36 pruebas, 0 fallos, 0 errores, BUILD SUCCESS**. Se utilizó el mismo comando documentado arriba cambiando el selector a `-Dtest=SchoolTests,ProfilesDomainConsistencyTests,DataDeletionRequestTests`. `git diff --check` no encontró errores.

No se volvió a ejecutar el test de arranque con el fallo de configuración ya documentado. No se comprobó PostgreSQL ni se avanzó a persistencia. Antes de cerrar Domain siguen pendientes las decisiones de ServiceEnrollment, anonimización de Student y los contratos de integración y autorización descritos anteriormente.

## Continuación: contraste de ServiceEnrollment

Se revisaron el código real, sus tres Commands, sus dos Queries y las fuentes del ZIP del Project Report: README (US12, línea 1007; modelo de Profiles, línea 1911; descripción de datos, línea 1985), los diagramas de clases y de datos, el event storming `assets/chapter04/event-storming/ddd-profile.png` y el diagrama de componentes `assets/chapter04/software-architecture/diagram-profiles.png`.

El reporte confirma que ServiceEnrollment vincula estudiante, conductor y solicitante, con status String, requestedAt/acceptedAt/endedAt como Instant y métodos accept(at)/end(at). El esquema representa esas fechas con TIMESTAMPTZ. No enumera los valores permitidos de status, no define rechazo o cancelación de solicitudes pendientes, no especifica la exclusividad de afiliaciones simultáneas ni quién puede finalizar una afiliación. Las multiplicidades 0..* no distinguen afiliaciones históricas de simultáneas. El event storming y los componentes tampoco completan ese flujo.

US12 exige vinculación autorizada para asignar a una ruta y evita repetir la misma asignación para ruta y turno. Esa regla pertenece a Route & Trip Planning: no demuestra que un estudiante solo pueda tener una afiliación de servicio ni establece los estados de ServiceEnrollment. No se importaron routeId, turno, paradas ni viajes a este aggregate.

Learning Center sirve de referencia para herencia, identidad, reconstitución y métodos de negocio. Su Enrollment usa REQUESTED/CONFIRMED/REJECTED/CANCELED para matrículas académicas; esos nombres y reglas no se trasladaron al transporte escolar.

### Comportamiento actual verificado y propuesta mínima

| Estado actual | Fechas coherentes | Operación permitida | isActive |
|---|---|---|---|
| PENDING | requestedAt obligatorio; acceptedAt y endedAt nulos | accept(at) | false |
| ACCEPTED | requestedAt <= acceptedAt; endedAt nulo | end(at) | true |
| ENDED | requestedAt <= acceptedAt <= endedAt | Ninguna transición adicional | false |

La implementación ya rechaza IDs inválidos, estados desconocidos, fechas nulas obligatorias, combinaciones incoherentes y cronología inversa. Las transiciones repetidas o fuera de orden fallan antes de modificar el estado. No se encontró un defecto de transición que exigiera reescribir la clase. Se añadió documentación sobre la separación respecto de rutas/viajes y sobre las comprobaciones externas que corresponden a Application.

La propuesta mínima de esa etapa fue conservar PENDING -> ACCEPTED -> ENDED, el tipo String del contrato actual y los Commands existentes; no añadir rechazo, cancelación, un enum nuevo ni cambios en persistencia sin una necesidad funcional confirmada. El ciclo se sometió a confirmación del usuario y posteriormente quedó confirmado, como registra el apartado de reglas vigentes.

También se consultó si la exclusión de otra PENDING/ACCEPTED debía comprobarse por pareja estudiante-conductor o por estudiante frente a cualquier conductor, y quién podía finalizar una afiliación. En esa etapa se esperó la respuesta; la confirmación posterior consta en las reglas vigentes y se refleja en los contratos D5. Comprobar perfiles TUTOR/DRIVER, autorización vigente del tutor, identidad autenticada, duplicidades y condiciones de viajes requiere información externa: no se resolvió mediante consultas dentro del aggregate ni mediante booleanos provisionales. El ID del actor en un Command no prueba autorización.

### Pruebas de esta etapa

Se creó `ServiceEnrollmentTests.java`: **26 casos exitosos** de creación, actividad del servicio, ciclo existente, fechas iguales, cruce de día, reconstitución de los tres estados, transiciones inválidas, conservación del estado ante errores, estados/fechas incoherentes, fechas obligatorias e IDs/referencias positivas.

Maven compiló 52 fuentes principales y 5 fuentes de prueba con JDK 27. El comando con selector `-Dtest=ServiceEnrollmentTests,SchoolTests,ProfilesDomainConsistencyTests,DataDeletionRequestTests` terminó con **62 pruebas, 0 fallos, 0 errores, BUILD SUCCESS**. El comentario de documentación del aggregate se añadió después; no cambió su comportamiento. `git diff --check` pasó. No se volvió a ejecutar contextLoads ni se comprobó PostgreSQL.

En esa etapa todavía no se avanzó a repositorios de Domain: se esperaba la confirmación de las políticas consultadas. Esa consulta quedó resuelta y dio paso a D5, descrito a continuación.

## Continuación: contratos de repositorio de Domain (D5)

Referencia concreta: `profiles/domain/repositories/ProfileRepository.java` y `learning/domain/repositories/EnrollmentRepository.java` de Learning Center. Se crearon seis interfaces bajo `profiles/domain/repositories`, una por aggregate con estado persistible. Utilizan aggregates, AccountId y tipos de Java; no extienden JpaRepository ni contienen anotaciones Spring/JPA o implementaciones en memoria.

### Operaciones y motivo de cada puerto

| Puerto | Consultas | Comprobación o uso interno | Escritura |
|---|---|---|---|
| ProfileRepository | findById, findByAccountId(AccountId) | existsByAccountId para la cuenta con un solo Profile | save |
| StudentRepository | findById | findAllById(Collection<Long>) para cargar un conjunto acotado de estudiantes tras obtener las relaciones autorizadas | save |
| TutorStudentRelationshipRepository | findById, findByTutorProfileIdAndStudentId, findAllByTutorProfileId, findAllByStudentId | La búsqueda por pareja permite comprobar isAuthorized() y decidir entre alta o reautorización según el caso de uso | save |
| ServiceEnrollmentRepository | findById, findAllByDriverProfileId, findAllByStudentId | existsPendingOrAcceptedByStudentIdAndDriverProfileId, para la regla de duplicidad confirmada | save |
| DataDeletionRequestRepository | findById, findAllByRequestedByProfileId | La lectura por ID aporta estudiante, solicitante y estado para el caso de uso autorizado | save |
| SchoolRepository | findById, findAll del catálogo | findById también permite verificar la referencia opcional de Student | save |

Cada save devuelve el aggregate guardado, con la identidad que deberá devolver el futuro adaptador. Las búsquedas individuales devuelven Optional para representar ausencia; las colecciones devuelven List, vacía si no hay resultados. No se agregaron borrados físicos: la supresión de datos conserva su procedimiento pendiente. Tampoco se añadieron búsquedas globales de estudiantes, perfiles, relaciones, afiliaciones o solicitudes, ni métodos existsById que repitan una lectura ya necesaria. School conserva findAll porque GetAllSchoolsQuery consulta un catálogo; save permite almacenar el aggregate, sin inventar un endpoint o Command de administración.

### Correspondencia con las 14 Queries

| Queries existentes | Operaciones que las soportan |
|---|---|
| GetProfileById, GetProfileByAccountId | ProfileRepository.findById / findByAccountId; el Long recibido se convierte a AccountId |
| GetStudentById | StudentRepository.findById, después de verificar acceso |
| GetAllStudentsByTutorId | TutorStudentRelationshipRepository.findAllByTutorProfileId; filtrar por isAuthorized; después StudentRepository.findAllById con esos IDs |
| GetTutorStudentRelationshipById | TutorStudentRelationshipRepository.findById |
| GetTutorStudentRelationshipByTutorIdAndStudentId | TutorStudentRelationshipRepository.findByTutorProfileIdAndStudentId |
| GetAllTutorStudentRelationshipsByTutorId, GetAllTutorStudentRelationshipsByStudentId | TutorStudentRelationshipRepository.findAllByTutorProfileId / findAllByStudentId |
| GetAllServiceEnrollmentsByDriverId, GetAllServiceEnrollmentsByStudentId | ServiceEnrollmentRepository.findAllByDriverProfileId / findAllByStudentId |
| GetDataDeletionRequestById, GetAllDataDeletionRequestsByRequestedByProfileId | DataDeletionRequestRepository.findById / findAllByRequestedByProfileId |
| GetSchoolById, GetAllSchools | SchoolRepository.findById / findAll |

Los parámetros tutorId y driverId de las Queries se interpretan como IDs de Profile, no como AccountId. No se renombraron contratos existentes. La lectura singular por pareja tutor-estudiante sigue la Query singular y el aggregate que admite reautorización; no añade un historial separado de relaciones.

Las listas de relaciones incluyen también revocadas para representar los vínculos y sus estados, pero esas filas nunca conceden acceso. Para el listado de estudiantes se filtra explícitamente la autorización vigente antes de obtener los IDs. El contrato de findAllById exige que una colección vacía produzca una lista vacía, sin sustituirla por una lectura global. Todas las consultas de información personal quedan sujetas a la identidad autenticada y a las comprobaciones del futuro servicio.

### Aplicación futura de las reglas confirmadas

Al solicitar afiliación, Application deberá comprobar existencia/tipo de perfiles, estudiante y autorización vigente, consultar el conflicto de esa pareja y guardar solo si no existe otra PENDING/ACCEPTED. ENDED no cuenta como conflicto. `existsPendingOrAcceptedByStudentIdAndDriverProfileId` tiene significado de dominio: no es un nombre de consulta derivada que deba copiarse sin traducción a Spring Data. El adapter deberá implementar exactamente ese predicado.

Al aceptar se trabaja sobre la afiliación PENDING encontrada por ID y se verifica la identidad del conductor. El pre-chequeo de duplicidad se usa para crear una solicitud; no debe bloquearse la aceptación porque la propia solicitud ya sea PENDING.

Al finalizar, el servicio obtendrá el Profile del actor autenticado y la afiliación por ID. Permitirá al conductor cuyo Profile ID coincide con driverProfileId, o a un Profile TUTOR cuya relación con studentId exista y esté autorizada. Un tutor revocado, un conductor diferente y el solicitante original sin permiso actual no quedan autorizados por el mero valor de sus IDs. Después invocará end(at), que exige ACCEPTED. Ninguna de estas comprobaciones se ejecuta todavía: en esta etapa se declararon contratos y se documentó la coordinación.

La comprobación de existencia previa a guardar no evita por sí sola dos altas simultáneas. La futura Infrastructure debe hacer cumplir la exclusión por pareja para PENDING/ACCEPTED también bajo concurrencia, por ejemplo mediante una restricción única parcial de PostgreSQL y traducción del conflicto. Ese diseño de esquema se informará antes de ejecutarlo; no se creó SQL, índice ni migración ahora. La unicidad de AccountId requiere igualmente respaldo persistente. Las comprobaciones de viajes pendientes siguen perteneciendo a la integración correspondiente, no a estos repositorios.

### Verificación de D5

Se compilaron **58 fuentes principales y 5 fuentes de prueba** con JDK 27. La regresión con selector `-Dtest=ServiceEnrollmentTests,SchoolTests,ProfilesDomainConsistencyTests,DataDeletionRequestTests` terminó con **62 pruebas, 0 fallos, 0 errores y BUILD SUCCESS**. `git diff --check` pasó. No se añadieron pruebas que solo reflejen las firmas de interfaces sin implementación. La semántica de consultas, save y restricciones deberá comprobarse con pruebas reales de los futuros adapters. No se ejecutó contextLoads, ya diagnosticado, ni se comprobó PostgreSQL. La anonimización y la resolución de solicitudes de eliminación conservan los pendientes funcionales de sus etapas anteriores.

## Continuación: persistencia de Profile, I1–I6 sin modificar la base

Se implementó exclusivamente el recorrido de persistencia de Profile bajo `profiles/infrastructure/persistence/jpa`. Se conservaron el aggregate y el puerto de Domain, shared, el POM y los properties. La base actual no se inspeccionó ni modificó. No se arrancó Spring Boot ni se ejecutaron migraciones o SQL.

Referencias concretas: los seis archivos equivalentes de `profiles/infrastructure/persistence/jpa` de Learning Center y el capítulo 5 del libro. Para I6 se siguió la versión base del listado `lst:cap5-profile-adapter`, sin la extensión E3 de publicación de eventos. Rumbo no tiene aún un evento ProfileCreated ni el método onCreated; no se inventaron para completar persistencia. Se volvió a consultar el diagrama de datos de Profiles del Project Report para las columnas y longitudes, aplicando las decisiones posteriores de Long y eliminación de preferredLanguage.

### Archivos y decisiones de implementación

| Paso | Archivo nuevo | Decisión |
|---|---|---|
| I1 | converters/AccountIdPersistenceConverter.java | AttributeConverter<AccountId, Long>, autoApply=false y uso explícito en la entidad. Null se conserva; un valor almacenado no positivo falla al construir el VO. |
| I2 | embeddables/PersonNamePersistenceEmbeddable.java | first_name y last_name en la misma fila, ambos obligatorios y VARCHAR(100), sin añadir JPA al record del dominio. |
| I3 | entities/ProfilePersistenceEntity.java | Tabla profiles, herencia de AuditableAbstractPersistenceEntity, AccountId convertido, ProfileType como STRING, nombre embebido y teléfono. |
| I4 | assemblers/ProfilePersistenceAssembler.java | Convierte representaciones y llama a Profile.reconstitute con la identidad persistida; no ejecuta consultas ni asigna IDs por su cuenta. |
| I5 | repositories/ProfilePersistenceRepository.java | JpaRepository<ProfilePersistenceEntity, Long>, más búsqueda y conteo JPQL por AccountId. |
| I6 | adapters/ProfileRepositoryImpl.java | Implementa las cuatro operaciones del puerto ProfileRepository y recibe el repositorio JPA por constructor. |

El tipo ProfileType usa @Enumerated(STRING) para almacenar TUTOR/DRIVER, sin depender de sus posiciones ordinales. Su columna tiene longitud 10; phone_number tiene longitud 20. El ID, las fechas y su listener de auditoría se heredan de shared. No se añadió una asociación JPA ni una FK a una entidad Account inexistente: AccountId es la referencia externa al contexto IAM.

El assembler deja el ID nulo en un alta y copia el ID de un aggregate existente en una actualización. El adaptador reconstruye desde la entidad **devuelta** por save, que puede ser una instancia distinta de la enviada y contiene la identidad asignada por JPA. Las búsquedas mantienen Optional.empty cuando no existe una fila. La auditoría permanece en infraestructura y no se rellena artificialmente desde Domain.

### Unicidad de AccountId

Se declaró account_id como **BIGINT NOT NULL** y una restricción única de tabla nombrada **uk_profiles_account_id** sobre esa única columna. Así una cuenta no puede tener un Profile TUTOR y otro DRIVER: la unicidad no es por pareja account_id/profile_type. La restricción está presente en los metadatos, pero todavía no se instaló en PostgreSQL.

El puerto existsByAccountId se implementa mediante countByAccountId > 0, siguiendo el conteo por correo de Learning Center. Una consulta previa será útil para el futuro Command Service, pero no elimina carreras entre peticiones. La garantía concurrente requerirá que la restricción exista efectivamente en la base. save no captura ni silencia DataIntegrityViolationException; la propaga para que la futura capa de aplicación traduzca el conflicto sin informar un éxito inexistente. Una prueba con excepción simulada verifica esa propagación, no el rechazo real de duplicados por PostgreSQL.

### Correspondencia y consecuencias para una aplicación futura del esquema

- El mapeo incluye id, account_id, profile_type, first_name, last_name, phone_number, created_at y updated_at. No incluye preferred_language.
- Los IDs numéricos sustituyen UUID en el modelo antiguo por decisión del usuario. No se intentó convertir una columna UUID existente ni se comprobó qué esquema tiene la base actual.
- shared aporta createdAt/updatedAt como java.util.Date. Se reutilizó esa base sin cambiarla; updated_at es una adición respecto del diagrama mostrado. La correspondencia temporal exacta con el TIMESTAMPTZ del reporte debe revisarse antes de aplicar el esquema, no se presume comprobada con PostgreSQL.
- Las longitudes de nombres/teléfono proceden del reporte. No se añadieron cambios a las validaciones de Domain ni a recursos HTTP todavía inexistentes. La futura validación de entrada deberá respetar esos límites antes de guardar.
- Aplicar la unicidad requerirá revisar primero posibles cuentas duplicadas y valores nulos en cualquier dato existente. Las anotaciones no modifican ni eliminan esos datos durante estas pruebas.
- El perfil dev conserva ddl-auto=update: un futuro arranque podría intentar aplicar el nuevo mapeo. Antes de hacerlo corresponde revisar el esquema y sus diferencias. Esta tarea no arrancó la aplicación ni cambió esa configuración.

### Pruebas ejecutadas sin conexiones JDBC

| Clase de prueba nueva | Casos | Qué verifica |
|---|---|---|
| AccountIdPersistenceConverterTests | 6 | Conversión numérica de ida y vuelta, null y rechazo de valores almacenados inválidos. |
| ProfilePersistenceAssemblerTests | 4 | Alta sin ID, reconstitución para ambos tipos de perfil, identidad persistida y validación del dominio al leer datos inválidos. |
| ProfileRepositoryImplTests | 10 | Búsquedas presentes/ausentes, diferencia entre ID de Profile y AccountId, conteo, alta/actualización y propagación de una violación de integridad simulada. |
| ProfilePersistenceMappingTests | 4 | Construcción real de metadatos de Hibernate con dialecto PostgreSQL, unicidad de account_id, tipo BIGINT, nulabilidad/longitudes y análisis de ambos JPQL con parámetro AccountId. |

Las pruebas del adaptador utilizan Mockito, sin Spring ni base de datos. La prueba de metadatos no carga application-dev.properties: deshabilita acceso a metadatos JDBC y generación de esquema y registra un ConnectionProvider simulado que lanza AssertionError si se solicita una conexión. Al finalizar verifica que getConnection nunca se llamó. Las consultas se crean y parametrizan para validar su semántica, pero no se ejecutan.

Resultado: **64 fuentes principales y 9 fuentes de prueba compiladas con JDK 27; 86 pruebas, 0 fallos, 0 errores, BUILD SUCCESS**: 62 de Domain y 24 de esta persistencia. El selector de Maven fue:

```text
-Dtest=ServiceEnrollmentTests,SchoolTests,ProfilesDomainConsistencyTests,DataDeletionRequestTests,AccountIdPersistenceConverterTests,ProfilePersistenceAssemblerTests,ProfileRepositoryImplTests,ProfilePersistenceMappingTests
```

`git diff --check` pasó. No se modificaron dependencias. No se repitió contextLoads con su fallo previo documentado. No se comprobó arranque, INSERT/UPDATE, generación real de IDs/auditoría, consultas ejecutadas ni unicidad concurrente contra PostgreSQL. Esas comprobaciones deberán hacerse en una base de pruebas y con un esquema revisado; estos resultados no equivalen a funcionamiento real de persistencia en PostgreSQL.

## Continuación: persistencia de Student, I1–I6 sin modificar la base

Se implementó el recorrido de Student conservando el aggregate, sus Commands, el puerto StudentRepository y los componentes compartidos existentes. Referencias: entidad, assembler, repositorio y adaptador de Student en `learning/infrastructure/persistence/jpa` de Learning Center; versión base del procedimiento I1–I6 del capítulo 5; y diagrama de datos de Profiles del Project Report. Se adaptaron los atributos a Rumbo: no se copiaron los identificadores académicos, métricas ni eventos de Learning Center.

### Pasos y reutilización

| Paso | Componente | Decisión |
|---|---|---|
| I1 | Tipos nativos de JPA/Hibernate | No se necesitan converters nuevos: Long, LocalDate e Instant ya tienen soporte; StudentStatus usa @Enumerated(STRING). |
| I2 | PersonNamePersistenceEmbeddable existente | Se reutiliza sin cambios, con first_name y last_name obligatorios de longitud 100. |
| I3 | entities/StudentPersistenceEntity.java | Tabla students; hereda identidad y auditoría de AuditableAbstractPersistenceEntity. Mapea nombre, birthDate, schoolId, status y anonymizedAt. |
| I4 | assemblers/StudentPersistenceAssembler.java | Copia el estado completo y llama a Student.reconstitute con la identidad devuelta por persistencia. |
| I5 | repositories/StudentPersistenceRepository.java | Extiende JpaRepository<StudentPersistenceEntity, Long>. Se reutilizan findById, findAllById y save; no hay consultas personalizadas redundantes. |
| I6 | adapters/StudentRepositoryImpl.java | Implementa las tres operaciones del puerto. findAllById recibe solo los IDs suministrados; con una colección vacía retorna una lista vacía sin consultar. |

El mapeo contiene **nueve columnas**: id, first_name, last_name, birth_date, school_id, status, anonymized_at, created_at y updated_at. No incluye education_level. El dominio sigue sin JPA y no se añadieron dependencias, configuraciones ni migraciones.

birth_date es DATE obligatorio; school_id es BIGINT opcional, coherente con el Long nullable del aggregate y con la decisión previa de identidades numéricas. status es VARCHAR(10), obligatorio, y conserva ACTIVE/INACTIVE como nombres, no ordinales. anonymized_at admite null y se mapea desde Instant; los metadatos con dialecto PostgreSQL confirmaron un timestamp con zona horaria. Las consideraciones previas sobre las fechas heredadas de shared y updated_at siguen aplicando.

### Límites y responsabilidades conservados

- School todavía no tiene persistence entity en esta etapa. school_id se mapea como identidad escalar, sin @ManyToOne, cascadas ni FK declarada. Esa columna por sí sola no garantiza existencia del colegio. La validación de existencia en Application y la restricción referencial del esquema deberán completarse al implementar la persistencia de School y revisar la base antes de aplicarla.
- No se inventó unicidad por nombre, nacimiento o colegio. La identidad propia del estudiante procede de la base compartida; puede haber varios estudiantes con datos coincidentes.
- anonymizedAt y status se conservan al guardar y reconstruir. El assembler no borra ni sustituye información personal, no cambia estados y no ejecuta solicitudes de supresión. La política de anonimización pendiente no se resolvió silenciosamente dentro de persistencia.
- StudentRepository mantiene el acceso por ID o conjunto de IDs, sin findAll global. Spring Data posee sus operaciones generales en infraestructura, pero el adaptador no las utiliza como sustituto de una consulta sin resultados. Corresponde a Application verificar identidad y autorización vigente antes de seleccionar y exponer esos estudiantes.
- No se crean relaciones de tutor automáticamente desde el adaptador. La creación de Student y su relación inicial autorizada debe coordinarse en una transacción del futuro caso de uso de US06.
- save reconstruye a partir de la entidad retornada por JPA: un alta conserva ID nulo hasta recibir el generado; una actualización envía el ID existente y puede conservar o quitar el schoolId opcional. Los fallos de integridad se propagan, sin convertirlos en guardados exitosos.

### Verificación ejecutada

Se reutilizó la preparación de metadatos anterior mediante `OfflineProfilesMappingTestSupport`, compartida por las pruebas de mapeo de Profile y Student. La preparación registra ambas entidades con la estrategia de nombres real, deshabilita generación de esquema y acceso a metadatos JDBC, y usa un ConnectionProvider simulado que falla ante cualquier intento de conexión. Cada clase de prueba tiene su propio estado y verifica al cerrar que no se solicitó conexión.

| Pruebas nuevas | Casos | Cobertura |
|---|---|---|
| StudentPersistenceAssemblerTests | 6 | Alta sin identidad, reconstrucción completa con ambos estados y colegio opcional, conservación de anonymizedAt sin anonimización y rechazo de datos almacenados inválidos. |
| StudentRepositoryImplTests | 8 | Lecturas presentes/ausentes, consulta acotada por IDs, colección vacía sin consultar, ausencia sin lectura global, alta, actualización con retiro del colegio opcional y propagación de errores. |
| StudentPersistenceMappingTests | 3 | Columnas/nulabilidad, tipos SQL nativos y longitudes, y reutilización del embeddable en tablas independientes. |

Maven compiló **68 fuentes principales y 13 fuentes de prueba con JDK 27**. El resultado fue **103 pruebas, 0 fallos, 0 errores, BUILD SUCCESS**: las 86 anteriores y 17 de Student. Las cuatro pruebas de mapeo de Profile siguieron pasando con la preparación compartida; la restricción única de AccountId se conserva. Se usó el selector:

```text
-Dtest=ServiceEnrollmentTests,SchoolTests,ProfilesDomainConsistencyTests,DataDeletionRequestTests,AccountIdPersistenceConverterTests,ProfilePersistenceAssemblerTests,ProfileRepositoryImplTests,ProfilePersistenceMappingTests,StudentPersistenceAssemblerTests,StudentRepositoryImplTests,StudentPersistenceMappingTests
```

`git diff --check` pasó. No se arrancó Spring Boot, no se repitió contextLoads y no se ejecutaron consultas, INSERT/UPDATE, SQL de esquema ni conexiones a PostgreSQL. Las lecturas y escrituras del adaptador se probaron con mocks; los metadatos se construyeron con Hibernate real sin conexión. La generación de IDs/auditoría, la ejecución real de findAllById y la futura FK de school_id todavía requieren verificación con una base de pruebas y un esquema revisado.

## Continuación: persistencia de School, I1–I6 sin modificar la base

Se completó exclusivamente la persistencia de School desde el estado real del repositorio, conservando su aggregate y el puerto SchoolRepository. Learning Center no aporta un School equivalente: se siguió el patrón de entidad, assembler, repositorio JPA y adaptador de sus contextos profiles/learning, junto con el recorrido base I1–I6 del capítulo 5. Los atributos y límites proceden del modelo de School de Rumbo y del Project Report, no de un caso de uso de School atribuido al profesor.

### Implementación y decisiones

| Paso | Componente | Decisión |
|---|---|---|
| I1 | Tipos nativos | Long y String no requieren converters propios. |
| I2 | Campos escalares del modelo existente | School no compone value objects; no se crearon embeddables artificiales ni se usó PersonName para el nombre de una institución. |
| I3 | entities/SchoolPersistenceEntity.java | Tabla schools, herencia de AuditableAbstractPersistenceEntity y columnas name/address/district obligatorias de longitud 150/255/100. |
| I4 | assemblers/SchoolPersistenceAssembler.java | Convierte ambas representaciones y reconstruye con School.reconstitute para conservar identidad e invariantes. |
| I5 | repositories/SchoolPersistenceRepository.java | JpaRepository<SchoolPersistenceEntity, Long>; reutiliza findById, findAll y save sin redeclararlas ni añadir consultas personalizadas. |
| I6 | adapters/SchoolRepositoryImpl.java | Implementa las tres operaciones del puerto Domain mediante inyección por constructor y traducción con el assembler. |

La entidad tiene seis columnas: id, name, address, district, created_at y updated_at. La identidad Long generada y las dos fechas de auditoría provienen de shared; esas fechas no figuran en el diagrama original de School, pero se conserva la base compartida utilizada por el proyecto. No se alteró su definición ni el dominio. Tampoco se impuso unicidad por nombre, dirección o distrito que el reporte no establece.

El alta envía ID nulo; la reconstrucción de una fila existente requiere un ID positivo. save utiliza la entidad devuelta por JPA para devolver la identidad persistida. findById conserva Optional.empty y findAll devuelve una lista de entradas del catálogo, incluida una lista vacía cuando corresponde a los resultados. Este catálogo respalda GetAllSchoolsQuery; no expone datos de estudiantes. Los fallos de persistencia se propagan. No se crearon eventos ni operaciones REST o de borrado ajenos al puerto actual.

### Integración y alcance de esquema

La existencia de SchoolPersistenceEntity permite preparar el mapeo de la tabla schools, pero no crea automáticamente una FK sobre la columna escalar Student.schoolId. StudentPersistenceEntity sigue sin cambios: la FK students.school_id -> schools.id continúa pendiente de definición explícita del mapeo/esquema y de revisión antes de aplicarlo. No se agregó una asociación redundante ni se simuló que esa integridad referencial ya estuviera resuelta. La futura Application deberá comprobar la existencia del colegio cuando Student reciba un schoolId.

No se modificaron properties, dependencias, otras capas ni otros bounded contexts. Se conserva la restricción de no aplicar todavía cambios a la base. Un futuro arranque dev con ddl-auto=update podría intentar crear o ajustar schools y sus campos auditables; aquí no se arrancó la aplicación ni se ejecutó SQL.

### Pruebas y resultado

Se amplió OfflineProfilesMappingTestSupport para registrar School junto a Profile y Student, manteniendo bloqueadas las conexiones JDBC y deshabilitada la generación de esquema. Las pruebas anteriores de ambos aggregates siguieron pasando.

| Clase nueva | Casos | Cobertura |
|---|---|---|
| SchoolPersistenceAssemblerTests | 7 | Alta sin identidad, reconstrucción completa, rechazo de identidades persistidas inválidas y datos incompatibles con Domain, y preservación de los límites máximos. |
| SchoolRepositoryImplTests | 7 | Búsqueda por ID presente/ausente, catálogo con resultados/vacío, alta con identidad devuelta por JPA, guardado de una identidad existente y propagación de fallos. |
| SchoolPersistenceMappingTests | 2 | Tabla y columnas, longitud/nulabilidad, ID BIGINT y clave primaria, sin restricciones únicas de texto inventadas. |

Verificación con JDK 27: **72 fuentes principales y 16 fuentes de prueba compiladas; 119 pruebas, 0 fallos, 0 errores, BUILD SUCCESS**. Corresponden a las 103 pruebas anteriores y 16 nuevas de persistencia de School. El selector usado fue:

```text
-Dtest=ServiceEnrollmentTests,SchoolTests,ProfilesDomainConsistencyTests,DataDeletionRequestTests,AccountIdPersistenceConverterTests,ProfilePersistenceAssemblerTests,ProfileRepositoryImplTests,ProfilePersistenceMappingTests,StudentPersistenceAssemblerTests,StudentRepositoryImplTests,StudentPersistenceMappingTests,SchoolPersistenceAssemblerTests,SchoolRepositoryImplTests,SchoolPersistenceMappingTests
```

`git diff --check` pasó. Los tests del adaptador usan mocks y los de metadatos construyen Hibernate real sin conexión, con verificación de que no se llamó a getConnection. No se comprobó arranque, generación real de IDs/auditoría, ejecución de consultas ni guardados contra PostgreSQL. contextLoads no se volvió a ejecutar; conserva el fallo de configuración previamente documentado. La persistencia de los demás aggregates y las decisiones pendientes de anonimización/integración siguen fuera de esta etapa.

## Continuación: persistencia de TutorStudentRelationship, I1–I6 sin modificar la base

Se completó el recorrido de persistencia del aggregate existente y sus cinco operaciones de repositorio. La arquitectura sigue la separación del dominio y la persistencia, los assemblers y los adapters de Learning Center. Las consultas derivadas siguen el patrón de los repositorios de su contexto learning. El modelo de la relación procede de Rumbo; no se atribuye al profesor una implementación específica de TutorStudentRelationship que no existe en esa referencia.

Antes de crear el mapeo se detectaron y consultaron dos decisiones de datos. El usuario confirmó **VARCHAR(14)** para relationship_type y **una fila por pareja**, reutilizada al revocar y reautorizar. Esas confirmaciones quedan registradas al inicio de esta auditoría. El cambio de longitud y la nueva unicidad solo se declararon en el código; no se ejecutó ninguna modificación del esquema.

### Pasos y componentes

| Paso | Componente | Decisión |
|---|---|---|
| I1 | Tipos nativos de JPA/Hibernate | Long e Instant tienen soporte directo; RelationshipType y AuthorizationStatus se almacenan con @Enumerated(STRING). No se añadieron converters. |
| I2 | Propiedades escalares del aggregate | No hay valores compuestos que requieran un embeddable nuevo. |
| I3 | entities/TutorStudentRelationshipPersistenceEntity.java | Tabla tutor_student_relationships, herencia de AuditableAbstractPersistenceEntity y restricción única compuesta por pareja. |
| I4 | assemblers/TutorStudentRelationshipPersistenceAssembler.java | Copia todo el estado y reconstruye mediante reconstitute, conservando las invariantes del dominio. |
| I5 | repositories/TutorStudentRelationshipPersistenceRepository.java | JpaRepository con consultas derivadas por pareja, tutor y estudiante; findById/save se heredan. |
| I6 | adapters/TutorStudentRelationshipRepositoryImpl.java | Implementa las cinco operaciones del puerto mediante el repositorio JPA y el assembler. |

La tabla tiene diez columnas: id, tutor_profile_id, student_id, authorized_by_profile_id, relationship_type, authorization_status, authorized_at, revoked_at, created_at y updated_at. Las referencias son BIGINT, coherentes con los Long del dominio. tutor_profile_id y student_id son obligatorios; authorized_by_profile_id admite null porque reconstitute y el modelo de clases permiten una referencia opcional al autorizador. authorized_at es obligatorio y revoked_at opcional. Ambos Instant se mapean como timestamp con zona horaria usando el dialecto PostgreSQL. authorization_status conserva longitud 10, suficiente para AUTHORIZED/REVOKED. Las columnas de auditoría provienen de shared, como en las entidades anteriores.

Se declaró **uk_tutor_student_relationships_tutor_student** sobre tutor_profile_id y student_id. La restricción no separa filas autorizadas de revocadas: una reautorización debe trabajar con la relación existente, no crear otra de la misma pareja. Esa cardinalidad respalda el Optional del método findByTutorProfileIdAndStudentId.

Las consultas por tutor y por estudiante incluyen vínculos autorizados y revocados, sin una lectura global del puerto de Domain. Leer una fila no concede acceso: Application deberá comprobar isAuthorized() y la identidad autenticada antes de mostrar información de estudiantes. El assembler no llama a authorize(), no reemplaza fechas por Instant.now(), no inventa un autorizador y no crea eventos.

save reconstruye a partir de la entidad devuelta por JPA. Un alta mantiene ID nulo hasta recibir el persistido; al guardar revocación o reautorización se envía el mismo ID existente. La reconstitución rechaza estados y fechas incompatibles con las invariantes actuales. Las excepciones de integridad se propagan, incluida una futura colisión de la pareja única; no se traducen en éxito.

### Integridad pendiente y alcance

No se modificaron aggregates, puertos, Commands, properties, POM ni otros bounded contexts. Los IDs se mantuvieron como columnas escalares según el modelo ya usado por esta implementación: no se declararon todavía las FKs hacia profiles/students. Esas restricciones deberán revisarse e incorporarse al esquema explícitamente antes de afirmar que hay integridad referencial real. La existencia de los perfiles y del estudiante, el tipo TUTOR y la autorización vigente de quien autoriza o revoca corresponden a Application.

Antes de aplicar la unicidad habrá que revisar pares duplicados existentes, incluso los revocados. La ampliación a 14 debe revisarse frente a la columna actual si ya existe. No se eliminaron filas históricas ni se fusionaron registros. El código conserva la política confirmada sin ejecutar una migración sobre datos del usuario.

### Pruebas ejecutadas

Se reutilizó OfflineProfilesMappingTestSupport para registrar las cuatro persistence entities existentes. El proveedor JDBC sigue bloqueado y la generación de esquema deshabilitada.

| Clase nueva | Casos | Cobertura |
|---|---|---|
| TutorStudentRelationshipPersistenceAssemblerTests | 11 | Creación, cuatro tipos de relación y ambos estados, autorizador opcional, cronología inválida, reautorización con la misma identidad e IDs persistidos inválidos. |
| TutorStudentRelationshipRepositoryImplTests | 10 | Lecturas presentes/ausentes, filtros por tutor/estudiante, conservación de revocación, listas vacías sin consulta global, alta, guardado con identidad existente y propagación de error de unicidad simulado. |
| TutorStudentRelationshipPersistenceMappingTests | 4 | Columnas, referencias numéricas, nulabilidad, fechas con zona horaria, longitudes de enums, unicidad compuesta y construcción real del repositorio Spring Data con sus consultas derivadas sin conexión. |

Maven compiló **76 fuentes principales y 19 fuentes de prueba con JDK 27**. Resultado: **144 pruebas, 0 fallos, 0 errores, BUILD SUCCESS**: las 119 anteriores y 25 nuevas. Selector utilizado:

```text
-Dtest=ServiceEnrollmentTests,SchoolTests,ProfilesDomainConsistencyTests,DataDeletionRequestTests,AccountIdPersistenceConverterTests,ProfilePersistenceAssemblerTests,ProfileRepositoryImplTests,ProfilePersistenceMappingTests,StudentPersistenceAssemblerTests,StudentRepositoryImplTests,StudentPersistenceMappingTests,SchoolPersistenceAssemblerTests,SchoolRepositoryImplTests,SchoolPersistenceMappingTests,TutorStudentRelationshipPersistenceAssemblerTests,TutorStudentRelationshipRepositoryImplTests,TutorStudentRelationshipPersistenceMappingTests
```

`git diff --check` pasó. La construcción del repositorio mediante JpaRepositoryFactory validó las consultas derivadas, sin invocarlas para ejecutar SQL. La restricción única se verificó en metadatos; el error de integridad del adaptador se simuló. No se comprobaron inserciones duplicadas reales, generación de IDs/auditoría ni restricciones contra PostgreSQL. No se arrancó Spring Boot ni se repitió contextLoads. No hubo conexiones JDBC ni cambios en la base de datos.

## Continuación: persistencia de ServiceEnrollment, I1–I6, sin modificar la base

Se continuó desde la versión del ZIP recibida, respetando Domain, el puerto `ServiceEnrollmentRepository`, los comandos y la política de afiliación confirmada al inicio de este documento. No se modificaron `pom.xml`, properties, aggregates, otros bounded contexts, Application, REST ni el esquema de PostgreSQL. La implementación sigue la separación entre Domain, Persistence Entity, Assembler, repositorio Spring Data y adapter de Learning Center, al igual que los cuatro recorridos de persistencia anteriores de Rumbo.

### Componentes añadidos

| Paso | Componente | Decisión |
|---|---|---|
| I1 | Tipos nativos Hibernate/JPA | `Long`, `String` e `Instant` no requieren converters nuevos. `status` permanece como texto, con los valores de Domain. |
| I2 | Campos escalares | No se añaden embeddables: este aggregate no contiene Value Objects compuestos. |
| I3 | `entities/ServiceEnrollmentPersistenceEntity.java` | Tabla `service_enrollments`, herencia auditable de shared, referencias numéricas, estado como VARCHAR(10) y cronología `Instant`. |
| I4 | `assemblers/ServiceEnrollmentPersistenceAssembler.java` | Copia todos los campos sin modificar fechas ni transiciones; al leer, invoca `ServiceEnrollment.reconstitute` para aplicar invariantes del dominio. |
| I5 | `repositories/ServiceEnrollmentPersistenceRepository.java` | `JpaRepository`, búsquedas por conductor y estudiante y comprobación derivada `existsByStudentIdAndDriverProfileIdAndStatusIn`. |
| I6 | `adapters/ServiceEnrollmentRepositoryImpl.java` | Implementa las cinco operaciones del puerto; la búsqueda de conflictos pasa exactamente `PENDING` y `ACCEPTED` al repositorio, sin `ENDED`. |

La entidad declara **diez columnas**: `id`, `student_id`, `driver_profile_id`, `requested_by_profile_id`, `status`, `requested_at`, `accepted_at`, `ended_at`, `created_at` y `updated_at`. Las tres referencias y el ID usan `BIGINT`; `status` usa `VARCHAR(10)` (`PENDING`, `ACCEPTED`, `ENDED`). `requested_at` es obligatorio; `accepted_at` y `ended_at` son opcionales. Los tres `Instant` se mapean a timestamps con zona horaria bajo el dialecto PostgreSQL, a comprobar con pruebas de metadatos Hibernate. La base de identidad/auditoría es la misma que en los otros aggregates.

Un `save` conserva `id` nulo en el alta y reconstruye a partir de la entidad **devuelta** por JPA, que posee el ID asignado; las actualizaciones reutilizan la identidad persistida. Las búsquedas retornan `Optional` o `List`, incluidas listas vacías, sin consultas globales. Se conserva el historial de afiliaciones finalizadas. Las excepciones de integridad se propagan; no se convierten en éxito. Ninguna consulta de Infrastructure concede autorización por sí misma.

### Duplicidad, historial y concurrencia

La regla confirmada es **a lo sumo una afiliación `PENDING` o `ACCEPTED` por pareja `(student_id, driver_profile_id)`**. `ENDED` debe permitir altas posteriores de la **misma pareja** y no existe exclusividad entre conductores distintos. El método de Domain `existsPendingOrAcceptedByStudentIdAndDriverProfileId` se traduce a una consulta Spring Data que filtra expresamente por esos dos estados y ambos identificadores.

**No se añadió `@UniqueConstraint(student_id, driver_profile_id)`**: sería incorrecto porque impediría un segundo registro de la misma pareja tras un `ENDED`. La comprobación `exists...` es solo un prerrequisito lógico para el futuro Command Service; **no protege contra dos peticiones concurrentes**. Antes de activar la funcionalidad en PostgreSQL se requiere revisar el esquema y los datos y, si son compatibles, crear mediante una migración revisada una restricción única parcial, conceptualmente:

```sql
-- PROPUESTA PARA REVISIÓN FUTURA. NO EJECUTADA.
CREATE UNIQUE INDEX uk_service_enrollments_active_pair
ON service_enrollments (student_id, driver_profile_id)
WHERE status IN ('PENDING', 'ACCEPTED');
```

En producción, esa restricción y la traducción apropiada de `DataIntegrityViolationException` son necesarias para hacer cumplir la regla incluso bajo concurrencia. Los permisos para aceptar o finalizar afiliaciones pertenecen a Application; aún faltan integración IAM y verificación de autorización vigente del tutor o identidad del conductor. La transición `PENDING -> ACCEPTED -> ENDED` ya es protegida por Domain.

### Pruebas incorporadas

Se añadieron las siguientes clases:

- `ServiceEnrollmentPersistenceAssemblerTests`: altas con ID nulo, round-trip de tres estados y fechas, identidad histórica, entrada nula y rechazo de registros persistidos inválidos.
- `ServiceEnrollmentRepositoryImplTests`: lectura por ID, listados por conductor/estudiante, historial `ENDED`, consultas vacías, predicado de conflicto por pareja y estados, alta con ID devuelto, actualización de estados e integridad propagada.
- `ServiceEnrollmentPersistenceMappingTests`: inspección de columnas, nulabilidad, tipos numéricos, timestamps con zona, longitud del estado, ausencia intencional de unicidad indiscriminada y bootstrap del repositorio derivado sin JDBC.

Se añadió `ServiceEnrollmentPersistenceEntity` al registro de `OfflineProfilesMappingTestSupport` existente. Sus metadatos se preparan con un `ConnectionProvider` que prohíbe conexiones y con generación de esquema deshabilitada, como en las pruebas anteriores.

**Estado de la verificación en este entorno:** las 144 pruebas de la etapa anterior están documentadas en este archivo como el resultado de la ejecución original en Windows con JDK 27. En este entorno aislado solo está disponible JDK 21, no existe Maven instalado, y `bash mvnw -o ...` falla al descargar la distribución Maven 3.10.0 por falta de DNS. Por tanto, **no se afirma haber ejecutado la suite Maven/JUnit de esta etapa ni validado Hibernate real aquí**. La comprobación de Java aislada, si se realiza, se registra por separado y no sustituye a la compilación del proyecto con sus dependencias. Tampoco se arrancó Spring Boot, ejecutó SQL ni accedió a PostgreSQL.

Para continuar en IntelliJ con JDK 27, ejecutar los tests seleccionados sin iniciar la aplicación ni aplicar DDL:

```powershell
.\mvnw.cmd --batch-mode --no-transfer-progress '-Dtest=ServiceEnrollmentTests,ServiceEnrollmentPersistenceAssemblerTests,ServiceEnrollmentRepositoryImplTests,ServiceEnrollmentPersistenceMappingTests' test
```

Después, repetir la regresión de Profiles usando el selector de la etapa anterior más las tres pruebas nuevas. La comprobación real de la unicidad y de escrituras concurrentes requerirá más adelante un entorno de base de datos de pruebas y una migración revisada; no confundir estas pruebas sin JDBC con esa verificación.

### Verificación suplementaria disponible en este entorno

Como alternativa parcial a Maven, se compilaron con `javac --release 21` **copias temporales** de los siete componentes del recorrido (aggregate, command, puerto, entidad JPA, assembler, repositorio y adapter) junto con dobles mínimos para las clases de framework. En las copias del aggregate y la entidad se añadieron únicamente los getters/setters que Lombok generaría durante la compilación normal; **los archivos originales del proyecto no se alteraron para esta comprobación**. Un harness Java sin JDBC ejecutó **21 aserciones correctas**, cubriendo alta, ID devuelto, `PENDING`/`ACCEPTED`/`ENDED`, historial, re-afiliación, selección de pareja, exclusión de `ENDED` del predicado de conflicto, búsquedas por conductor/estudiante y rechazo de estado almacenado incoherente. La ejecución informó `OFFLINE SMOKE PASSED: 21 assertions`.

**Límite de esta comprobación:** los dobles no implementan realmente Spring Data JPA, Hibernate, Lombok ni JUnit, y Java 21 no es la versión declarada del proyecto. Se necesita ejecutar las pruebas JUnit añadidas con JDK 27 y Maven en IntelliJ para dar por comprobada la compilación completa, la carga de metadatos Hibernate y el bootstrap del repositorio derivado. La prueba aislada no conectó ni modificó ninguna base de datos.

## Continuación: alineación con el diagnóstico DDD de Claude y cierre de Infrastructure (noviembre de 2026)

**Fuente de las decisiones:** documento «Rumbo · Profiles & Relationship Management — Diagnóstico DDD del diagrama de clases y modelo de dominio corregido», aportado por el usuario. Se preservaron los acuerdos D1–D14; las recomendaciones opcionales de la sección 6 no se aplicaron indiscriminadamente. Esta sección **actualiza** los apartados anteriores de la auditoría y no pretende borrar el historial previo.

### Cambios de límite de aggregate

- `Student` pasa a controlar el conjunto de autorizaciones de tutores. Se conserva `Student` como raíz y `TutorStudentRelationship` como entidad interna con identidad `Long`; el archivo de esa entidad está en el mismo paquete Java que `Student` para permitir métodos de mutación de visibilidad de paquete. Ya no hereda `AbstractDomainAggregateRoot`.
- El registro `Student(RegisterStudentCommand)` incorpora una primera relación `AUTHORIZED` por el tutor que registra al menor, con `authorizedByProfileId` igual a su propio ID. `Student.authorizeTutor` valida que quien autoriza tenga autorización vigente, impide duplicados activos y crea una nueva entidad en vez de revivir una revocada (D9–D10).
- `Student.revokeTutor` comprueba que el actor esté autorizado, que exista la relación activa y que quede al menos un tutor autorizado (D11). Se conserva el historial, incluidas filas `REVOKED`.
- `Student.anonymize` revoca todas las autorizaciones, cambia a `INACTIVE` y registra `anonymizedAt`; rechaza una posterior activación. **Esto no borra de verdad nombre, fecha de nacimiento, ni datos personales en otros contextos**. La política de supresión efectiva sigue sin definirse y debe resolverse antes de exponer el flujo de US08 como terminado.
- Desaparecen el contrato `TutorStudentRelationshipRepository` de Domain, su repositorio JPA independiente y su adaptador. Las consultas de relaciones pasan por `StudentRepository` y su agregado, con métodos de búsqueda por tutor. La búsqueda por pareja se expresa como `GetActiveTutorStudentRelationshipByTutorIdAndStudentIdQuery` porque puede existir historial revocado múltiple para una misma pareja. La consulta global por ID de relación, propia del aggregate anterior, ya no se conserva.

### Persistencia de Student y concurrencia

- `StudentPersistenceEntity` mapea una colección hija de `TutorStudentRelationshipPersistenceEntity` mediante `@OneToMany(mappedBy="student", cascade=ALL, orphanRemoval=false)`; la entidad hija apunta a Student mediante `@ManyToOne`. Ningún adapter manipula relaciones de forma independiente ni se creó `UNIQUE(student_id,tutor_profile_id)` que descartaría historial legítimo.
- `StudentPersistenceAssembler` reconstruye la raíz con todas sus relaciones y su revisión. Los repositorios de lectura utilizan `@EntityGraph` para cargar el aggregate completo y evitar acceder a una colección lazy ya desvinculada.
- Se añadió `@Version` a la entidad JPA y un **`authorization_revision` persistido**. Domain incrementa `authorizationRevision` después de autorizar o revocar correctamente, y al anonimizar, para garantizar que cambios de estado de las entidades hijas también modifiquen la fila raíz. De esta forma la versión optimista puede detectar actualizaciones simultáneas con la misma versión inicial en un entorno JPA real. **La efectividad bajo concurrencia sigue pendiente de una prueba integrada con PostgreSQL**; no basta con los tests con mocks.
- La refactorización añade columnas/relaciones nuevas y elimina restricciones conceptuales anteriores; los cambios de anotaciones **no equivalen a una migración aplicada**. Debe revisarse el esquema real antes del primer arranque con `ddl-auto=update`.

### ServiceEnrollment: decisión D12

- Se mantiene `ServiceEnrollment` como Aggregate Root independiente; el flujo admite `PENDING -> ACCEPTED -> ENDED` y ahora `PENDING -> CANCELLED`. `CANCELLED` y `ENDED` son estados terminales. `cancel(by,at)` comprueba que `by == requestedByProfileId`, que la afiliación esté PENDING y que la fecha no preceda la solicitud.
- Se añadió `CancelServiceEnrollmentCommand` y la columna persistente `cancelled_at`. `ServiceEnrollmentPersistenceAssembler` copia la fecha en ambos sentidos. Se conserva el `String` para `status`; cambiarlo a enum es una **recomendación opcional**, no un cambio obligatorio de las decisiones D1–D14.
- El chequeo de duplicidad sigue siendo exclusivamente por `(student_id, driver_profile_id)` y estados `PENDING`/`ACCEPTED`; una afiliación `CANCELLED` o `ENDED` no impide una nueva. **Aún no se ha aplicado el índice único parcial de PostgreSQL** necesario para exclusión bajo carreras; requiere inspección y migración aprobada.

### DataDeletionRequest: persistencia completada

- Se implementaron `DataDeletionRequestPersistenceEntity`, `DataDeletionRequestPersistenceAssembler`, `DataDeletionRequestPersistenceRepository` y `DataDeletionRequestRepositoryImpl`, siguiendo I1–I6 de Learning Center. No requiere converters ni embeddables nuevos: `Instant` y `Long` son nativos, `DeletionRequestStatus` se almacena por `EnumType.STRING`.
- El mapeo conserva `student_id`, `requested_by_profile_id`, `status`, `requested_at`, `resolved_at`, `resolution_note`, identidad y auditoría. La nota tiene máximo 500 caracteres. Las fechas de negocio son `Instant` y la compatibilidad con `TIMESTAMPTZ` se comprueba a nivel de metadatos, sin abrir conexión JDBC.
- Se agregaron los métodos explícitos `approve(note, at)` y `reject(note, at)` que reutilizan las validaciones existentes de `resolve`. La autorización del administrador, las comprobaciones de viajes activos y la **eliminación efectiva de PII** siguen pendientes de Application y de los contratos entre bounded contexts.
- Se añadieron pruebas unitarias/Mockito/metadatos para Domain, los assemblers, adapters y mapeos. Se actualizaron las pruebas antiguas del modelo de relaciones para verificar historia, alta, revocación, última autorización, cancelación y versionado.

### Límites de la validación de esta continuación

- **Java del proyecto: 27**. El `pom.xml` se ha mantenido con `<java.version>27</java.version>`. No se debe bajar a Java 21.
- La máquina de edición aislada de esta continuación expone **JDK 21**, no JDK 27 ni una distribución Maven 3.10 instalada; por ello **no se afirma haber compilado el proyecto completo ni ejecutado su suite JUnit con JDK 27**. En su lugar se compiló un subconjunto de Domain con copias de getters generados por Lombok y se ejecutaron **21 verificaciones Java aisladas** satisfactorias; no sustituyen la comprobación de Spring Boot, Hibernate ni PostgreSQL.
- Se entregan los archivos fuente editados y las pruebas para ejecutar en IntelliJ con JDK 27. Recomendación: ejecutar la regresión seleccionando tests de `profiles`, sin incluir todavía `RumboWebServicesApplicationTests` (el error histórico de `@SpringBootConfiguration` sigue sin arreglar), y no arrancar contra una base de datos real hasta revisar/migrar su esquema. Un ejemplo de selector es:

```powershell
.\mvnw.cmd --batch-mode --no-transfer-progress '-Dtest=ProfilesDomainConsistencyTests,ServiceEnrollmentTests,DataDeletionRequestTests,SchoolTests,*PersistenceAssemblerTests,*RepositoryImplTests,*PersistenceMappingTests,AccountIdPersistenceConverterTests' test
```

- **Aún no implementado:** eventos de dominio y sus consumidores, integración IAM, aplicación de autorizaciones reales a REST, coordinación con Trip Execution para bloqueo de supresión, política de borrado real, índice parcial de afiliaciones y tests transaccionales sobre PostgreSQL. No deben presentarse como funciones terminadas.

**Estado actual:** cinco Aggregate Roots (Profile, Student, School, ServiceEnrollment, DataDeletionRequest), `TutorStudentRelationship` como entidad interna de Student y persistencia I1–I6 de las cinco raíces a nivel de código fuente. Se ha completado el punto de Infrastructure que estaba pendiente antes del rediseño; **el siguiente paso es Application, previa ejecución/depuración de pruebas JDK 27 y revisión no destructiva de la base de datos**.


## Continuación: Application y REST de Profiles — etapa de cierre

Esta sección actualiza la nota histórica «Aún no implementado» anterior: se implementaron los cinco contratos e implementaciones de Query Services, cuatro Command Services (School permanece de lectura), cinco controladores, resources/transform, comprobaciones de autorización internas y eventos post-commit para altas y cambios de autorización. La seguridad HTTP se limita a rutas del bounded context Profiles.

Para la matriz exacta de rutas, restricciones de seguridad, diferencias aún pendientes con IAM, Trip Execution, política de supresión y verificación ejecutada, consultar `docs/profiles-completion.md`. No se modificó ningún otro bounded context ni se utilizó PostgreSQL.
