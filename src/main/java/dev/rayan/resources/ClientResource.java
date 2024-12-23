package dev.rayan.resources;

import dev.rayan.dto.request.ClientRequest;
import dev.rayan.dto.request.TransactionByQuantityRequest;
import dev.rayan.dto.request.TransactionFiltersRequest;
import dev.rayan.dto.request.TransactionRequest;
import dev.rayan.dto.respose.TransactionReportResponse;
import dev.rayan.enums.TransactionReportFormat;
import dev.rayan.enums.TransactionReportPeriod;
import dev.rayan.enums.TransactionType;
import dev.rayan.enums.validation.EnumValidator;
import dev.rayan.factory.ReportFileFactory;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import dev.rayan.model.client.Client;
import dev.rayan.report.ReportAbstractFile;
import dev.rayan.services.ClientService;
import dev.rayan.services.TransactionService;
import dev.rayan.utils.ConverterEnumUtils;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

@Path(ClientResource.RESOUCE_PATH)
public final class ClientResource {

    public static final String RESOUCE_PATH = "/api/v1/clients";

    @Context
    UriInfo uriInfo;

    @Inject
    TransactionService transactionService;

    @Inject
    ClientService clientService;

    @Inject
    Logger log;

    //Todo criar método, persistir cliente e testar
    // métodos com clientes persistidos
    @POST
    @Transactional
    @PermitAll
    public Response createClient(@Valid final ClientRequest request) {

        log.info("Creating client");
        final Client client = clientService.persistClient(request);

        log.info("Creating uri info");
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path("id")
                .resolveTemplate("{id}", client.getId())
                .build();

        return Response.created(uri)
                .entity(clientService.getMappedClient(client))
                .build();
    }

    @GET
    @Path("{id}")
    @Authenticated
    public Response findClientById(@PathParam("id") @NotNull(message = "Required id!") final UUID id) {

        log.info("Finding client by id");
        final Client client = clientService.findById(id);

        return Response.ok(clientService.getMappedClient(client))
                .build();
    }

    @POST
    @Transactional
    @RolesAllowed("client")
    @Path("/buy-bitcoins")
    public Response buyBitcoins(@Context final SecurityContext context,
                                @Valid final TransactionRequest request) {

        if (!clientService.validClient(context, request.client().getId())) {
            return Response.status(UNAUTHORIZED)
                    .build();
        }

        log.info("Persisting buy transaction");
        final Transaction transaction = transactionService.persistTransaction(request, TransactionType.PURCHASE);

        log.info("Creating resource URI");
        final URI uri = transactionService.createUri(uriInfo, transaction.getId());

        log.info("Quoting bitcoin");
        final Bitcoin quote = transactionService.quoteBitcoin();

        log.info("Returning mapped transaction");
        return Response.created(uri)
                .entity(transactionService.getMappedTransaction(transaction, quote))
                .build();
    }


    @POST
    @Transactional
    @RolesAllowed("client")
    @Path("/sell-bitcoins")
    public Response sellBitcoins(@Context final SecurityContext context,
                                 @Valid final TransactionRequest request) {

        if (!clientService.validClient(context, request.client().getId())) {
            return Response.status(UNAUTHORIZED)
                    .build();
        }

        log.info("Find all transactions");
        final List<Transaction> transactions = transactionService.findAllTransactions(request.client());

        log.info("Validate sale quantity");
        transactionService.validateQuantity(transactions, request.quantity());

        log.info("Persisting sale transaction");
        final Transaction transaction = transactionService.persistTransaction(request, TransactionType.SALE);

        log.info("Creating resource URI");
        final URI uri = transactionService.createUri(uriInfo, transaction.getId());

        log.info("Quoting bitcoin");
        final Bitcoin bitcoin = transactionService.quoteBitcoin();

        return Response.created(uri)
                .entity(transactionService.getMappedTransaction(transaction, bitcoin))
                .build();
    }

    @GET
    @RolesAllowed("client")
    @Path("{clientId}/transactions/summary-by-types")
    public Response findTransactionsSummaryByTypes(@Context final SecurityContext context,
                                                   @PathParam("clientId") final UUID clientId,
                                                   @QueryParam("type") final List<String> types) {

        if (!clientService.validClient(context, clientId)) {
            return Response.status(UNAUTHORIZED)
                    .build();
        }

        log.info("Mapping string types to enum");
        final List<TransactionType> transactionTypes = ConverterEnumUtils.convertEnums(TransactionType.class, types);

        log.info("Finding transactions by types");
        return Response.ok(transactionService.findTransactionsSummaryByType(clientId, transactionTypes))
                .build();
    }

    @GET
    @RolesAllowed("client")
    @Path("{clientId}/transactions/summary-by-filters")
    public Response findTransactionsSummaryByFilters(@Context final SecurityContext context,
                                                     @PathParam("clientId") final UUID clientId,
                                                     @BeanParam @Valid final TransactionFiltersRequest request) {

        if (!clientService.validClient(context, clientId)) {
            return Response.status(UNAUTHORIZED)
                    .build();
        }

        log.info("Finding transactions summary by filters");
        return Response.ok(transactionService.findTransactionSummaryByFilters(clientId, request))
                .build();
    }

    @GET
    @Authenticated
    @Path("{clientId}/transactions/{transactionId}")
    public Response findTransactionById(@Context final SecurityContext context,
                                        @PathParam("clientId") final UUID clientId,
                                        @PathParam("transactionId") final UUID id) {

        if (!clientService.validClient(context, clientId)) {
            return Response.status(UNAUTHORIZED)
                    .build();
        }

        log.info("Finding transaction by id");
        final Transaction transaction = transactionService.findTransactionById(id);

        log.info("Quoting bitcoin");
        final Bitcoin bitcoin = transactionService.quoteBitcoin();

        return Response.ok(transactionService.getMappedTransaction(transaction, bitcoin))
                .build();
    }

    @GET
    @RolesAllowed("client")
    @Path("{clientId}/transactions/last-transaction-by-quantity")
    public Response findTransactionByQuantity(@Context final SecurityContext context,
                                              @PathParam("clientId") final UUID clientId,
                                              @Valid @BeanParam final TransactionByQuantityRequest request) {

        if (!clientService.validClient(context, clientId)) {
            return Response.status(UNAUTHORIZED)
                    .build();
        }

        log.infof("Finding transaction by quantity and sort created at %s", request.getSortCreatedAtDirection());
        final Transaction transaction = transactionService.findTransactionByQuantity(clientId, request.getQuantity(), request.getSortCreatedAtDirection());

        log.info("Quoting bitcoin");
        final Bitcoin bitcoin = transactionService.quoteBitcoin();

        return Response.ok(transactionService.getMappedTransaction(transaction, bitcoin))
                .build();
    }


    @GET
    @RolesAllowed("client")
    @Path("{clientId}/transactions/report")
    public Response createTransactionReport(@Context final SecurityContext context,
                                            @PathParam("clientId") final UUID clientId,
                                            @QueryParam("format") @EnumValidator(enumClass = TransactionReportFormat.class) String format,
                                            @QueryParam("period") @EnumValidator(enumClass = TransactionReportPeriod.class) String period) {

        if (!clientService.validClient(context, clientId)) {
            return Response.status(UNAUTHORIZED)
                    .build();
        }

        log.info("Mapping string period to enum");
        final TransactionReportPeriod reportPeriod = ConverterEnumUtils.convertEnum(TransactionReportPeriod.class, period);

        log.infof("Finding transaction report on %s", reportPeriod);
        final TransactionReportResponse reportResponse = transactionService.findTransactionReport(clientId, reportPeriod);

        //quoteBitcoin throws ApiException, if this occurred the bitcoin attributes will become unavailable
        log.info("Quoting bitcoin");
        final Bitcoin bitcoin = transactionService.quoteBitcoin();

        log.info("Setting bitcoin attributes in reportResponse");
        transactionService.setBitcoinAttributesInResponse(reportResponse, bitcoin);

        log.info("Mapping string report format to enum");
        final TransactionReportFormat reportFormat = ConverterEnumUtils.convertEnum(TransactionReportFormat.class, format);

        try {
            log.infof("Generating report in format %s", reportFormat);
            final ReportAbstractFile reportAbstractFile = ReportFileFactory.createReportAbstractFile(reportFormat);
            reportAbstractFile.createReport(reportResponse, reportPeriod);

            return Response.ok("Report created and downloaded!")
                    .build();

        } catch (IllegalAccessException | IOException e) {
            throw new WebApplicationException("Server error! Contact the support in Linkedin: rayan_argolo", INTERNAL_SERVER_ERROR);
        }
    }


}
