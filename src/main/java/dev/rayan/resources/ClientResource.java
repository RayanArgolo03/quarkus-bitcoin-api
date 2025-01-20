package dev.rayan.resources;

import dev.rayan.dto.request.*;
import dev.rayan.dto.respose.ClientResponse;
import dev.rayan.dto.respose.CredentialResponse;
import dev.rayan.dto.respose.CredentialTokensResponse;
import dev.rayan.dto.respose.TransactionReportResponse;
import dev.rayan.enums.TransactionReportFormat;
import dev.rayan.enums.TransactionReportPeriod;
import dev.rayan.enums.TransactionType;
import dev.rayan.facade.ServiceFacade;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import dev.rayan.model.client.Client;
import dev.rayan.report.ReportAbstractFile;
import dev.rayan.report.ReportFileFactory;
import dev.rayan.utils.ConverterEnumUtils;
import dev.rayan.validation.EnumValidator;
import io.quarkus.security.Authenticated;
import io.smallrye.jwt.auth.principal.ParseException;
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
import static java.lang.String.format;

@Path(ClientResource.RESOUCE_PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class ClientResource {

    public static final String RESOUCE_PATH = "/api/v1/clients";

    @Context
    UriInfo uriInfo;

    @Inject
    ServiceFacade facade;

    @Inject
    Logger log;

    @GET
    @PermitAll
    @Path("/index-page")
    public Response index() {
        return Response.ok("Bitcoin Exchange by Rayan :)")
                .build();
    }

    @POST
    @Transactional
    @PermitAll
    @Path("/sign-up")
    public Response createCredential(@Valid final CredentialRequest request) {

        log.info("Persisting client credential in database and keycloak");
        final CredentialResponse response = facade.persistCredential(request);

        log.info("Creating uri info");
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path("/{id}")
                .resolveTemplate("id", response.getId())
                .build();

        //Front-end redirect to the login page after confirmation verification
        //The keycloak user id is used in path param to resend the verification email
        return Response.created(uri)
                .entity(format("The confirmation email was sent to: %s \nKeycloak user id: %s", response.getEmail(), response.getKeycloakUserId()))
                .build();
    }

    @PUT
    @PermitAll
    @Path("{keycloakUserId}/resent-verify-email")
    public Response resentVerifyEmail(@PathParam("keycloakUserId") final String keycloakUserId) {

        //Keycloak user id would be extracted by the URL in keycloak expired email response
        facade.sendVerifyEmail(keycloakUserId);

        //Front-end redirect to the login page after confirmation verification
        return Response.ok()
                .entity("Email forwarded!")
                .build();
    }

    @POST
    @PermitAll
    @Path("/login")
    public Response login(@Valid final CredentialRequest request) {

        log.info("Login and generate tokens");
        final CredentialTokensResponse response = facade.login(request);

        //Front-end redirect to index-page
        return Response.ok()
                .entity(response)
                .build();
    }

    @POST
    @RolesAllowed("user")
    @Transactional
    public Response createClient(@Valid final ClientRequest request) {

        log.info("Creating client");
        final ClientResponse response = facade.persistClient(request);

        log.info("Creating uri info");
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path("id")
                .resolveTemplate("{id}", null)
                .build();

        return Response.created(uri)
                .entity(request)
                .build();
    }

    /**
    @param request Token can also be defined as @CookieParam
    **/
    @GET
    @PermitAll
    @Path("/generate-new-tokens")
    public Response generateNewTokens(@Valid final RefreshTokenRequest request) throws ParseException {

        log.info("Validate and generate new tokens with refresh token");
        final CredentialTokensResponse response = facade.generateToken(request.refreshToken());

        return Response.ok()
                .entity(response)
                .build();
    }

    @GET
    @Authenticated
    @Path("/{id}")
    public Response findClientById(@PathParam("id") @NotNull(message = "Required id!") final UUID id) {

        final Client client = facade.findClientById(id);

        return Response.ok(null)
                .build();
    }

    @POST
    @Transactional
    @RolesAllowed("client")
    @Path("/buy-bitcoins")
    public Response buyBitcoins(@Context final SecurityContext context,
                                @Valid final TransactionRequest request) {


        log.info("Persisting buy transaction");
        final Transaction transaction = facade.persistTransaction(request, TransactionType.PURCHASE);

        log.info("Creating resource URI");
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path("id")
                .resolveTemplate("{id}", transaction.getId())
                .build();

        log.info("Quoting bitcoin");
        final Bitcoin quote = facade.quoteBitcoin();

        log.info("Returning mapped transaction");
        return Response.created(uri)
                .entity(facade.getMappedTransaction(transaction, quote))
                .build();
    }


    @POST
    @Transactional
    @RolesAllowed("client")
    @Path("/sell-bitcoins")
    public Response sellBitcoins(@Context final SecurityContext context,
                                 @Valid final TransactionRequest request) {

        log.info("Find all transactions");
        final List<Transaction> transactions = facade.findAllTransactions(request.client());

        log.info("Validate sale quantity");
        facade.validateBitcoinQuantity(transactions, request.quantity());

        log.info("Persisting sale transaction");
        final Transaction transaction = facade.persistTransaction(request, TransactionType.SALE);

        log.info("Creating resource URI");
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path("id")
                .resolveTemplate("{id}", transaction.getId())
                .build();

        log.info("Quoting bitcoin");
        final Bitcoin bitcoin = facade.quoteBitcoin();

        return Response.created(uri)
                .entity(facade.getMappedTransaction(transaction, bitcoin))
                .build();
    }

    @GET
    @RolesAllowed("client")
    @Path("/{clientId}/transactions/summary-by-types")
    public Response findTransactionsSummaryByTypes(@Context final SecurityContext context,
                                                   @HeaderParam("Authorization") final String token,
                                                   @PathParam("clientId") final UUID clientId,
                                                   @QueryParam("type") final List<String> types) {

        System.out.println();
        facade.findClientById(clientId);

        log.info("Mapping string types to enum");
        final List<TransactionType> transactionTypes = ConverterEnumUtils.convertEnums(TransactionType.class, types);

        log.info("Finding transactions by types");
        return Response.ok(facade.findTransactionsSummaryByType(clientId, transactionTypes))
                .build();
    }

    @GET
    @RolesAllowed("client")
    @Path("/{clientId}/transactions/summary-by-filters")
    public Response findTransactionsSummaryByFilters(@Context final SecurityContext context,
                                                     @PathParam("clientId") final UUID clientId,
                                                     @BeanParam @Valid final TransactionFiltersRequest request) {

        log.info("Finding transactions summary by filters");
        return Response.ok(facade.findTransactionSummaryByFilters(clientId, request))
                .build();
    }

    @GET
    @Authenticated
    @Path("/{clientId}/transactions/{transactionId}")
    public Response findTransactionById(@Context final SecurityContext context,
                                        @PathParam("clientId") final UUID clientId,
                                        @PathParam("transactionId") final UUID transactionId) {

        log.info("Finding transaction by id");
        final Transaction transaction = facade.findTransactionById(transactionId);

        log.info("Quoting bitcoin");
        final Bitcoin bitcoin = facade.quoteBitcoin();

        return Response.ok(facade.getMappedTransaction(transaction, bitcoin))
                .build();
    }

    @GET
    @RolesAllowed("client")
    @Path("/{clientId}/transactions/last-transaction-by-quantity")
    public Response findTransactionByQuantity(@Context final SecurityContext context,
                                              @PathParam("clientId") final UUID clientId,
                                              @Valid @BeanParam final TransactionByQuantityRequest request) {


        log.infof("Finding transaction by quantity and sort created at %s", request.getSortCreatedAtDirection());
        final Transaction transaction = facade.findTransactionByQuantity(clientId, request.getQuantity(), request.getSortCreatedAtDirection());

        log.info("Quoting bitcoin");
        final Bitcoin bitcoin = facade.quoteBitcoin();

        return Response.ok(facade.getMappedTransaction(transaction, bitcoin))
                .build();
    }


    @GET
    @RolesAllowed("client")
    @Path("/{clientId}/transactions/report")
    public Response createTransactionReport(@Context final SecurityContext context,
                                            @PathParam("clientId") final UUID clientId,
                                            @QueryParam("format") @EnumValidator(enumClass = TransactionReportFormat.class) String format,
                                            @QueryParam("period") @EnumValidator(enumClass = TransactionReportPeriod.class) String period) {

        log.info("Mapping string period to enum");
        final TransactionReportPeriod reportPeriod = ConverterEnumUtils.convertEnum(TransactionReportPeriod.class, period);

        log.infof("Finding transaction report on %s", reportPeriod);
        final TransactionReportResponse reportResponse = facade.findTransactionReport(clientId, reportPeriod);

        //quoteBitcoin throws ApiException, if this occurred the bitcoin attributes will become unavailable
        log.info("Quoting bitcoin");
        final Bitcoin bitcoin = facade.quoteBitcoin();

        log.info("Setting bitcoin attributes in reportResponse");
        facade.setBitcoinAttributesInResponse(reportResponse, bitcoin);

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
