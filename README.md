# Demo: Spring Cloud Config

La demo incluye un Config Server, tres microservicios, un registro Eureka y un API Gateway. El Config Server puede leer archivos locales o consultar GitHub desde Render.

## Requisitos

- JDK 17 o superior (comprueba con `java -version`).
- Internet para descargar dependencias la primera vez.
- Puertos 8888, 8081, 8082 y 8083 libres.

El repositorio incluye Maven Wrapper, por lo que no hace falta instalar Maven. Para el modo local, abre una terminal por aplicación **desde la carpeta raíz del proyecto**. Arranca primero el Config Server y espera a que indique que inició.

| Aplicación | Linux (bash) | Windows (PowerShell) |
| --- | --- | --- |
| Servidor | `cd config-server && ./mvnw spring-boot:run` | `cd config-server; .\mvnw.cmd spring-boot:run` |
| Productos | `cd productos-service && ./mvnw spring-boot:run` | `cd productos-service; .\mvnw.cmd spring-boot:run` |
| Clientes | `cd clientes-service && ./mvnw spring-boot:run` | `cd clientes-service; .\mvnw.cmd spring-boot:run` |
| Pedidos | `cd pedidos-service && ./mvnw spring-boot:run` | `cd pedidos-service; .\mvnw.cmd spring-boot:run` |

Visita `http://localhost:8888/productos-service/default` para consultar la configuración y luego `http://localhost:8081/mensaje`, `http://localhost:8082/mensaje` y `http://localhost:8083/mensaje` para ver cada respuesta.

Los mensajes están en `config-repo/`. Para detener cada aplicación usa `Ctrl+C` en su terminal.

## Actualizar un mensaje automáticamente

GitHub envía un webhook firmado al Config Server cuando se hace `push`. El servidor avisa por WebSocket a los clientes conectados; estos vuelven a leer su configuración y `@RefreshScope` actualiza los mensajes. No hay polling de Git ni hace falta reiniciar los servicios o ejecutar una petición manual. Un push de cualquier archivo o rama genera el aviso: cada cliente vuelve a consultar su propia configuración.

Para la demo con Render, configura una vez el webhook siguiendo la sección de abajo. Inicia los microservicios con `CONFIG_SERVER_URL` apuntando a ese servidor y espera en sus logs el mensaje `Conectado a eventos de configuración`. Cambia `mensaje` en un archivo de `config-repo/` y publica el commit en `main`. Al llegar el aviso, consulta `/mensaje` otra vez; el valor también se actualiza por el Gateway. La demora depende de GitHub, la red y Render.

Si se interrumpe el WebSocket, los clientes intentan reconectarse automáticamente y vuelven a consultar su configuración al reconectar, para recuperar cambios ocurridos durante la desconexión. También reintentan una recarga que falle. Cada conexión envía un mensaje de mantenimiento cada cuatro minutos: eso puede mantener activo el servicio gratuito de Render mientras haya clientes conectados. Detén los clientes al terminar la demo.

### Configurar el webhook de GitHub

1. Genera una contraseña aleatoria larga para el webhook. Guárdala en un lugar seguro y **no la subas al repositorio**.
2. En el servicio de Render, añade la variable `GITHUB_WEBHOOK_SECRET` con ese valor y despliega esta versión del Config Server.
3. En el repositorio de GitHub, abre **Settings → Webhooks → Add webhook**. Usa como Payload URL `https://config-server-sem12.onrender.com/github-webhook`, Content type `application/json`, y el mismo secreto en Secret. Selecciona el evento **push**, activa el webhook y guárdalo.
4. GitHub enviará un evento de prueba `ping`; la entrega debe responder HTTP `202`. Revisa **Recent Deliveries** si falla. No compartas el secreto.

El endpoint rechaza las peticiones sin firma válida o sin secreto configurado. Los avisos WebSocket no contienen mensajes ni secretos. Render debe estar activo y el webhook debe poder llegar al servidor; en el plan gratuito puede tardar en despertar. Antes de demostrar un cambio, verifica la conexión de los tres clientes. Para pruebas locales sin publicar el servidor, un webhook de GitHub no puede alcanzar `localhost` por sí solo: se requiere un túnel o enviar un webhook de prueba firmado.

## Eureka y API Gateway con Config Server en Render

Eureka registra las instancias de los microservicios en el puerto `8761`. El Gateway recibe las peticiones en el puerto `8080` y resuelve los destinos por nombre en Eureka. Los microservicios conservan sus puertos locales y cargan su configuración desde Render.

1. Desde la raíz del repositorio, inicia Eureka en una terminal.

   ```powershell
   .\config-server\mvnw.cmd -f eureka-server/pom.xml spring-boot:run
   ```

2. Abre una terminal por microservicio. Define estas variables en cada terminal y usa el comando de inicio correspondiente de la tabla anterior.

   ```powershell
   $env:CONFIG_SERVER_URL = 'https://config-server-sem12.onrender.com'
   $env:EUREKA_ENABLED = 'true'
   ```

3. Inicia el Gateway desde la raíz, en otra terminal.

   ```powershell
   .\config-server\mvnw.cmd -f api-gateway/pom.xml spring-boot:run
   ```

En Bash, usa `./config-server/mvnw` para los módulos nuevos y `export` para definir las variables.

Cuando el [panel de Eureka](http://localhost:8761/) muestre las tres instancias, prueba las rutas del Gateway. La propagación del registro puede tardar unos segundos.

| Servicio | Ruta por el Gateway |
| --- | --- |
| Productos | [http://localhost:8080/productos/mensaje](http://localhost:8080/productos/mensaje) |
| Clientes | [http://localhost:8080/clientes/mensaje](http://localhost:8080/clientes/mensaje) |
| Pedidos | [http://localhost:8080/pedidos/mensaje](http://localhost:8080/pedidos/mensaje) |

El Gateway quita el primer segmento de la ruta antes de reenviarla. Por ejemplo, `/productos/mensaje` llega como `/mensaje` a `productos-service`. Puedes cambiar la URL del registro con `EUREKA_SERVER_URL` en el Gateway y en los microservicios. En modo local con Eureka también puedes usar el Config Server del puerto `8888` dejando `CONFIG_SERVER_URL` sin definir.

## Siguiente etapa

El servidor soporta el perfil `git`, configurable con las variables `CONFIG_BACKEND=git`, `CONFIG_GIT_URI` y opcionalmente `CONFIG_GIT_BRANCH` (por defecto `main`). Por defecto, busca los archivos dentro de la carpeta `config-repo` del repositorio Git; se puede cambiar con `CONFIG_GIT_SEARCH_PATHS`. Los clientes aceptan `CONFIG_SERVER_URL`. El servidor de la demo está desplegado en Render y usa el repositorio remoto de GitHub.

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
  - `GITHUB_WEBHOOK_SECRET` = el secreto que también configures en GitHub

El Dockerfile incluye el comando de inicio, por lo que no es necesario proporcionar otro. Render establece `PORT`; Spring Boot lo usa automáticamente. Las opciones `CONFIG_GIT_BRANCH=main` y `CONFIG_GIT_SEARCH_PATHS=config-repo` ya son los valores predeterminados.

Una vez desplegado, abre `https://TU-SERVICIO.onrender.com/productos-service/default` y comprueba que `propertySources` contiene el mensaje de productos. Repite con `clientes-service` y `pedidos-service`. El plan gratuito puede tardar en responder si el servicio se ha detenido por inactividad. No coloques credenciales ni información privada en este repositorio público.

Para conectar los microservicios a Render, configura `CONFIG_SERVER_URL` en cada terminal antes de iniciar el cliente. En Linux: `export CONFIG_SERVER_URL=https://TU-SERVICIO.onrender.com`; en PowerShell: `$env:CONFIG_SERVER_URL = "https://TU-SERVICIO.onrender.com"`. Después sigue los comandos de inicio de la tabla anterior. Comprueba las tres rutas `/mensaje` en localhost.
