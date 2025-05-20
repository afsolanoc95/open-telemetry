package org.diplomado.controller;

import jakarta.inject.Inject;
import org.apache.camel.ProducerTemplate;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/vuelo")
public class GreetingResource {

    @Inject
    ProducerTemplate producerTemplate;

    /*@GET
    @Path("/salud/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String obtenerSalud(@PathParam("id") String id) {
        return producerTemplate.requestBodyAndHeader("direct:obtenerSaludPersonaje", null, "id", id, String.class);
    }*/

    @GET
    @Path("/reserva/{reserva}")
    public String hello(@PathParam("reserva") String reserva) {
        return  producerTemplate.requestBodyAndHeader("direct:infoReserva", null, "reserva",reserva, String.class);
    }
}