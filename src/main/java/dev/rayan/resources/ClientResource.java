package dev.rayan.resources;

import dev.rayan.dto.request.TransactionRequest;
import dev.rayan.dto.respose.TransactionSummaryByTypeResponse;
import dev.rayan.enums.TransactionType;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import dev.rayan.model.client.Client;
import dev.rayan.services.TransactionService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
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
    TransactionService service;

    @POST
    @Transactional
    @Path("/buy-bitcoins")
    public Response buyBitcoins(@Valid final TransactionRequest request) {

        //Todo valida se cliente está logado

        log.info("Persisting buy transaction");
        final Transaction transaction = service.persistTransaction(request, TransactionType.PURCHASE);

        log.info("Creating resource URI");
        final URI uri = service.createUri(uriInfo, transaction.getId());

        log.info("Quoting bitcoin");
        final Bitcoin quote = service.quoteBitcoin();

        return Response.created(uri)
                .entity(service.getMappedTransaction(transaction, quote))
                .build();
    }


    @POST
    @Transactional
    @Path("/sell-bitcoins")
    public Response sellBitcoins(@Valid final TransactionRequest request) {

        //Todo verifica se cliente está logado

        log.info("Find all transactions");
        final List<Transaction> transactions = service.findAllTransactions(request.client());

        log.info("Validate sale quantity");
        service.validateQuantity(transactions, request.quantity());

        log.info("Persisting sale transaction");
        final Transaction transaction = service.persistTransaction(request, TransactionType.SALE);

        log.info("Creating resource URI");
        final URI uri = service.createUri(uriInfo, transaction.getId());

        log.info("Quoting bitcoin");
        final Bitcoin bitcoin = service.quoteBitcoin();

        return Response.created(uri)
                .entity(service.getMappedTransaction(transaction, bitcoin))
                .build();
    }

    @GET
    @Path("/wallet/transactions")
    public Response findTransactionSummaryByType(final Client client, @Valid @QueryParam("type") final TransactionType transactionType) {

        //Todo cliente precisa estar logado

        log.info("Finding transactions by type");
        final TransactionSummaryByTypeResponse response = service.findTransactionsByType(client, transactionType);

        return Response.ok(response)
                .build();
    }

}
