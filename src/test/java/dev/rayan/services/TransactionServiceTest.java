package dev.rayan.services;

import dev.rayan.dto.request.transaction.TransactionRequest;
import dev.rayan.dto.response.transaction.BitcoinResponse;
import dev.rayan.dto.response.transaction.TransactionResponse;
import dev.rayan.enums.TransactionType;
import dev.rayan.mappers.TransactionMapper;
import dev.rayan.mappers.TransactionMapperImpl;
import dev.rayan.model.Client;
import dev.rayan.model.Transaction;
import dev.rayan.repositories.TransactionRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.component.QuarkusComponentTest;
import io.quarkus.test.component.SkipInject;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;

@QuarkusComponentTest(value = TransactionMapperImpl.class)
@DisplayName("---- TransactionService tests ----")
class TransactionServiceTest {

    @Inject
    TransactionService service;

    @Inject
    TransactionMapper mapper;

    @InjectMock
    TransactionRepository repository;

    Validator hibernateValidator = Validation.buildDefaultValidatorFactory().getValidator();

    @Nested
    @DisplayName("---- Persist tests ----")
    class PersistTests {

        Client client;

        TransactionType type;

        BitcoinResponse bitcoin;

        @BeforeEach
        void setUp() {
            client = new Client();
            type = TransactionType.PURCHASE;
            bitcoin = new BitcoinResponse(null, null);
        }

        @Test
        @DisplayName("Must have one violation when quantity is null")
        void givenPersist_whenQuantityIsNull_thenThrowConstraintViolationException() {

            final TransactionRequest request = new TransactionRequest(null);

            final Set<ConstraintViolation<TransactionRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Quantity required!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);
        }

        @ParameterizedTest
        @ValueSource(strings = {"0", "-1", "0.0000001"})
        @DisplayName("Must have one violation when quantity is less than decimal min")
        void givenPersist_whenQuantityLessThanDecimalMin_thenThrowConstraintViolationException(@SkipInject final BigDecimal quantity) {

            final TransactionRequest request = new TransactionRequest(quantity);

            final Set<ConstraintViolation<TransactionRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Quantity must be greater than 0.000001!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);
        }

        @ParameterizedTest
        @ValueSource(strings = {"1", "10", "0.000002"})
        @DisplayName("Should does nothing when request is valid")
        void givenPersist_whenRequestIsValid_thenDoesNothing(@SkipInject final BigDecimal quantity) {
            final TransactionRequest request = new TransactionRequest(quantity);
            assertEquals(0, hibernateValidator.validate(request).size());
        }

        @Test
        void givenPersist_whenRequestIsValid_thenPersistAndReturnTransactionResponse() {

            final TransactionRequest request = new TransactionRequest(
                    new BigDecimal(1)
            );

            final Transaction transaction = Transaction.builder()
                    .id(UUID.randomUUID())
                    .quantity(request.quantity())
                    .client(client)
                    .type(type)
                    .build();

            doNothing().when(repository).persist(transaction);

            final TransactionResponse response = service.persist(request, client, type, bitcoin);
        }
    }

}