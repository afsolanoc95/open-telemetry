# Prueba de Circuit Breaker en Camel Quarkus

## 📌 Descripción de la prueba

Esta prueba implementa el patrón **Circuit Breaker** en **Apache Camel con Quarkus**, asegurando la estabilidad del sistema ante fallos en una API externa que devuelve información de salud de un personaje. Se simulan fallos en la API para observar el comportamiento del Circuit Breaker.

## 🎯 Objetivo(s) de la prueba

- **Validar** que el Circuit Breaker interrumpe las solicitudes tras un umbral de fallos.
- **Evaluar** el tiempo de espera antes de reintentar nuevas solicitudes.
- **Comprobar** la activación del mecanismo de **fallback** cuando la API no responde correctamente.
- **Garantizar** la recuperación del servicio cuando la API vuelve a estar disponible.

## 🔄 Pasos implementados para llevar a cabo la prueba

1. Se configuró un **endpoint REST** en Quarkus que recibe solicitudes de salud de un personaje.
2. Se utilizó **Apache Camel** para enrutar las solicitudes hacia una API externa.
3. Se implementó el **Circuit Breaker** con:
    - `requestVolumeThreshold = 5` (analiza 5 intentos para abrir el circuito).
    - `failureRatio = 0.5` (si más del 50% fallan, se abre el circuito).
    - `delay = 50000` ms (espera 50 segundos antes de probar nuevamente).
    - `successThreshold = 1` (una solicitud exitosa cierra el circuito).
4. Se creó un **método fallback** que retorna una respuesta por defecto si la API no responde.
5. Se realizaron pruebas simulando fallos en la API para verificar el comportamiento del Circuit Breaker.

## 🛠️ Tecnologías usadas en la prueba

- **Lenguaje:** Java 21
- **Frameworks y Librerías:**
    - Quarkus
    - Apache Camel Quarkus
    - MicroProfile Fault Tolerance

- **Dependencias principales:**

```xml
<dependency>
    <groupId>org.apache.camel.quarkus</groupId>
    <artifactId>camel-quarkus-core</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.camel.quarkus</groupId>
    <artifactId>camel-quarkus-microprofile-fault-tolerance</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-fault-tolerance</artifactId>
</dependency>
```

## 📊 Resultados

- Cuando la API externa falla en más del 50% de los intentos, el Circuit Breaker abre el circuito y usa la respuesta fallback.
- Durante el tiempo de delay = 50000 ms, cualquier intento se bloquea automáticamente y usa el fallback.
- Después del tiempo de espera, si la primera solicitud es exitosa, el circuito se cierra y las siguientes solicitudes funcionan normalmente.


## 📌 Conclusiones

El Circuit Breaker funciona correctamente, protegiendo la aplicación de fallos en la API externa.

El fallback garantiza una respuesta al usuario, evitando interrupciones en el servicio.

El umbral de recuperación (successThreshold = 1) es adecuado si la API vuelve a estar operativa inmediatamente.

El tiempo de espera (delay) puede ajustarse según el comportamiento de la API, para equilibrar disponibilidad y rapidez en la recuperación.




se debe agregar y correr esta imagen
docker run -p 3000:3000 -p 4317:4317 -p 4318:4318 --rm -ti grafana/otel-lgtm


View Grafana
Navigate to http://127.0.0.1:3000 and log in with the default built-in user admin and password admin.

