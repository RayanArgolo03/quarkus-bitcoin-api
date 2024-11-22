package dev.rayan.resources;


import dev.rayan.dto.request.TransactionRequest;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import dev.rayan.services.ClientService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;
import org.mapstruct.Mapper;

import java.net.URI;

@Path(ClientResource.RESOUCE_PATH)
public final class ClientResource {

    public static final String RESOUCE_PATH = "/api/v1/clients";

    @Context
    UriInfo uriInfo;

    @Inject
    Logger log;

    @Inject
    ClientService service;

    @POST
    @Transactional
    @Path("/buy-bitcoins")
    public Response buyBitcoins(@Valid final TransactionRequest dto) {

        log.info("Creating transaction");
        final Transaction transaction = service.createTransaction(dto);

        log.info("Persisting transaction");
        Transaction.persist(transaction);

        log.info("Creating resource URI");
        final URI uri = uriInfo.getRequestUriBuilder()
                .path("{id}")
                .resolveTemplate("id", transaction.getId())
                .build();

        try {

            log.info("Quoting bitcoin using Adapter to display information");
            final Bitcoin quote = service.quote();

            return Response.created(uri)
                    .entity(service.getMappedTransaction(transaction, quote))
                    .build();

        } catch (WebApplicationException e) {

            log.errorf("Bitcoin API is down, value and date unavailable! %s", e.getMessage());
            return Response.created(uri)
                    .entity(service.getMappedTransaction(transaction, null))
                    .build();

        }
    }

}
