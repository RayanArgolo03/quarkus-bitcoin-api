package dev.rayan.resources;

import dev.rayan.services.BitcoinService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/bitcoins")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class BitcoinResource {

    //Todo continuar entendendo consumo de api externa

    @Inject
    BitcoinService service;

    @GET
    @Path("/quote")
    public Response quote() {
        return Response
                .ok(service.quoteBitcoin())
                .build();
    }

}
