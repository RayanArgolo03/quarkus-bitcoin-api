package dev.rayan.resources;

import dev.rayan.dto.request.transaction.TransactionByQuantityRequest;
import dev.rayan.dto.request.transaction.TransactionByTypeRequest;
import dev.rayan.dto.request.transaction.TransactionFiltersRequest;
import dev.rayan.dto.request.transaction.TransactionRequest;
import dev.rayan.dto.response.transaction.TransactionReportResponse;
import dev.rayan.dto.response.transaction.TransactionResponse;
import dev.rayan.enums.TransactionReportFormat;
import dev.rayan.enums.TransactionReportPeriod;
import dev.rayan.enums.TransactionType;
import dev.rayan.exceptions.ApiException;
import dev.rayan.model.Client;
import dev.rayan.report.ReportAbstractFile;
import dev.rayan.report.ReportFileFactory;
import dev.rayan.dto.response.bitcoin.BitcoinResponse;
import dev.rayan.model.Transaction;
import dev.rayan.services.ClientService;
import dev.rayan.services.KeycloakService;
import dev.rayan.services.TransactionService;
import dev.rayan.utils.EnumConverterUtils;
import dev.rayan.validation.EnumValidator;
import io.quarkus.panache.common.Sort;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(TransactionResource.RESOURCE_PATH)
public final class TransactionResource {

    public static final String RESOURCE_PATH = "api/v1/transactions";

    @Inject
    TransactionService transactionService;

    @Inject
    KeycloakService keycloakService;

    @Inject
    ClientService clientService;

    @Inject
    Logger log;

    @Context
    UriInfo uriInfo;

    @Inject
    JsonWebToken token;

    @POST
    @Transactional
    @RolesAllowed("user")
    @Path("/buy-bitcoins")
    public Response buyBitcoins(@Valid final TransactionRequest request) {

        log.info("Verifyning if credential exists in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(token.getSubject());

        log.info("Finding client in the database");
        final Client client = clientService.findClientByEmail(email);

        log.info("Quoting bitcoin");
        final BitcoinResponse bitcoin = transactionService.quoteBitcoin();

        log.info("Persisting purchase transaction");
        final TransactionResponse response = transactionService.persist(request, client, TransactionType.PURCHASE, bitcoin);

        log.info("Creating resource URI");
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path("{id}")
                .resolveTemplate("id", response.id())
                .build();

        return Response.created(uri)
                .entity(response)
                .build();
    }

    @POST
    @Transactional
    @RolesAllowed("user")
    @Path("/sell-bitcoins")
    public Response sellBitcoins(@Valid final TransactionRequest request) {

        log.info("Verifyning if credential exists in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(token.getSubject());

        log.info("Finding client in the database");
        final Client client = clientService.findClientByEmail(email);

        log.info("Validate sale quantity if has transactions");
        transactionService.validateTransaction(client, request.quantity());

        log.info("Quoting bitcoin");
        final BitcoinResponse bitcoin = transactionService.quoteBitcoin();

        log.info("Persisting sale transaction");
        final TransactionResponse response = transactionService.persist(request, client, TransactionType.SALE, bitcoin);

        log.info("Creating resource URI");
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path("{id}")
                .resolveTemplate("id", response.id())
                .build();

        return Response.created(uri)
                .entity(response)
                .build();
    }

    @GET
    @RolesAllowed("user")
    @Path("/by-types")
    public Response findTransactionsByTypes(@Valid @BeanParam final TransactionByTypeRequest request) {

        log.info("Verifyning if credential exists in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(token.getSubject());

        log.info("Finding client in the database");
        final Client client = clientService.findClientByEmail(email);

        log.info("Mapping string types to enums");
        final List<TransactionType> transactionTypes = EnumConverterUtils.convertEnums(TransactionType.class, request.getTypes());

        return Response.ok(transactionService.findTransactionsSummaryByType(request, client))
                .build();
    }

    @GET
    @RolesAllowed("client")
    @Path("/summary-by-filters")
    public Response findTransactionsSummaryByFilters(@Context final SecurityContext context,
                                                     @BeanParam @Valid final TransactionFiltersRequest request) {

        log.info("Finding transactions summary by filters");
        return Response.ok(transactionService.findTransactionSummaryByFilters(null, request))
                .build();
    }

    @GET
    @Authenticated
    @Path("/{transactionId}")
    public Response findTransactionById(@Context final SecurityContext context,
                                        @PathParam("transactionId") final UUID transactionId) {

        log.info("Finding transaction by id");
        final Transaction transaction = transactionService.findTransactionById(transactionId);

        log.info("Quoting bitcoin");
        final BitcoinResponse bitcoinResponse = transactionService.quoteBitcoin();

        return Response.ok(null)
                .build();
    }

    @GET
    @RolesAllowed("client")
    @Path("/last-transaction-by-quantity")
    public Response findTransactionByQuantity(@Context final SecurityContext context,
                                              @Valid @BeanParam final TransactionByQuantityRequest request) {


        log.infof("Finding transaction by totalQuantity and sort created at %s", request.getSortCreatedAtDirection());
        final Transaction transaction = transactionService.findTransactionByQuantity(null, request.getQuantity(), request.getSortCreatedAtDirection());

        log.info("Quoting bitcoin");
        final BitcoinResponse bitcoinResponse = transactionService.quoteBitcoin();

        return Response.ok(null)
                .build();
    }


    @GET
    @RolesAllowed("client")
    @Path("/report")
    public Response createTransactionReport(@Context final SecurityContext context,
                                            @QueryParam("format") @EnumValidator(enumClass = TransactionReportFormat.class) String format,
                                            @QueryParam("period") @EnumValidator(enumClass = TransactionReportPeriod.class) String period) {

        log.info("Mapping string period to enum");
        final TransactionReportPeriod reportPeriod = EnumConverterUtils.convertEnum(TransactionReportPeriod.class, period);

        log.infof("Finding transaction report on %s", reportPeriod);
        final TransactionReportResponse reportResponse = transactionService.findTransactionReport(null, reportPeriod);

        //quoteBitcoin throws ApiException, if this occurred the bitcoin attributes will become unavailable
        log.info("Quoting bitcoin");
        final BitcoinResponse bitcoinResponse = transactionService.quoteBitcoin();

        log.info("Setting bitcoin attributes in reportResponse");
        transactionService.setBitcoinAttributesInResponse(reportResponse, bitcoinResponse);

        log.info("Mapping string report format to enum");
        final TransactionReportFormat reportFormat = EnumConverterUtils.convertEnum(TransactionReportFormat.class, format);

        try {
            log.infof("Generating report in format %s", reportFormat);
            final ReportAbstractFile reportAbstractFile = ReportFileFactory.createReportAbstractFile(reportFormat);
            reportAbstractFile.createReport(reportResponse, reportPeriod);

            return Response.ok("Report created and downloaded!")
                    .build();

        } catch (IllegalAccessException | IOException e) {
            throw new ApiException("Server error! Contact the support in Linkedin: rayan_argolo");
        }
    }


}
