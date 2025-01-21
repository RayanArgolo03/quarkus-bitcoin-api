package dev.rayan.facade;

import dev.rayan.dto.request.*;
import dev.rayan.dto.respose.*;
import dev.rayan.enums.TransactionReportPeriod;
import dev.rayan.enums.TransactionType;
import dev.rayan.model.bitcoin.Bitcoin;
import dev.rayan.model.bitcoin.Transaction;
import dev.rayan.model.client.Client;
import dev.rayan.model.client.Credential;
import dev.rayan.services.ClientService;
import dev.rayan.services.CredentialService;
import dev.rayan.services.KeycloakService;
import dev.rayan.services.TransactionService;
import io.quarkus.panache.common.Sort;
import io.smallrye.jwt.auth.principal.ParseException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static jakarta.ws.rs.core.Response.Status.UNAUTHORIZED;

@ApplicationScoped
public final class ServiceFacade {

    @Inject
    TransactionService transactionService;

    @Inject
    ClientService clientService;

    @Inject
    CredentialService credentialService;

    @Inject
    KeycloakService keycloakService;

    public CredentialResponse persistCredential(final CredentialRequest request) {

        final CredentialResponse response = credentialService.persist(request);
        keycloakService.persist(response);

        return response;
    }

    public CredentialTokensResponse login(final CredentialRequest request) {
        final CredentialResponse response = credentialService.login(request);
        return keycloakService.login(response);
    }

    public void resentVerifyEmail(final String keycloakUserId) {
        keycloakService.resendVerifyEmail(keycloakUserId);
    }


    public CredentialTokensResponse generateToken(final String refreshToken) throws ParseException {

        final JsonWebToken token = keycloakService.validateToken(refreshToken);

        final Credential credential = credentialService.findCredential(token.getName())
                .orElseThrow(() -> new NotAuthorizedException("Refresh token belongs to a non-existent client!", UNAUTHORIZED));

        return keycloakService.generateNewTokens(
                credentialService.mapCredentialToResponse(credential)
        );
    }

    public void logout(final String authorizationToken) throws ParseException {
        keycloakService.logout(authorizationToken);
    }

    public ClientResponse persistClient(final ClientRequest request) {
//        return clientService.persist(request);
        return null;
    }

    public Client findClientById(final UUID id) {
        return clientService.findById(id);
    }

    public Bitcoin quoteBitcoin() {
        return transactionService.quoteBitcoin();
    }

    public Transaction persistTransaction(final TransactionRequest request, final TransactionType type) {
        return transactionService.persist(request, type);
    }

    public List<Transaction> findAllTransactions(final Client client) {
        return transactionService.findAllTransactions(client);
    }

    public Transaction findTransactionById(final UUID transactionId) {
        return transactionService.findTransactionById(transactionId);
    }

    public Transaction findTransactionByQuantity(final UUID clientId, final BigDecimal quantity, final Sort.Direction sortCreatedAt) {
        return transactionService.findTransactionByQuantity(clientId, quantity, sortCreatedAt);
    }

    public List<TransactionSummaryByTypeResponse> findTransactionsSummaryByType(final UUID clientId, final List<TransactionType> types) {
        return transactionService.findTransactionsSummaryByType(clientId, types);
    }

    public List<TransactionSummaryByFiltersResponse> findTransactionSummaryByFilters(final UUID clientId, final TransactionFiltersRequest request) {
        return transactionService.findTransactionSummaryByFilters(clientId, request);
    }

    public TransactionReportResponse findTransactionReport(final UUID clientId, final TransactionReportPeriod period) {
        return transactionService.findTransactionReport(clientId, period);
    }

    public void setBitcoinAttributesInResponse(final TransactionReportResponse reportResponse, final Bitcoin bitcoin) {
        transactionService.setBitcoinAttributesInResponse(reportResponse, bitcoin);
    }

    public void validateBitcoinQuantity(final List<Transaction> transactions, final BigDecimal quantity) {
        transactionService.validateQuantity(transactions, quantity);
    }

    public TransactionResponse getMappedTransaction(final Transaction transaction, final Bitcoin bitcoin) {
        return transactionService.getMappedTransaction(transaction, bitcoin);
    }
}
