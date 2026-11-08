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

El servidor soporta el perfil `git`, configurable con las variables `CONFIG_BACKEND=git`, `CONFIG_GIT_URI` y opcionalmente `CONFIG_GIT_BRANCH` (por defecto `main`). Los clientes aceptan `CONFIG_SERVER_URL`. La integración Git y el despliegue se prepararán y verificarán en la siguiente etapa.
