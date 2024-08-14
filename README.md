# Microservicio Usuarios y Seguridad (msvc-Authentication)

`msvc-authentication` es un microservicio diseñado para gestionar la autenticación de usuarios en múltiples microservicios a través de la emisión de tokens JWT. Este servicio facilita un control centralizado del acceso y asegura que solo los usuarios autorizados puedan acceder a los recursos y operaciones permitidas.

## Funcionalidades Principales

- **Autenticación y Emisión de Tokens:** Implementación de JWT para manejar la autenticación y la emisión de tokens seguros.
- **Interacción con `msvc-authorization`:** Verificación de credenciales y obtención de detalles del usuario y roles.
- **Interacción con `msvc-configuration`:** Obtención de parámetros de configuración, como claves de aplicación y estados de las entidades.

## Tecnologías Utilizadas

- **Java** - Spring Boot
- **Spring WebFlux**
- **Spring Security**
- **JWT (JSON Web Tokens)**
- **MongoDB**
- **Lombok**
- **SpringDoc** - Para la documentación de la API

## Estructura del Proyecto

- **`src/main/java/com/diceprojects/msvcauthentication`**: Código fuente del microservicio.
  - **`clients`**: Clases que se comunican con otros microservicios (`msvc-authorization`, `msvc-configuration`).
  - **`exceptions`**: Clases relacionadas con el manejo de excepciones.
  - **`persistences`**: DTOs y otras clases relacionadas con la persistencia de datos.
  - **`services`**: Lógica de negocio y servicios relacionados con la autenticación.
  - **`security`**: Configuración de seguridad y utilidades relacionadas con JWT.
  - **`controllers`**: EndPoints de la API.
  - **`utils`**: Utilidades adicionales, como la obtención de estados de entidades.

## Configuración

Asegúrate de configurar adecuadamente el archivo `application-dev.properties` o `application-prod.properties` para la conexión a la base de datos y otros ajustes necesarios, como las URLs de los microservicios `msvc-authorization` y `msvc-configuration`.

### Ejemplo de Configuración (`application.properties`):

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/msvc-authentication
msvc.authorization.url=http://localhost:8003/api/
msvc.configuration.url=http://localhost:8005/api/
```

## Uso

1. **Compilación y Ejecución:** Utiliza Maven o tu herramienta de construcción preferida para compilar y ejecutar el proyecto.
   ```bash
   mvn clean install
   java -jar target/msvc-authentication.jar
   ```

## Endpoints

- **Autenticación:**
  - `POST /api/auth/login`: Iniciar sesión y obtener un token JWT.
  - `GET /api/auth/validate`: Validar un token JWT y devolver los detalles del usuario. (Requiere FIX)

- **Usuarios (a través de `msvc-authorization`):**
  - `GET /users/{id}`: Obtener los detalles de un usuario.

- **Roles (a través de `msvc-authorization`):**
  - `GET /roles`: Obtener todos los roles.

Documentación detallada de la API disponible en: `[HOST]:[PORT]/apidoc/webjars/swagger-ui/index.html`

## Contribuir

¡Contribuciones son bienvenidas! Si encuentras errores o mejoras, abre un problema o envía una solicitud de extracción.

## Licencia

Este proyecto está bajo la Licencia MIT. Consulta el archivo `LICENSE.md` para más detalles.

## Dependencias

Este proyecto utiliza las siguientes dependencias principales:

- **Spring Boot Starter Data MongoDB Reactive**
- **Spring Boot Starter Security**
- **Spring Boot Starter WebFlux**
- **JWT (JSON Web Tokens)**
- **Lombok**
- **SpringDoc OpenAPI WebFlux UI**

Las dependencias están definidas en el archivo `pom.xml` del proyecto.
