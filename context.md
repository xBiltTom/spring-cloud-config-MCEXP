# Contexto para afinar la presentación de Spring Cloud Config

## Encargo

Afina la presentación existente para una exposición universitaria de 5 a 7 minutos. Conserva sus siete diapositivas en formato 16:9. Mejora la jerarquía visual, la legibilidad al proyectar y la conexión entre arquitectura y demostración. Trabaja sobre el diseño existente y conserva las capturas verificadas, los enlaces y las notas del presentador.

- [Canva editable](https://www.canva.com/d/ddskfgOJGTlUHva)
- [Vista de la presentación](https://www.canva.com/d/P6hHrI2lOo8dJfj)
- [Repositorio del proyecto](https://github.com/xBiltTom/spring-cloud-config-MCEXP)
- [Respuesta real de configuración de productos en Render](https://config-server-sem12.onrender.com/productos-service/default)

La audiencia conoce los conceptos básicos de microservicios. Usa `productos-service` como ejemplo conductor para que pueda seguir el mismo valor desde GitHub hasta la respuesta que recibe el usuario.

## Datos de portada

- **Título** · Configuración centralizada con Spring Cloud Config
- **Universidad** · Universidad Nacional de Trujillo
- **Curso** · Arquitectura de Microservicios
- **Ciclo** · 2026-II
- **Integrantes, en orden**
  1. Gomez Rodríguez, Adrian Fernando
  2. Lucano Nieves, David Adrian
  3. Novoa Arce, Bilton Junior
  4. Quezada Rodríguez, Roberto Enrique

## Funcionamiento verificado

1. GitHub guarda un archivo `.properties` por servicio en `config-repo/`. Para productos, el archivo contiene `mensaje=Bienvenido al servicio de productos`.
2. El Config Server está desplegado en Render con Docker. Lee el repositorio Git y entrega las propiedades de cada aplicación por HTTPS.
3. Al arrancar, `productos-service` importa su configuración desde Render. Para el perfil `default` solicita `GET /productos-service/default`. El JSON contiene el valor de `mensaje` dentro de `propertySources`. El campo `version` indica el commit de Git del que provienen los valores.
4. Spring inyecta el valor en el controlador. Cuando se consulta `GET http://localhost:8081/mensaje`, el servicio local devuelve `200 OK` y el texto `Bienvenido al servicio de productos`.
5. Para aplicar un cambio en esta demo, se publica el archivo actualizado en Git y se reinicia el cliente. Al arrancar, el cliente importa el nuevo valor sin recompilar su código.

| Componente | Dónde corre en la demo | Identificador |
| --- | --- | --- |
| Config Server | Render | `https://config-server-sem12.onrender.com` |
| Productos | Local | `productos-service` · `8081` |
| Clientes | Local | `clientes-service` · `8082` |
| Pedidos | Local | `pedidos-service` · `8083` |

Mantén clara la diferencia entre la ruta del Config Server, que entrega JSON, y `/mensaje`, que pertenece al cliente y devuelve texto. Los clientes consultan al servidor de configuración durante el arranque. Una petición posterior a `/mensaje` se atiende con el valor ya cargado.

## Ajustes por diapositiva

| N.º | Diapositiva | Qué afinar |
| --- | --- | --- |
| 1 | Configuración centralizada con Spring Cloud Config | Da prioridad al título y mantén los cuatro nombres con el mismo peso y alineación. Conserva la fotografía como elemento de apoyo y deja espacio entre título, curso e integrantes. |
| 2 | Un repositorio para tres servicios | Haz visible la diferencia con dos columnas. A la izquierda, cada servicio gestiona su archivo. A la derecha, los tres archivos se reúnen en `config-repo/`. La idea clave es que se centraliza la gestión y cada servicio conserva sus valores. |
| 3 | Arquitectura del proyecto | Conserva el flujo visual GitHub → Config Server en Render → tres clientes locales. Añade «Clientes locales» como rótulo común y «Al iniciar» sobre la conexión. Mantén los puertos junto a los nombres. Las flechas representan el flujo de configuración. |
| 4 | El recorrido de la propiedad mensaje | Usa tres filas numeradas para mostrar el archivo, la consulta y el resultado. Resalta la clave `mensaje` y su valor con el mismo color para que el público pueda seguirlos. Usa tipografía monoespaciada para las rutas. |
| 5 | El despliegue en Render | Amplía el extracto del JSON y destaca la aplicación, la revisión de Git y el valor entregado junto a la imagen. Rotula la imagen «Extracto de la respuesta HTTP». Conserva el enlace a la respuesta completa. |
| 6 | Demostración de extremo a extremo | Da protagonismo al `200 OK`, la URL local y el mensaje. Etiqueta los pasos como «Consultar Render» y «Consultar el cliente». Mantén visible la dirección `localhost:8081` en la captura. |
| 7 | Cómo se aplica un cambio | Muestra cómo editar y publicar el valor, reiniciar el servicio y consultar `/mensaje`. Cierra con la idea «El mensaje se actualiza sin recompilar el microservicio». |

## Dirección visual y evidencias

- Mantén el azul oscuro `#102033`, el verde `#52D6A1` como acento y los fondos claros en las diapositivas explicativas. En fondo claro, usa verde oscuro para el texto resaltado.
- Usa una familia de texto legible y reserva la monoespaciada para rutas, archivos y valores técnicos. Si un bloque resulta difícil de leer al proyectar, recorta el texto antes de reducir la fuente.
- Mantén los diagramas y los textos editables en Canva. Alinea títulos y márgenes. Evita adornos que compitan con el diagrama o con la evidencia.
- La captura de Render presenta un extracto de la respuesta real. La versión registrada en esa captura es `6241631b39b2f758e3be1666b20c839efbe01356`. Conserva los valores de la captura. Si obtienes una respuesta nueva, actualiza la captura y su explicación juntas.
- La captura local se obtuvo con el cliente de productos en ejecución, conectado a Render. Si no puedes volver a ejecutar el cliente, conserva la evidencia original.
- La imagen de portada es ilustrativa. Mantén el crédito en las notas. [Panumas Nikhomkhai, Pexels](https://www.pexels.com/photo/computer-server-in-data-center-room-17489156/)

## Revisión antes de entregar

- Confirma que las siete diapositivas conservan el orden y que la portada reproduce los datos del equipo.
- Revisa el diagrama, el JSON y la captura local a tamaño de proyección. Comprueba que se leen las rutas, los puertos y el mensaje.
- Abre el enlace de Render desde la presentación. Para la demostración en vivo, deja iniciado el cliente local y usa la captura de la diapositiva 6 como respaldo.
- Entrega el enlace editable del Canva afinado y un resumen breve de los cambios. Si solo puedes revisar el diseño, entrega ajustes concretos por diapositiva e indica cuáles requieren edición manual.
