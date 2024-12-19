package dev.rayan.resources;

import dev.rayan.services.ClientService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;

@Path(ClientResource.RESOUCE_PATH)
public final class ClientResource {

    public static final String RESOUCE_PATH = "/api/v1/clients";

    @Context
    UriInfo uriInfo;

    @Inject
    ClientService service;

    @Inject
    Logger log;

    @GET
    public Response findClients() {
        return Response.ok(service.findClients())
                .build();
    }


}
