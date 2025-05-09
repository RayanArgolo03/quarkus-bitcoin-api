package dev.rayan.resources;

import dev.rayan.dto.request.transaction.*;
import dev.rayan.dto.response.transaction.BitcoinResponse;
import dev.rayan.dto.response.transaction.TransactionReportResponse;
import dev.rayan.dto.response.transaction.TransactionResponse;
import dev.rayan.enums.TransactionReportFormat;
import dev.rayan.enums.TransactionReportPeriod;
import dev.rayan.enums.TransactionType;
import dev.rayan.model.Client;
import dev.rayan.report.ReportAbstractFile;
import dev.rayan.report.ReportFactory;
import dev.rayan.services.BitcoinService;
import dev.rayan.services.ClientService;
import dev.rayan.services.KeycloakService;
import dev.rayan.services.TransactionService;
import dev.rayan.utils.EnumConverterUtils;
import dev.rayan.validation.EnumValidator;
import io.quarkus.security.Authenticated;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;
import org.eclipse.microprofile.jwt.Claims;
import org.hibernate.validator.constraints.UUID;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(TransactionResource.RESOURCE_PATH)
public final class TransactionResource {

    public static final String RESOURCE_PATH = "api/v1/transactions";

    @Inject
    TransactionService transactionService;

    @Inject
    BitcoinService bitcoinService;

    @Inject
    KeycloakService keycloakService;

    @Inject
    ClientService clientService;

    @Context
    UriInfo uriInfo;

    @Claim(standard = Claims.sub)
    ClaimValue<String> keycloakUserIdClaim;

    private static final Logger LOG = Logger.getLogger(TransactionResource.class);

    @POST
    @Transactional(rollbackOn = Exception.class)
    @RolesAllowed("user")
    @Path("/buy")
    public Response buyBitcoins(@Valid @NotNull(message = "Required value!") final TransactionRequest request) {

        final String keycloakUserId = keycloakUserIdClaim.getValue();

        LOG.info("Finding user email in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        LOG.info("Verifyning if is logged in");
        keycloakService.verifyIfLoggedIn(keycloakUserId);

        LOG.info("Finding client in the database");
        final Client client = clientService.findClientByEmail(email);

        LOG.info("Quoting bitcoin");
        final BitcoinResponse bitcoin = bitcoinService.quote();

        LOG.info("Persisting purchase transaction");
        final TransactionResponse response = transactionService.persist(
                request, client, TransactionType.PURCHASE, bitcoin
        );

        LOG.info("Creating resource URI");
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path("{id}")
                .resolveTemplate("id", response.id())
                .build();

        return Response.created(uri)
                .entity(response)
                .build();
    }

    @POST
    @Transactional(rollbackOn = Exception.class)
    @RolesAllowed("user")
    @Path("/sell")
    public Response sellBitcoins(@Valid @NotNull(message = "Required value!") final TransactionRequest request) {

        final String keycloakUserId = keycloakUserIdClaim.getValue();

        LOG.info("Finding user email in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        LOG.info("Verifyning if is logged in");
        keycloakService.verifyIfLoggedIn(keycloakUserId);

        LOG.info("Finding client in the database");
        final Client client = clientService.findClientByEmail(email);

        LOG.info("Validate sale quantity if has transactions");
        transactionService.validateTransaction(client, request.quantity());

        LOG.info("Quoting bitcoin");
        final BitcoinResponse bitcoin = bitcoinService.quote();

        LOG.info("Persisting sale transaction");
        final TransactionResponse response = transactionService.persist(request, client, TransactionType.SALE, bitcoin);

        LOG.info("Creating resource URI");
        final URI uri = uriInfo.getAbsolutePathBuilder()
                .path("{id}")
                .resolveTemplate("id", response.id())
                .build();

        return Response.created(uri)
                .entity(response)
                .build();
    }

    @GET
    @Authenticated
    @Path("/by-types")
    public Response findTransactionsByTypes(@Valid @BeanParam final TransactionByTypeRequest request) {

        final String keycloakUserId = keycloakUserIdClaim.getValue();

        LOG.info("Finding user email in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        LOG.info("Verifyning if is logged in");
        keycloakService.verifyIfLoggedIn(keycloakUserId);

        LOG.info("Finding client in the database");
        final Client client = clientService.findClientByEmail(email);

        LOG.info("Mapping string types to enums");
        final List<TransactionType> transactionTypes = EnumConverterUtils.convertEnums(TransactionType.class, request.types());

        return Response.ok(transactionService.findByTypes(request, client, transactionTypes))
                .build();
    }

    @GET
    @Authenticated
    @Path("/by-filters")
    public Response findTransactionsByFilters(@BeanParam @Valid final TransactionFiltersRequest request) {

        final String keycloakUserId = keycloakUserIdClaim.getValue();

        LOG.info("Finding user email in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        LOG.info("Verifyning if is logged in");
        keycloakService.verifyIfLoggedIn(keycloakUserId);

        LOG.info("Finding client in the database");
        final Client client = clientService.findClientByEmail(email);

        return Response.ok(transactionService.findByFilters(request, client))
                .build();
    }

    @GET
    @PermitAll
    @Path("/{id}")
    public Response findTransactionById(@PathParam("id")
                                        @UUID(message = "Invalid UUID!") final String transactionId) {

        final String keycloakUserId = keycloakUserIdClaim.getValue();

        LOG.info("Finding user email in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        LOG.info("Verifyning if is logged in");
        keycloakService.verifyIfLoggedIn(keycloakUserId);

        LOG.info("Finding client in the database");
        final Client client = clientService.findClientByEmail(email);

        LOG.info("Quoting bitcoin");
        final BitcoinResponse bitcoin = bitcoinService.quote();

        return Response.ok(transactionService.findById(transactionId, bitcoin))
                .build();
    }

    @GET
    @Authenticated
    @Path("/by-quantity")
    public Response findTransactionsByQuantity(@Valid @BeanParam final TransactionByQuantityRequest request) {

        final String keycloakUserId = keycloakUserIdClaim.getValue();

        LOG.info("Finding user email in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        LOG.info("Verifyning if is logged in");
        keycloakService.verifyIfLoggedIn(keycloakUserId);

        LOG.info("Finding client in the database");
        final Client client = clientService.findClientByEmail(email);

        return Response.ok(transactionService.findByQuantity(request, client))
                .build();
    }

    @GET
    @Authenticated
    @Path("/by-period")
    public Response findTransactionCountByPeriod(@QueryParam("period")
                                                 @EnumValidator(message = "Invalid period!", enumClass = TransactionReportPeriod.class) final String stringPeriod) {

        final String keycloakUserId = keycloakUserIdClaim.getValue();

        LOG.info("Finding user email in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        LOG.info("Verifyning if is logged in");
        keycloakService.verifyIfLoggedIn(keycloakUserId);

        LOG.info("Finding client in the database");
        final Client client = clientService.findClientByEmail(email);

        LOG.info("Mapping string period to enum");
        final TransactionReportPeriod period = EnumConverterUtils.convertEnum(TransactionReportPeriod.class, stringPeriod);

        return Response.ok(transactionService.findCountByPeriod(period, client))
                .build();
    }

    @GET
    @Consumes(value = MediaType.APPLICATION_FORM_URLENCODED)
    @RolesAllowed("user")
    @Path("/report")
    public Response createTransactionsReport(@Valid @BeanParam final TransactionReportRequest request)
            throws IOException, IllegalAccessException {

        final String keycloakUserId = keycloakUserIdClaim.getValue();

        LOG.info("Finding user email in keycloak");
        final String email = keycloakService.findUserEmailByKeycloakUserId(keycloakUserId);

        LOG.info("Verifyning if is logged in");
        keycloakService.verifyIfLoggedIn(keycloakUserId);

        LOG.info("Finding client in the database");
        final Client client = clientService.findClientByEmail(email);

        LOG.info("Mapping string period to enum");
        final TransactionReportPeriod period = EnumConverterUtils.convertEnum(TransactionReportPeriod.class, request.period());

        LOG.info("Mapping string format to enum");
        final TransactionReportFormat format = EnumConverterUtils.convertEnum(TransactionReportFormat.class, request.format());

        LOG.infof("Finding transaction report on %s", period);
        final TransactionReportResponse response = transactionService.findReport(period, client);

        LOG.info("Quoting bitcoin");
        final BitcoinResponse bitcoinResponse = bitcoinService.quote();

        LOG.infof("Generating report in the format %s", format);
        final ReportAbstractFile reportAbstractFile = ReportFactory.createReportFile(format);
        reportAbstractFile.createReport(response, period);

        return Response.ok("Report created and downloaded!")
                .build();
    }


/*    @GET
    @PermitAll
    @Path("/total-made")
    public double findTransactionsTotalMade() {
        return transactionService.findTotalMade();
    }*/

}
