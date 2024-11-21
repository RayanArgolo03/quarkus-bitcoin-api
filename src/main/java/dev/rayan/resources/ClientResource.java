package dev.rayan.resources;


import dev.rayan.dto.request.TransactionRequest;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import dev.rayan.services.ClientService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.jboss.logging.Logger;

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
    public Response buy(final TransactionRequest dto) {

        log.info("Creating transaction");
        final Transaction transaction = service.createTransaction(dto);

        log.info("Persisting transaction");
        Transaction.persist(transaction);

        log.info("Creating URI");
        URI uri = uriInfo.getBaseUriBuilder()
                .path(ClientResource.class, "buy")
                .resolveTemplate("id", transaction.getId())
                .build();

        try {
            log.info("Quoting bitcoin using Adapter to display information");
            final Bitcoin quote = service.quote();

            return Response.created(uri)
                    .entity(service.getMappedTransaction(transaction, quote))
                    .build();

        } catch (WebApplicationException e) {

            log.errorf("Bitcoin API is down! %s", e.getMessage());

            return Response.created(uri)
                    .entity(service.getMappedTransaction(transaction, null))
                    .build();

        }

    }

}
