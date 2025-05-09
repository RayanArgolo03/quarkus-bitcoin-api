package dev.rayan.services;

import dev.rayan.dto.request.page.DatePeriodRequest;
import dev.rayan.dto.request.page.PaginationRequest;
import dev.rayan.dto.request.transaction.TransactionByTypeRequest;
import dev.rayan.dto.request.transaction.TransactionFiltersRequest;
import dev.rayan.dto.request.transaction.TransactionReportRequest;
import dev.rayan.dto.request.transaction.TransactionRequest;
import dev.rayan.dto.response.page.PageResponse;
import dev.rayan.dto.response.transaction.*;
import dev.rayan.enums.TransactionReportPeriod;
import dev.rayan.enums.TransactionType;
import dev.rayan.exceptions.BusinessException;
import dev.rayan.mappers.TransactionMapper;
import dev.rayan.mappers.TransactionMapperImpl;
import dev.rayan.model.Client;
import dev.rayan.model.Transaction;
import dev.rayan.repositories.TransactionRepository;
import dev.rayan.utils.NumberFormatUtils;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import io.quarkus.test.InjectMock;
import io.quarkus.test.component.QuarkusComponentTest;
import io.quarkus.test.component.SkipInject;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static io.quarkus.panache.common.Sort.Direction.Ascending;
import static jakarta.ws.rs.core.Response.Status.*;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
            bitcoin = new BitcoinResponse(new BigDecimal(10), LocalDateTime.now());
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
        @DisplayName("---- Should persist and return TransactionResponse when request is valid")
        void givenPersist_whenRequestIsValid_thenPersistAndReturnTransactionResponse() {

            final TransactionRequest request = new TransactionRequest(
                    new BigDecimal("1")
            );

            final Transaction expectedTransaction = Transaction.builder()
                    .client(client)
                    .quantity(request.quantity())
                    .type(type)
                    .build();

            final ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);

            final String bitcoinCurrentValue = NumberFormatUtils.formatNumber(bitcoin.price());
            final String quantity = request.quantity() + " unit(s)";

            final String transactionTotal = NumberFormatUtils.formatNumber(
                    bitcoin.price().multiply(request.quantity())
            );

            final TransactionResponse expectedResponse = TransactionResponse.builder()
                    .bitcoinCurrentValue(bitcoinCurrentValue)
                    .currentValueDate(bitcoin.quotedAt().toLocalDate())
                    .quantity(quantity)
                    .createdAt(expectedTransaction.getCreatedAt())
                    .type(type.getValue())
                    .transactionTotal(transactionTotal)
                    .build();

            try (MockedStatic<LocalDateTime> mockCreatedAt = mockStatic(LocalDateTime.class)) {

                //Used by mapper request to transaction in build()
                mockCreatedAt.when(LocalDateTime::now).thenReturn(expectedTransaction.getCreatedAt());
                doNothing().when(repository).persist(transactionCaptor.capture());

                final TransactionResponse response = service.persist(request, client, type, bitcoin);
                final Transaction capturedTransaction = transactionCaptor.getValue();

                assertEquals(expectedResponse, response);
                assertEquals(expectedTransaction, capturedTransaction);

                verify(repository).persist(capturedTransaction);
            }
        }
    }


    @Nested
    class ValidateTransactionTests {

        Client client;

        BigDecimal quantity;

        @BeforeEach
        void setUp() {
            client = new Client();
            quantity = new BigDecimal("3.5");
        }

        @Test
        @DisplayName("Should be throw ForbiddenException with status code 409 Forbidden when no has transactions made")
        void givenValidateTransaction_whenNoHasTransactions_thenThrowForbiddenException() {

            when(repository.findAllTransactions(client)).thenReturn(List.of());

            final ForbiddenException e = assertThrows(ForbiddenException.class,
                    () -> service.validateTransaction(client, quantity));

            final String expectedMessage = "You don´t have transactions made!";
            final Response.Status expectedStatusCode = FORBIDDEN;

            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedStatusCode, e.getResponse().getStatusInfo().toEnum());

            verify(repository).findAllTransactions(client);
        }

        @Test
        @DisplayName("Should be throw BusinessException with status code 400 Bad Request when no has available quantity")
        void givenValidateTransaction_whenNoHasAvailableQuantity_thenThrowBusinessException() {

            final BigDecimal totalSum = new BigDecimal(quantity.toString());

            final Transaction purchaseTransaction = Transaction.builder()
                    .type(TransactionType.PURCHASE)
                    .quantity(totalSum)
                    .build();

            final Transaction saleTransaction = Transaction.builder()
                    .type(TransactionType.SALE)
                    .quantity(totalSum)
                    .build();

            when(repository.findAllTransactions(client))
                    .thenReturn(List.of(purchaseTransaction, saleTransaction));

            final BusinessException e = assertThrows(BusinessException.class,
                    () -> service.validateTransaction(client, quantity));

            final String expectedMessage = "No has bitcoins to sale!";
            final Response.Status expectedStatusCode = BAD_REQUEST;

            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedStatusCode, e.getResponse().getStatusInfo().toEnum());

            verify(repository).findAllTransactions(client);
        }

        @Test
        @DisplayName("Should be throw BusinessException with status code 400 Bad Request when quantity is greater than available quantity")
        void givenValidateTransaction_whenIsInvalidQuantity_thenThrowBusinessException() {

            final BigDecimal totalSum = new BigDecimal(quantity.toString()).subtract(
                    new BigDecimal("1")
            );

            final Transaction transaction = Transaction.builder()
                    .type(TransactionType.PURCHASE)
                    .quantity(totalSum)
                    .build();

            when(repository.findAllTransactions(client))
                    .thenReturn(List.of(transaction));

            final BusinessException e = assertThrows(BusinessException.class,
                    () -> service.validateTransaction(client, quantity));

            final String expectedMessage = format(
                    "Quantity desired (%s) is greater than the available quantity (%s)!",
                    quantity,
                    totalSum
            );

            final Response.Status expectedStatusCode = BAD_REQUEST;

            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedStatusCode, e.getResponse().getStatusInfo().toEnum());

            verify(repository).findAllTransactions(client);
        }

        @Test
        @DisplayName("Should does nothing when quantity is equals to available quantity")
        void givenValidateTransaction_whenQuantityIsEqualsAvailableQuantity_thenDoesNothing() {

            final BigDecimal totalSum = new BigDecimal(quantity.toString());

            final Transaction transaction = Transaction.builder()
                    .type(TransactionType.PURCHASE)
                    .quantity(totalSum)
                    .build();

            when(repository.findAllTransactions(client))
                    .thenReturn(List.of(transaction));

            service.validateTransaction(client, quantity);

            verify(repository).findAllTransactions(client);
        }

        @Test
        @DisplayName("Should does nothing when quantity is less than to available quantity")
        void givenValidateTransaction_whenQuantityIsLessThanAvailableQuantity_thenDoesNothing() {

            final BigDecimal totalSum = new BigDecimal(quantity.toString()).add(
                    new BigDecimal("1")
            );

            final Transaction transaction = Transaction.builder()
                    .type(TransactionType.PURCHASE)
                    .quantity(totalSum)
                    .build();

            when(repository.findAllTransactions(client))
                    .thenReturn(List.of(transaction));

            service.validateTransaction(client, quantity);

            verify(repository).findAllTransactions(client);
        }
    }

    @Nested
    @DisplayName("---- TransactionByTypeRequest tests")
    class TransactionByTypeRequestTests {

        @Test
        @DisplayName("Should do nothing when no has types")
        void givenFindByTypes_whenNoHasTypes_thenDoNothing() {
            final TransactionByTypeRequest request = new TransactionByTypeRequest(null, null);
            assertTrue(
                    hibernateValidator.validate(request).isEmpty()
            );
        }

        @ParameterizedTest
        @MethodSource("getBlankTypes")
        @DisplayName("Must have (types size * annotations quantity) violations when the is blank type")
        void givenFindByTypes_whenIsBlankType_thenThrowConstraintViolationException(@SkipInject final List<String> types) {

            final TransactionByTypeRequest request = new TransactionByTypeRequest(types, null);

            final List<String> messages = hibernateValidator.validate(request)
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .toList();

            final List<String> expectedMessages = List.of(
                    "Required type!",
                    "Invalid transaction type!"
            );

            final int size = messages.size();

            //Expected size is: (quantity of types * the request annotation quantity); two, NotBlank and EnumValidator
            final int expectedSize = types.size() * expectedMessages.size();

            assertEquals(expectedSize, size);
            assertTrue(expectedMessages.containsAll(messages));
        }

        static Stream<Arguments> getBlankTypes() {
            final List<String> types = List.of(" ", "", "  ");
            return Stream.of(Arguments.of(types));
        }

        @ParameterizedTest
        @MethodSource("getInvalidTypes")
        @DisplayName("Must have types size violations when type is unlike of all TransactionType")
        void givenFindByTypes_whenIsInvalidType_thenThrowConstraintViolationException(@SkipInject final List<String> types) {

            final TransactionByTypeRequest request = new TransactionByTypeRequest(types, null);

            final Set<ConstraintViolation<TransactionByTypeRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .distinct()
                    .findFirst()
                    .get();

            final String expectedMessage = "Invalid transaction type!";

            final int size = violations.size();
            final int expectedSize = types.size();

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);
        }

        static Stream<Arguments> getInvalidTypes() {
            final List<String> invalidTypes = List.of(
                    "Purchasee", "SAlee", "SaLEE", "PURCHASEE", "salee",
                    "Buyy", "SELLL", "Trannsaction", "PURCHAS", "sall",
                    "Acquirre", "DISPOSAL", "TRADEEE", "PURCHHASE", "saless",
                    "Investt", "WITHDRAWAL", "EXCHANGEE", "PURCHASED", "salesss",
                    "Purchasess", "SELLER", "TRADING", "PURCHASEING", "salessss",
                    "Acquisitionn", "DISPOSALS", "TRADINGS", "PURCHASING", "salesssss",
                    "Investingg", "WITHDRAWALS", "EXCHANGES", "PURCHASESS", "salessssss",
                    "Purchasesss", "SELLERS", "TRADESS", "PURCHASEEEE", "salesssssss",
                    "Acquisitions", "DISPOSALL", "TRADEINGS", "PURCHASERR", "salessssssss"
            );
            return Stream.of(Arguments.of(invalidTypes));
        }

        @ParameterizedTest
        @MethodSource("getValidTypes")
        @DisplayName("Should do nothing when is valid type")
        void givenFindByTypes_whenRequestIsValid_thenDoNothing(@SkipInject final List<String> types) {
            final TransactionByTypeRequest request = new TransactionByTypeRequest(types, null);
            assertTrue(
                    hibernateValidator.validate(request).isEmpty()
            );
        }

        static Stream<Arguments> getValidTypes() {
            final List<String> types = List.of(
                    "PURCHASE", "purchase", "Purchase", "PURchase", "pURCHASE",
                    "puRCHASE", "PURCHase", "purchASe", "PURCHaSE", "pUrchase",
                    "PURCHasE", "pURchASE", "pUrCHASE", "PURCHaSe", "purchasE",
                    "SALE", "sale", "Sale", "sALE", "SaLE",
                    "sAle", "SALe", "sALe", "SalE", "saLe",
                    "salE", "SAlE", "sALe", "SaLE", "sAle",
                    "PURCHASE", "purchase", "Purchase", "PURCHASE", "purchase",
                    "PURCHASE", "purchase", "Purchase", "PURCHASE", "purchase",
                    "SALE", "sale", "Sale", "SALE", "sale",
                    "SALE", "sale", "Sale", "SALE", "sale"
            );
            return Stream.of(Arguments.of(types));
        }
    }

    @Nested
    @DisplayName("---- FindByTypes tests ----")
    class FindByTypesTests {

        TransactionByTypeRequest request;

        Client client;

        PanacheQuery<TransactionByTypeResponse> mockQuery;

        TransactionByTypeResponse purchaseResponse;

        TransactionByTypeResponse saleResponse;


        @BeforeEach
        void setUp() {
            request = new TransactionByTypeRequest(List.of("any"), Ascending);
            client = new Client();
            mockQuery = mock(PanacheQuery.class);

            purchaseResponse = TransactionByTypeResponse.builder()
                    .type(TransactionType.PURCHASE)
                    .transactionsMade(1L)
                    .totalQuantity(new BigDecimal("1"))
                    .firstTransactionDate(LocalDateTime.now())
                    .lastTransactionDate(LocalDateTime.now())
                    .periodBetweenFirstAndLast("0 day(s)")
                    .build();

            saleResponse = TransactionByTypeResponse.builder()
                    .type(TransactionType.SALE)
                    .transactionsMade(1L)
                    .totalQuantity(new BigDecimal("1"))
                    .firstTransactionDate(LocalDateTime.now())
                    .lastTransactionDate(LocalDateTime.now())
                    .periodBetweenFirstAndLast("0 day(s)")
                    .build();
        }

        @Test
        @DisplayName("Should be return PageResponse with all aggregation types when no has types")
        void givenFindByTypes_whenNoHasTypes_thenReturnPageResponseWithAllTypes() {

            final List<TransactionByTypeResponse> expectedElements = List.of(purchaseResponse, saleResponse);

            final int totalElements = expectedElements.size();

            final PageResponse expectedResponse = new PageResponse(
                    expectedElements,
                    null,
                    true,
                    totalElements,
                    1,
                    1
            );

            final List<TransactionType> transactionTypes = new ArrayList<>();

            when(repository.findTransactionsByTypes(request, client, transactionTypes))
                    .thenReturn(mockQuery);

            when(mockQuery.list())
                    .thenReturn(expectedElements);

            final PageResponse response = service.findByTypes(request, client, List.of());

            assertEquals(expectedResponse, response);

            verify(repository).findTransactionsByTypes(request, client, transactionTypes);
            verify(mockQuery).list();
        }

        @Test
        @DisplayName("Should be return PageResponse with purchase type when has purchase type")
        void givenFindByTypes_whenHasPurchaseType_thenReturnPageResponseWithPurchaseType() {

            final List<TransactionByTypeResponse> expectedElements = Collections.singletonList(purchaseResponse);

            final int totalElements = expectedElements.size();

            final PageResponse expectedResponse = new PageResponse(
                    expectedElements,
                    null,
                    true,
                    totalElements,
                    1,
                    1
            );

            final List<TransactionType> transactionTypes = Collections.singletonList(TransactionType.PURCHASE);

            when(repository.findTransactionsByTypes(request, client, transactionTypes))
                    .thenReturn(mockQuery);

            //Calling by PaginationUtils
            when(mockQuery.list())
                    .thenReturn(expectedElements);

            final PageResponse response = service.findByTypes(request, client, transactionTypes);

            assertEquals(expectedResponse, response);

            verify(repository).findTransactionsByTypes(request, client, transactionTypes);
            verify(mockQuery).list();
        }

        @Test
        @DisplayName("Should be return PageResponse with sale type when has sale type")
        void givenFindByTypes_whenHasSaleType_thenReturnPageResponseWithSaleType() {

            final List<TransactionByTypeResponse> expectedElements = Collections.singletonList(saleResponse);

            final int totalElements = expectedElements.size();

            final PageResponse expectedResponse = new PageResponse(
                    expectedElements,
                    null,
                    true,
                    totalElements,
                    1,
                    1
            );

            final List<TransactionType> transactionTypes = Collections.singletonList(TransactionType.SALE);

            when(repository.findTransactionsByTypes(request, client, transactionTypes))
                    .thenReturn(mockQuery);

            //Calling by PaginationUtils
            when(mockQuery.list())
                    .thenReturn(expectedElements);

            final PageResponse response = service.findByTypes(request, client, transactionTypes);

            assertEquals(expectedResponse, response);

            verify(repository).findTransactionsByTypes(request, client, transactionTypes);
            verify(mockQuery).list();
        }
    }

    @Nested
    @DisplayName("---- DatePeriodRequest tests ---- ")
    class DatePeriodRequestTests {

        @Test
        @DisplayName("Must have one violation when the start date is null")
        void givenDatePeriodRequest_whenStartDateIsNull_thewThrowConstraintViolationException() {

            final DatePeriodRequest request = new DatePeriodRequest(null, LocalDate.now());

            final Set<ConstraintViolation<DatePeriodRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Required start date!";

            final int size = 1,
                    expectedSize = violations.size();

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);
        }

        @Test
        @DisplayName("Must have one violation when the start date is after the current date")
        void givenDatePeriodRequest_whenStartDateIsAfterTheCurrentDate_thewThrowConstraintViolationException() {

            final LocalDate currentDate = LocalDate.now();

            final DatePeriodRequest request = new DatePeriodRequest(
                    currentDate.plusDays(1L),
                    currentDate
            );

            final Set<ConstraintViolation<DatePeriodRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "The start date can´t be after current date!";

            final int size = 1,
                    expectedSize = violations.size();

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);
        }


        @Test
        @DisplayName("Must have one violation when the end date is null")
        void givenDatePeriodRequest_whenEndDateIsNull_thewThrowConstraintViolationException() {

            final DatePeriodRequest request = new DatePeriodRequest(LocalDate.now(), null);

            final Set<ConstraintViolation<DatePeriodRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Required end date!";

            final int size = 1,
                    expectedSize = violations.size();

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);
        }

        @Test
        @DisplayName("Must have one violation when the start date is after the current date")
        void givenDatePeriodRequest_whenEndDateIsAfterTheCurrentDate_thewThrowConstraintViolationException() {

            final LocalDate currentDate = LocalDate.now();

            final DatePeriodRequest request = new DatePeriodRequest(
                    currentDate,
                    currentDate.plusDays(1L)
            );

            final Set<ConstraintViolation<DatePeriodRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "The end date can´t be after current date!";

            final int size = 1,
                    expectedSize = violations.size();

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);
        }

    }

    @Nested
    @DisplayName("---- PaginationRequest tests ----")
    class PaginationRequestTests {

        @Test
        @DisplayName("Must have one violation when the page number is less than 1")
        void givenPaginationRequest_whenPageNumberIsLessThan1_thenThrowConstraintViolationException() {

            final PaginationRequest request = new PaginationRequest(-1, 1);

            final Set<ConstraintViolation<PaginationRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Page number can´t be less than 1!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);
        }

        @Test
        @DisplayName("Must have one violation when the page size is less than 1")
        void givenPaginationRequest_whenPageSizeIsLessThan1_thenThrowConstraintViolationException() {

            final PaginationRequest request = new PaginationRequest(1, -1);

            final Set<ConstraintViolation<PaginationRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Page size can´t be less than 1!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);
        }

        @Test
        @DisplayName("Must have one violation when the page size is greater than 100")
        void givenPaginationRequest_whenPageSizeIsGreaterThan100_thenThrowConstraintViolationException() {

            final PaginationRequest request = new PaginationRequest(1, 101);

            final Set<ConstraintViolation<PaginationRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Page size can´t be greater than 100!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);
        }

        @Test
        @DisplayName("Should do nothing when request is valid")
        void givenPaginationRequest_RequestIsValid_thenDoNothing() {
            final PaginationRequest request = new PaginationRequest(1, 1);
            assertTrue(
                    hibernateValidator.validate(request).isEmpty()
            );
        }
    }

    @Nested
    @DisplayName("---- TransactionFiltersRequest tests ----")
    class TransactionFiltersRequestTests {

        DatePeriodRequest datePeriod;

        PaginationRequest paginationRequest;

        Sort.Direction sortCreatedAt;

        Sort.Direction sortQuantity;

        Sort.Direction sortType;

        @BeforeEach
        void setUp() {
            datePeriod = new DatePeriodRequest(LocalDate.now(), LocalDate.now());
            paginationRequest = new PaginationRequest(1, 1);
            sortCreatedAt = Ascending;
            sortQuantity = Ascending;
            sortType = Ascending;
        }

        @Test
        @DisplayName("Must have one violation when min quantity is zero")
        void givenFindByFilters_whenMinQuantityIsZero_thenThrowConstraintViolationException() {

            final BigDecimal minQuantity = BigDecimal.ZERO,
                    maxQuantity = BigDecimal.ONE;

            final TransactionFiltersRequest request = TransactionFiltersRequest.builder()
                    .datePeriod(datePeriod)
                    .paginationRequest(paginationRequest)
                    .minQuantity(minQuantity)
                    .maxQuantity(maxQuantity)
                    .sortCreatedAt(sortCreatedAt)
                    .sortQuantity(sortQuantity)
                    .sortType(sortType)
                    .build();

            final Set<ConstraintViolation<TransactionFiltersRequest>> violations = hibernateValidator.validate(request);


            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Min quantity should be more than 0!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);
        }

        @Test
        @DisplayName("Must have one violation when min quantity is negative")
        void givenFindByFilters_whenMinQuantityIsNegative_thenThrowConstraintViolationException() {

            final BigDecimal minQuantity = new BigDecimal("-1"),
                    maxQuantity = BigDecimal.ONE;

            final TransactionFiltersRequest request = TransactionFiltersRequest.builder()
                    .datePeriod(datePeriod)
                    .paginationRequest(paginationRequest)
                    .minQuantity(minQuantity)
                    .maxQuantity(maxQuantity)
                    .sortCreatedAt(sortCreatedAt)
                    .sortQuantity(sortQuantity)
                    .sortType(sortType)
                    .build();

            final Set<ConstraintViolation<TransactionFiltersRequest>> violations = hibernateValidator.validate(request);


            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Min quantity should be more than 0!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);
        }

        @Test
        @DisplayName("Must have one violation when max quantity is negative")
        void givenFindByFilters_whenMaxQuantityIsNegative_thenThrowConstraintViolationException() {

            final BigDecimal maxQuantity = new BigDecimal("-1"),
                    minQuantity = BigDecimal.ONE;

            final TransactionFiltersRequest request = TransactionFiltersRequest.builder()
                    .datePeriod(datePeriod)
                    .paginationRequest(paginationRequest)
                    .minQuantity(minQuantity)
                    .maxQuantity(maxQuantity)
                    .sortCreatedAt(sortCreatedAt)
                    .sortQuantity(sortQuantity)
                    .sortType(sortType)
                    .build();

            final Set<ConstraintViolation<TransactionFiltersRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Max quantity should be more than 0!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);
        }
    }

    @Nested
    @DisplayName("---- FindByFilters tests ----")
    class FindByFiltersTests {

        Client client;

        @BeforeEach
        void setUp() {
            client = new Client();
        }

        @Test
        @DisplayName("Should be return empty page response when no has transactions by filters")
        void givenFindByFilters_whenNoHasTransactionsByFilters_thenReturnEmptyPageResponse() {

            final TransactionFiltersRequest request = TransactionFiltersRequest.builder()
                    .datePeriod(new DatePeriodRequest(LocalDate.now(), LocalDate.now()))
                    .paginationRequest(new PaginationRequest(3, 4))
                    .minQuantity(new BigDecimal("1"))
                    .maxQuantity(new BigDecimal("1"))
                    .sortCreatedAt(Ascending)
                    .sortQuantity(Ascending)
                    .sortType(Ascending)
                    .build();

            final PanacheQuery<TransactionByFilterResponse> mockQuery = mock(PanacheQuery.class);

            final List<TransactionByFilterResponse> expectedElements = new ArrayList<>();

            final PageResponse expectedResponse = new PageResponse(
                    expectedElements,
                    null,
                    false,
                    0,
                    0,
                    0
            );

            when(repository.findTransactionsByFilters(request, client))
                    .thenReturn(mockQuery);

            when(mockQuery.count())
                    .thenReturn(0L);

            when(mockQuery.list())
                    .thenReturn(expectedElements);

            final PageResponse response = service.findByFilters(request, client);

            assertEquals(expectedResponse, response);

            verify(repository).findTransactionsByFilters(request, client);
            verify(mockQuery).count();
            verify(mockQuery).list();
        }

        @Test
        @DisplayName("Should be return page response when has transactions by filters and pagination (page number and size) is valid")
        void givenFindByFilters_whenHasTransactionsByFiltersAndPaginationIsValid_thenReturnPageResponse() {

            final BigDecimal quantity = new BigDecimal("1");
            final Sort.Direction sort = Ascending;
            final LocalDate date = LocalDate.now();

            final int pageNumber = 4;
            final int pageSize = 3;

            final TransactionFiltersRequest request = TransactionFiltersRequest.builder()
                    .datePeriod(new DatePeriodRequest(date, date))
                    .paginationRequest(new PaginationRequest(pageNumber, pageSize))
                    .minQuantity(quantity)
                    .maxQuantity(quantity)
                    .sortCreatedAt(sort)
                    .sortQuantity(sort)
                    .sortType(sort)
                    .build();

            final PanacheQuery<TransactionByFilterResponse> mockQuery = mock(PanacheQuery.class);

            final int expectedTotalElements = 23;

            final int expectedTotalPages = (int) Math.ceil(
                    (double) expectedTotalElements / (double) pageSize
            );

            final int expectedTotalElementsInPage = Math.min(
                    (expectedTotalElements - (pageNumber - 1) * pageSize),
                    pageSize
            );

            final List<TransactionByFilterResponse> expectedElements = Collections.nCopies(
                    expectedTotalElementsInPage,
                    new TransactionByFilterResponse(null, null, null)
            );

            final PaginationRequest pagination = request.paginationRequest();

            final PageResponse expectedResponse = new PageResponse(
                    expectedElements,
                    pagination,
                    true,
                    expectedTotalElements,
                    pageNumber,
                    expectedTotalPages
            );

            when(repository.findTransactionsByFilters(request, client))
                    .thenReturn(mockQuery);

            when(mockQuery.count())
                    .thenReturn((long) expectedTotalElements);

            when(mockQuery.page(pagination.getPage()))
                    .thenReturn(mockQuery);

            when(mockQuery.pageCount())
                    .thenReturn(expectedTotalPages);

            when(mockQuery.page())
                    .thenReturn(Page.of(pageNumber - 1, pageSize));

            when(mockQuery.list())
                    .thenReturn(expectedElements);

            final PageResponse response = service.findByFilters(request, client);

            assertEquals(expectedResponse, response);

            verify(repository).findTransactionsByFilters(request, client);
            verify(mockQuery).count();
            verify(mockQuery).page(any());
            verify(mockQuery).pageCount();
            verify(mockQuery).page();
            verify(mockQuery).list();
        }

        @Test
        @DisplayName("Should be return page response in the last page when has transactions by filters and pagination (page number and size) is invalid")
        void givenFindByFilters_whenHasTransactionsByFiltersAndPaginationIsInvalid_thenReturnLastPageResponse() {

            final BigDecimal quantity = BigDecimal.ONE;
            final Sort.Direction sort = Ascending;
            final LocalDate date = LocalDate.now();

            final int pageNumber = 4,
                    pageSize = 9,
                    expectedTotalElements = 8;

            final TransactionFiltersRequest request = TransactionFiltersRequest.builder()
                    .datePeriod(new DatePeriodRequest(date, date))
                    .paginationRequest(new PaginationRequest(pageNumber, pageSize))
                    .minQuantity(quantity)
                    .maxQuantity(quantity)
                    .sortCreatedAt(sort)
                    .sortQuantity(sort)
                    .sortType(sort)
                    .build();

            final PanacheQuery<TransactionByFilterResponse> mockQuery = mock(PanacheQuery.class);

            final int expectedTotalPages = (int) Math.ceil(
                    (double) expectedTotalElements / (double) pageSize
            );

            final int expectedTotalElementsInPage = (expectedTotalElements - (expectedTotalPages - 1) * pageSize);

            final List<TransactionByFilterResponse> expectedElements = Collections.nCopies(
                    expectedTotalElementsInPage, new TransactionByFilterResponse(null, null, null)
            );

            final PageResponse expectedResponse = new PageResponse(
                    expectedElements,
                    request.paginationRequest(),
                    true,
                    expectedTotalElements,
                    pageNumber,
                    expectedTotalPages
            );

            when(repository.findTransactionsByFilters(request, client))
                    .thenReturn(mockQuery);

            when(mockQuery.count())
                    .thenReturn((long) expectedTotalElements);

            when(mockQuery.page(any()))
                    .thenReturn(mockQuery);

            when(mockQuery.pageCount())
                    .thenReturn(expectedTotalPages);

            when(mockQuery.lastPage())
                    .thenReturn(mockQuery);

            when(mockQuery.page())
                    .thenReturn(Page.of(pageNumber - 1, pageSize));

            when(mockQuery.list())
                    .thenReturn(expectedElements);

            final PageResponse response = service.findByFilters(request, client);

            assertEquals(expectedResponse, response);

            verify(repository).findTransactionsByFilters(request, client);
            verify(mockQuery).count();
            verify(mockQuery).page(any());
            verify(mockQuery).pageCount();
            verify(mockQuery).lastPage();
            verify(mockQuery).page();
            verify(mockQuery).list();
        }
    }

    @Nested
    @DisplayName("---- FindById tests ----")
    class FindByIdTests {

        String transactionId;

        BitcoinResponse bitcoin;

        @BeforeEach
        void setUp() {
            transactionId = "8c878e6f-ff13-4a37-a208-7510c2638944";
            bitcoin = new BitcoinResponse(BigDecimal.ONE, LocalDateTime.now());
        }

        @Test
        @DisplayName("Should be return mapped TransactionResponse when transaction is found")
        void givenFindById_whenTransactionIsFound_thenReturnMappedTransactionResponse() {

            final UUID id = UUID.fromString(transactionId);

            final Transaction transactionFound = Transaction.builder()
                    .id(id)
                    .quantity(BigDecimal.ONE)
                    .type(TransactionType.PURCHASE)
                    .build();

            final String transactionTotal = NumberFormatUtils.formatNumber(
                    transactionFound.getQuantity().multiply(bitcoin.price())
            );

            final TransactionResponse expectedResponse = TransactionResponse.builder()
                    .id(id)
                    .bitcoinCurrentValue(bitcoin.getPriceFormatted())
                    .currentValueDate(bitcoin.quotedAt().toLocalDate())
                    .quantity(transactionFound.getQuantity() + " unit(s)")
                    .createdAt(transactionFound.getCreatedAt())
                    .type(transactionFound.getType().getValue())
                    .transactionTotal(transactionTotal)
                    .build();

            when(repository.findByIdOptional(id))
                    .thenReturn(Optional.of(transactionFound));

            assertEquals(expectedResponse, service.findById(transactionId, bitcoin));

            verify(repository).findByIdOptional(id);
        }

        @Test
        @DisplayName("Should be throw NotFoundException with status code 404 NotFound when transaction not found")
        void givenFindById_whenTransactionNotFound_thenThrowNotFoundException() {

            final UUID id = UUID.fromString(transactionId);

            when(repository.findByIdOptional(id))
                    .thenReturn(Optional.empty());

            final NotFoundException e = assertThrows(NotFoundException.class,
                    () -> service.findById(transactionId, bitcoin));

            final String expectedMessage = "Transaction not found!";
            final Response.Status expectedStatus = NOT_FOUND;

            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedStatus, e.getResponse().getStatusInfo().toEnum());

            verify(repository).findByIdOptional(id);
        }
    }

    @Nested
    @DisplayName("---- TransactionReportRequest tests ----")
    class TransactionReportRequestTests {

        @ParameterizedTest
        @ValueSource(strings = {"a", "  ", "   ", "", "LAst", "TODAY",})
        @DisplayName("Must have one violation when the period is invalid")
        void givenTransactionReportRequest_whenIsInvalidPeriod_thenThrowConstraintViolationException(@SkipInject final String period) {

            final TransactionReportRequest request = new TransactionReportRequest(period, "Excel");

            final Set<ConstraintViolation<TransactionReportRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Invalid period!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);

        }

        @ParameterizedTest
        @ValueSource(strings = {"a", "  ", "   ", "", "LAst", "TODAY",})
        @DisplayName("Must have one violation when the format is invalid")
        void givenTransactionReportRequest_whenIsInvalidFormat_thenThrowConstraintViolationException(@SkipInject final String format) {

            final TransactionReportRequest request = new TransactionReportRequest("Last Year", format);

            final Set<ConstraintViolation<TransactionReportRequest>> violations = hibernateValidator.validate(request);

            final String message = violations.iterator().next().getMessage(),
                    expectedMessage = "Invalid format!";

            final int size = violations.size(),
                    expectedSize = 1;

            assertEquals(expectedSize, size);
            assertEquals(expectedMessage, message);

        }
    }

    @Nested
    @DisplayName(" ---- FindReport tests ----")
    class FindReportTests {

        TransactionReportPeriod period;

        Client client;

        @BeforeEach
        void setUp() {
            period = TransactionReportPeriod.LAST_YEAR;
            client = new Client();
        }

        @Test
        @DisplayName("Should be throw ForbiddenException with status code 403 Forbidden when no has transactions in the period")
        void givenFindReport_whenNoHasTransactionsInThePeriod_thenThrowForbiddenException() {

            when(repository.findTransactionCount(period, client))
                    .thenReturn(new TransactionCountResponse(0L));

            final ForbiddenException e = assertThrows(ForbiddenException.class,
                    () -> service.findReport(period, client));

            final String expectedMessage = format("No has transactions in the %s period", period.getValue());
            final Response.Status expectedStatusCode = FORBIDDEN;

            assertEquals(expectedMessage, e.getMessage());
            assertEquals(expectedStatusCode, e.getResponse().getStatusInfo().toEnum());

            verify(repository).findTransactionCount(period, client);
            verifyNoMoreInteractions(repository);
        }

        @Test
        @DisplayName("Should be return TransactionReportResponse when has transactions in the period")
        void givenFindReport_whenHasTransactionsInThePeriod_thenReturnTransactionReportResponse() {

            final TransactionReportResponse expectedResponse = TransactionReportResponse.builder()
                    .build();

            when(repository.findTransactionCount(period, client))
                    .thenReturn(new TransactionCountResponse(1L));

            when(repository.findTransactionReport(period, client))
                    .thenReturn(expectedResponse);

            assertEquals(expectedResponse, service.findReport(period, client));

            verify(repository).findTransactionCount(period, client);
            verify(repository).findTransactionReport(period, client);
        }

    }

}