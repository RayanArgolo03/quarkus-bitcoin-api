package dev.rayan.resources;


import dev.rayan.dto.request.TransactionCreatedDTO;
import dev.rayan.model.bitcoin.Transaction;
import dev.rayan.model.client.Client;
import dev.rayan.services.ClientService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path(ClientResource.RESOUCE_PATH)
public final class ClientResource {

    public static final String RESOUCE_PATH = "/api/v1/clients";

    @Inject
    Logger log;

    @Inject
    BitcoinResource bitcoinResource;

    @Inject
    ClientService service;

    @POST
    @Transactional
    @Path("/buy-bitcoins")
    public Response buy(final TransactionCreatedDTO dto) {

        //Todo continue analisando e nos vídeos, continue curso alura

        log.info("Creating transaction");
        final Transaction transaction = service.createTransaction(dto);

        log.info("Persisting transaction");
        Transaction.persist(transaction);

        //Todo created e URI Info no retorno
        return Response.ok(transaction)
                .build();
    }

}
