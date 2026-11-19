# SICA - Hoja de bolsillo para la sustentación

> Una página para tener al lado. Comandos, credenciales y respuestas cortas.

---

## 1. Encender el proyecto (3 pasos)

```bash
# 1. Abrir Docker Desktop y esperar a que arranque

# 2. En la carpeta del proyecto
docker compose up -d
docker compose ps          # debe decir: Up (healthy)

# 3. Compilar y abrir
mvn clean package
mvn exec:java
```

## 2. Apagar y reiniciar

```bash
docker compose down        # apaga, conserva los datos
docker compose down -v     # apaga y BORRA los datos
docker compose up -d       # vuelve a cargar schema.sql y data.sql
```

## 3. Credenciales (contraseña: Acme2024!)

| Rol | Correo |
|---|---|
| Superusuario | superusuario@zonaacme.com |
| Supervisor | supervisor@zonaacme.com |
| Guarda | guarda@zonaacme.com |
| Funcionario | funcionario@globex.com |

## 4. Ver la base de datos

- phpMyAdmin: http://localhost:8081
- Usuario: `root` | Contraseña: `root1234`
- MySQL: `localhost:3307` | `sica_user` / `sica_pass`

## 5. Si algo falla

| Síntoma | Solución |
|---|---|
| "cannot connect to the docker daemon" | Abrir Docker Desktop y esperar. |
| "ports are not available" | El puerto está ocupado. Revisar `docker compose ps`. |
| "mvn no se reconoce" | Cerrar y abrir la terminal, o usar `C:\Users\Miguel\tools\apache-maven-3.9.9\bin\mvn.cmd`. |
| La app dice "MySQL no disponible" | `docker compose up -d` y revisar que use el puerto 3307. |
| "Acceso denegado" en login | Escribir exactamente `Acme2024!`. |

---

## 6. Pitch de 30 segundos

> "SICA reemplaza el libro de papel de Zona Acme. Centraliza personas, empresas y visitas en MySQL; valida permisos por rol; registra todo en una bitácora inmutable y permite que el guarda y el funcionario trabajen en ventanas distintas con aprobaciones en tiempo real."

---

## 7. Preguntas y respuestas de una línea

| Pregunta | Respuesta corta |
|---|---|
| ¿Dónde se conecta a la BD? | En `DatabaseConfig.java`, línea 49, con `DriverManager.getConnection(...)`. Los datos están en `application.properties`. |
| ¿Cómo valida permisos? | Dos veces: la UI oculta módulos y el servicio vuelve a validar con `sesion.requerir("permiso")`. |
| ¿Cómo actualiza dos ventanas a la vez? | Patrón Observer: `VisitaEventBus` compartido por `ServiceLocator`; los avisos llegan al EDT de Swing. |
| ¿Dónde queda la auditoría? | En `bitacora_auditoria`, solo INSERT. Nadie la edita ni la borra. |
| ¿Qué es un DAO? | La clase que va a MySQL con `PreparedStatement` y devuelve objetos. La vista nunca escribe SQL. |
| ¿Qué hace la Factory? | `VisitaFactory` crea la visita con el estado inicial correcto según el flujo. |
| ¿Qué hace Strategy? | Elige la regla de ingreso en orden: bloqueado, salida olvidada, pre-registrado, no anunciado, carnet olvidado. |
| ¿Qué pasa en la salida olvidada? | Una transacción cierra la visita vieja, crea la nueva en `Dentro` y escribe 3 filas de auditoría. |
| ¿Cómo guarda contraseñas? | Hash BCrypt con salt (`jbcrypt`). Nunca en texto plano. |
| ¿Cuántas tablas y permisos? | 11 tablas y 25 permisos. |
| ¿Por qué el puerto 3307? | Para no chocar con un MySQL local que ya usa 3306. |
| ¿Cuántas pruebas hay? | 15 pruebas JUnit, todas en verde. |
| ¿Diferencia main y develop? | `develop` es el cuaderno de trabajo; `main` es el libro final. Se unen con un Pull Request. |

---

## 8. Archivos que debes saber señalar

| Archivo | Para qué |
|---|---|
| `config/DatabaseConfig.java` | Conexión a MySQL. |
| `config/ServiceLocator.java` | Une servicios, DAOs y el bus de eventos. |
| `service/impl/VisitaServiceImpl.java` | Los 4 flujos, RBAC, auditoría y transacciones. |
| `pattern/observer/VisitaEventBus.java` | Tiempo real entre ventanas. |
| `pattern/strategy/IngresoStrategyResolver.java` | Orden de las reglas de ingreso. |
| `pattern/factory/VisitaFactory.java` | Creación de visitas según el flujo. |
| `dao/BaseDao.java` | JDBC, `PreparedStatement`, cierre seguro. |
| `view/MainFrame.java` | Menú construido según permisos del rol. |
| `db/schema.sql` y `db/data.sql` | Tablas y datos de prueba. |

---

## 9. Los 4 flujos en una línea

| Flujo | Qué pasa |
|---|---|
| Pre-registrado | Nace **Aprobado**; el guarda hace check-in. |
| No anunciado | Nace **Pendiente**; el funcionario aprueba en vivo. |
| Carnet olvidado | Nace **Pendiente por Olvido**; aprobación puntual. |
| Salida olvidada | Se cierra la visita vieja y se crea una nueva **Dentro** en una transacción. |

---

## 10. Publicar el Pull Request

```bash
git push origin develop
```

En GitHub: **Compare & pull request** -> `base: main`, `compare: develop` -> **Create pull request**.
