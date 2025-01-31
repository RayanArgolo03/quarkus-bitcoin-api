package dev.rayan.resources;

import dev.rayan.services.AdressService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path(AddressResource.RESOUCE_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class AddressResource {

    public static final String RESOUCE_PATH = "/api/v1/addresses";

    @Inject
    AdressService service;

    @Inject
    Logger log;

    @GET
    @Authenticated
    @Path("/{cep}")
    public Response findAdressByCep(@PathParam("cep")
                                    @NotBlank(message = "Required CEP!")
                                    @Pattern(regexp = "^\\d{8}$", message = "The CEP should have only 8 numbers!") final String cep) {

        log.info("Finding adress by cep");

        return Response.ok()
                .entity(service.findAdressByCep(cep))
                .build();
    }

}
