package dev.rayan.resources;

import dev.rayan.dto.request.transaction.*;
import dev.rayan.dto.response.bitcoin.BitcoinResponse;
import dev.rayan.dto.response.transaction.TransactionReportResponse;
import dev.rayan.dto.response.transaction.TransactionResponse;
import dev.rayan.enums.TransactionReportFormat;
import dev.rayan.enums.TransactionReportPeriod;
import dev.rayan.enums.TransactionType;
import dev.rayan.model.Client;
import dev.rayan.report.ReportAbstractFile;
import dev.rayan.report.ReportFactory;
import dev.rayan.services.ClientService;
import dev.rayan.services.KeycloakService;
import dev.rayan.services.TransactionService;
import dev.rayan.utils.EnumConverterUtils;
import dev.rayan.validation.EnumValidator;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
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

        log.info("Finding and verifyning if email exists in keycloak");
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

        log.info("Finding and verifyning if email exists in keycloak");
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

        log.info("Finding and verifyning if email exists in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(token.getSubject());

        log.info("Finding client in the database");
        final Client client = clientService.findClientByEmail(email);

        log.info("Mapping string types to enums");
        final List<TransactionType> transactionTypes = EnumConverterUtils.convertEnums(TransactionType.class, request.getTypes());

        return Response.ok(transactionService.findTransactionsByType(request, client))
                .build();
    }

    @GET
    @RolesAllowed("user")
    @Path("/by-filters")
    public Response findTransactionsByFilters(@BeanParam @Valid final TransactionFiltersRequest request) {

        log.info("Finding and verifyning if email exists in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(token.getSubject());

        log.info("Finding client in the database");
        final Client client = clientService.findClientByEmail(email);

        return Response.ok(transactionService.findTransactionsByFilters(client, request))
                .build();
    }

    @GET
    @Authenticated
    @Path("/{transactionId}")
    public Response findTransactionById(@PathParam("transactionId") final UUID transactionId) {

        log.info("Finding and verifyning if email exists in keycloak");
        keycloakService.findUserEmailByKeycloakUserId(token.getSubject());

        log.info("Quoting bitcoin");
        final BitcoinResponse bitcoin = transactionService.quoteBitcoin();

        return Response.ok(transactionService.findTransactionById(transactionId, bitcoin))
                .build();
    }

    @GET
    @RolesAllowed("user")
    @Path("/by-quantity")
    public Response findTransactionsByQuantity(@Valid @BeanParam final TransactionByQuantityRequest request) {

        log.info("Finding and verifyning if email exists in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(token.getSubject());

        log.info("Finding client in the database");
        final Client client = clientService.findClientByEmail(email);

        return Response.ok(transactionService.findTransactionByQuantity(client, request))
                .build();
    }

    @GET
    @RolesAllowed("user")
    @Path("/by-period")
    public Response findTransactionCountByPeriod(@QueryParam("period")
                                                 @EnumValidator(enumClass = TransactionReportPeriod.class) final String stringPeriod) {

        log.info("Finding and verifyning if email exists in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(token.getSubject());

        log.info("Finding client in the database");
        final Client client = clientService.findClientByEmail(email);

        log.info("Mapping string period to enum");
        final TransactionReportPeriod period = EnumConverterUtils.convertEnum(TransactionReportPeriod.class, stringPeriod);

        return Response.ok(transactionService.findTransactionCountByPeriod(client, period))
                .build();
    }


    @GET
    @RolesAllowed("user")
    @Path("/report")
    public Response createTransactionsReport(@Valid @BeanParam final TransactionReportRequest request) throws IOException, IllegalAccessException {

        log.info("Finding and verifyning if email exists in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(token.getSubject());

        log.info("Finding client in the database");
        final Client client = clientService.findClientByEmail(email);

        log.info("Mapping string period to enum");
        final TransactionReportPeriod period = EnumConverterUtils.convertEnum(TransactionReportPeriod.class, request.getPeriod());

        log.info("Mapping string format to enum");
        final TransactionReportFormat format = EnumConverterUtils.convertEnum(TransactionReportFormat.class, request.getFormat());

        log.infof("Finding transaction report on %s", period);
        final TransactionReportResponse response = transactionService.findTransactionReport(client, period);

        log.info("Quoting bitcoin");
        final BitcoinResponse bitcoinResponse = transactionService.quoteBitcoin();

        log.info("Setting bitcoin current value in response");
        response.setBitcoinCurrentValue(
                transactionService.quoteBitcoin().priceFormatted()
        );

        log.infof("Generating report in the format %s", format);
        final ReportAbstractFile reportAbstractFile = ReportFactory.createReportFile(format);
        reportAbstractFile.createReport(response, period);

        return Response.ok("Report created and downloaded!")
                .build();
    }


}
