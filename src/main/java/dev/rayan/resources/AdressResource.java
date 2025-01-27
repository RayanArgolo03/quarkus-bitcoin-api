package dev.rayan.resources;

import dev.rayan.model.client.Address;
import dev.rayan.services.AdressService;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path(AdressResource.RESOUCE_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class AdressResource {

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
        Address adressByCep = service.findAdressByCep(cep);
        return Response.ok()
                .entity(adressByCep)
                .build();
    }

}
