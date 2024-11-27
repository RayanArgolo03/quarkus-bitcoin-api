package dev.rayan.resources;


import dev.rayan.dto.request.TransactionRequest;
import dev.rayan.enums.TransactionType;
import dev.rayan.exceptions.ApiException;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import dev.rayan.services.ClientService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.logging.Logger;

import java.net.URI;
import java.util.List;

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
    public Response buyBitcoins(final TransactionRequest request) {

        //Todo valida se cliente está logado

        log.info("Persisting transaction");
        final Transaction transaction = service.persistTransaction(request, TransactionType.BUY);

        log.info("Creating resource URI");
        final URI uri = uriInfo.getRequestUriBuilder()
                .path("{id}")
                .resolveTemplate("id", transaction.getId())
                .build();

        log.info("Quoting bitcoin using Adapter to display information");
        final Bitcoin quote = service.quoteBitcoin();

        return Response.created(uri)
                .entity(service.getMappedTransaction(transaction, quote))
                .build();
    }


    @GET()
    @Path("/sell-bitcoins")
    public Response sellBitcoins(final TransactionRequest request) {

        //Todo verifica se cliente está logado

        log.info("Find all transactions");
        List<Transaction> transactions = service.findAll(request);

        log.info("Validate quantity");
        service.validateQuantity(transactions, request.quantity());

        //Todo testa e retorna URI do objeto criado

        return Response.ok(Transaction.listAll())
                .build();
    }

}
