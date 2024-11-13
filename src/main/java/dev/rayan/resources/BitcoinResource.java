package dev.rayan.resources;

import dev.rayan.services.BitcoinService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("v1/bitcoins")
public final class BitcoinResource {

    @Inject
    BitcoinService service;

    @GET
    @Path("/quote")
    public Response quote(){
        return Response
                .ok(service.quoteBitcoin())
                .build();
    }

}
