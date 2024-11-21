package dev.rayan.resources;


import dev.rayan.dto.request.TransactionRequestDTO;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import dev.rayan.services.ClientService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path(ClientResource.RESOUCE_PATH)
public final class ClientResource {

    public static final String RESOUCE_PATH = "/api/v1/clients";

    @Inject
    Logger log;

    @Inject
    ClientService service;

    @POST
    @Transactional
    @Path("/buy-bitcoins")
    public Response buy(final TransactionRequestDTO dto) {

        log.info("Creating transaction");
        final Transaction transaction = service.createTransaction(dto);

        log.info("Persisting transaction");
        Transaction.persist(transaction);

        try {
            log.info("Quoting bitcoin using Adapter to display information");
            final Bitcoin quote = service.quote();

            //Todo created e URI Info no retorno
            return Response.ok(service.getMappedTransaction(transaction, quote))
                    .build();

        } catch (WebApplicationException e) {

            log.errorf("Bitcoin API is down! %s", e.getMessage());

            //Todo created e URI Info no retorno
            return Response.status(e.getResponse().getStatus())
                    .entity(service.getMappedTransaction(transaction, null))
                    .build();

        }

    }

}
