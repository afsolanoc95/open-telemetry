package org.diplomado.routes;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.Header;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;

import java.util.LinkedHashMap;
import java.util.Map;


@ApplicationScoped
public class PersonajeRoute extends RouteBuilder {




    @Override
    public void configure() throws Exception {
       /* from("direct:obtenerSaludPersonaje")
                .log("Obteniendo datos de salud del personaje con ID: ${header.id}")
                .bean(this, "llamarApiConCircuitBreaker")
                .log("Respuesta recibida: ${body}");*/

        from("direct:infoReserva").id("infoReserva")
                .log("🟢 ${routeId} - Inicio del proceso para PNR: ${header.reserva}")
                .to("direct:consultarVuelo")
                .to("direct:consultarPasajero")
                .to("direct:combinarResultado")
                .log("✅ ${routeId} - Proceso finalizado: ${body}");

        from("direct:consultarVuelo").id("consultarVuelo")
                .log("🛫 ${routeId} - Iniciando consulta SQL para reserva: ${header.reserva}")
                .setProperty("query", simple("SELECT * FROM Reserva WHERE pnr = '${header.reserva}'"))
                .log("🛫 ${routeId} - Query construida: ${exchangeProperty.query}")
                .toD("sql:${exchangeProperty.query}?outputType=SelectOne")
                .log("🛫 ${routeId} - Resultado SQL: ${body}")
                .setProperty("consulta", body())
                .setBody(constant(null));

        from("direct:consultarPasajero").id("consultarPasajero")
                .log("🧍 ${routeId} - Consultando datos del pasajero con reserva: ${header.reserva}")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .toD("http://localhost:3001/pasajero/${header.reserva}")
                .log("🧍 ${routeId} - Respuesta recibida: ${body}")
                .unmarshal().json(JsonLibrary.Jackson)
                .setProperty("pasajero", body());

        from("direct:combinarResultado").id("combinarResultado")
                .log("🔄 ${routeId} - Iniciando combinación de datos")
                .process(exchange -> {
                    Map<String, Object> vuelo = exchange.getProperty("consulta", Map.class);
                    Map<String, Object> pasajero = exchange.getProperty("pasajero", Map.class);

                    Object hora = vuelo.get("hora");
                    if (hora instanceof java.time.LocalDateTime) {
                        vuelo.put("hora", hora.toString());
                    }

                    Map<String, Object> resultado = new LinkedHashMap<>();
                    resultado.put("vuelo", vuelo);
                    resultado.put("pasajero", pasajero);

                    exchange.getIn().setBody(resultado);
                })
                .marshal().json(JsonLibrary.Jackson)
                .setHeader("Content-Type", constant("application/json"))
                .log("📦 ${routeId} - JSON combinado: ${body}");



    }

    @CircuitBreaker(
            requestVolumeThreshold = 4,
            failureRatio = 0.5,
            delay = 30000,
            successThreshold = 1
    )
    @Timeout(2000)
    @Fallback(fallbackMethod = "fallbackPersonaje")
    public String llamarApiConCircuitBreaker(@Header("id") String id) {
        String url = "https://ed821773-6b05-4f37-b525-56973eca1b1b-00-2acsq8l8469uk.worf.replit.dev/salud/" + id;

        Log.info("url es " + url);

        return getContext()
                .createProducerTemplate()
                .requestBody(url, null, String.class);
    }

    public String fallbackPersonaje(String id) {
        return "{ \"id\": \"" + id + "\", \"salud\": \"Desconocida\", \"mensaje\": \"Datos no disponibles\" }";
    }
}