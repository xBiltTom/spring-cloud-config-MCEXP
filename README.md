# Demo: Spring Cloud Config

Cuatro aplicaciones Spring Boot: un servidor de configuración y tres clientes, sin base de datos. En esta primera etapa, el servidor lee archivos locales; luego se conectará a GitHub y se desplegará en Render.

## Requisitos

- JDK 17 o superior (comprueba con `java -version`).
- Internet para descargar dependencias la primera vez.
- Puertos 8888, 8081, 8082 y 8083 libres.

Cada proyecto incluye Maven Wrapper: no hace falta instalar Maven. Abre una terminal por aplicación **desde la carpeta raíz de este proyecto**. Arranca primero el servidor y espera a que indique que inició.

| Aplicación | Linux (bash) | Windows (PowerShell) |
| --- | --- | --- |
| Servidor | `cd config-server && ./mvnw spring-boot:run` | `cd config-server; .\mvnw.cmd spring-boot:run` |
| Productos | `cd productos-service && ./mvnw spring-boot:run` | `cd productos-service; .\mvnw.cmd spring-boot:run` |
| Clientes | `cd clientes-service && ./mvnw spring-boot:run` | `cd clientes-service; .\mvnw.cmd spring-boot:run` |
| Pedidos | `cd pedidos-service && ./mvnw spring-boot:run` | `cd pedidos-service; .\mvnw.cmd spring-boot:run` |

Visita `http://localhost:8888/productos-service/default` para consultar la configuración y luego `http://localhost:8081/mensaje`, `http://localhost:8082/mensaje` y `http://localhost:8083/mensaje` para ver cada respuesta.

Los mensajes están en `config-repo/`. Edita uno, reinicia el cliente correspondiente y vuelve a consultar `/mensaje`. Para detener cada aplicación usa `Ctrl+C` en su terminal.

## Siguiente etapa

El servidor soporta el perfil `git`, configurable con las variables `CONFIG_BACKEND=git`, `CONFIG_GIT_URI` y opcionalmente `CONFIG_GIT_BRANCH` (por defecto `main`). Por defecto, busca los archivos dentro de la carpeta `config-repo` del repositorio Git; se puede cambiar con `CONFIG_GIT_SEARCH_PATHS`. Los clientes aceptan `CONFIG_SERVER_URL`. Falta configurar un repositorio remoto y verificar el despliegue.

## Config Server en Render

Crea un **Web Service** desde este repositorio y selecciona la rama `main`:

- **Language:** Docker.
- **Root Directory:** `config-server`.
- **Dockerfile Path:** `./Dockerfile` (relativo a `config-server`).
- **Docker Build Context Directory:** `.` (relativo a `config-server`, si se solicita).
- **Instance Type:** Free, para la demostración.
- **Environment Variables:**
  - `CONFIG_BACKEND` = `git`
  - `CONFIG_GIT_URI` = `https://github.com/xBiltTom/spring-cloud-config-MCEXP.git`

El Dockerfile incluye el comando de inicio, por lo que no es necesario proporcionar otro. Render establece `PORT`; Spring Boot lo usa automáticamente. Las opciones `CONFIG_GIT_BRANCH=main` y `CONFIG_GIT_SEARCH_PATHS=config-repo` ya son los valores predeterminados.

Una vez desplegado, abre `https://TU-SERVICIO.onrender.com/productos-service/default` y comprueba que `propertySources` contiene el mensaje de productos. Repite con `clientes-service` y `pedidos-service`. El plan gratuito puede tardar en responder si el servicio se ha detenido por inactividad. No coloques credenciales ni información privada en este repositorio público.

Para conectar los microservicios a Render, configura `CONFIG_SERVER_URL` en cada terminal antes de iniciar el cliente. En Linux: `export CONFIG_SERVER_URL=https://TU-SERVICIO.onrender.com`; en PowerShell: `$env:CONFIG_SERVER_URL = "https://TU-SERVICIO.onrender.com"`. Después sigue los comandos de inicio de la tabla anterior. Comprueba las tres rutas `/mensaje` en localhost.
