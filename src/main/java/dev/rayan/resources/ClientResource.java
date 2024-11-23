package dev.rayan.resources;


import dev.rayan.dto.request.TransactionRequest;
import dev.rayan.exceptions.ApiException;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import dev.rayan.services.ClientService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;

import java.net.URI;

@Path(ClientResource.RESOUCE_PATH)
public final class ClientResource {

    //TODO continue operações do Repository

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
    public Response buyBitcoins(final TransactionRequest request) {

        log.info("Creating transaction");
        final Transaction transaction = service.createTransaction(request);

        log.info("Persisting transaction");
        Transaction.persist(transaction);

        log.info("Creating resource URI");
        final URI uri = uriInfo.getRequestUriBuilder()
                .path("{id}")
                .resolveTemplate("id", transaction.getId())
                .build();
        try {

            log.info("Quoting bitcoin using Adapter to display information");
            final Bitcoin quote = service.quoteBitcoin();

            return Response.created(uri)
                    .entity(service.getMappedTransaction(transaction, quote))
                    .build();

        } catch (ApiException e) {

            log.error("Bitcoin API is down, value and date unavailable!");
            return Response.created(uri)
                    .entity(service.getMappedTransaction(transaction, e.getMessage()))
                    .build();

        }
    }

}
