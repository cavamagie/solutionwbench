/**
 * GiftcardController - REST API Controller
 * 
 * REST API controller for managing gift cards.
 * Provides endpoints for creating, querying, redeeming, and refunding gift cards.
 * 
 * Base path: /api/v1/giftcards
 * 
 * Endpoints:
 * - POST /giftcards - Create a new gift card
 * - GET /giftcards/{giftCardId}/balance - Get gift card balance
 * - GET /giftcards/{giftCardId}/transactions - Get gift card transactions
 * - GET /giftcards/{customerId}/giftcards - Get all gift cards for a customer
 * - POST /giftcards/{giftCardId}/redeem - Redeem gift card at checkout
 * - POST /giftcards/{giftCardId}/refund - Refund gift card redemption
 */
package k5.giftcard.api.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import k5.giftcard.api.dto.CreateGiftcardRequest;
import k5.giftcard.api.dto.ErrorResponse;
import k5.giftcard.api.dto.GiftcardBalanceResponse;
import k5.giftcard.api.dto.GiftcardResponse;
import k5.giftcard.api.dto.RedeemFromGiftcardRequest;
import k5.giftcard.api.dto.RefundToGiftcardRequest;
import k5.giftcard.api.dto.TransactionResponse;
import k5.giftcard.api.mapper.GiftcardMapper;
import k5.giftcard.domain.giftcard.command.ActivateCommand;
import k5.giftcard.domain.giftcard.command.RedeemCommand;
import k5.giftcard.domain.giftcard.command.RefundCommand;
import k5.giftcard.domain.giftcard.entity.Giftcard;
import k5.giftcard.domain.giftcard.event.GiftCardActivated;
import k5.giftcard.domain.giftcard.event.publisher.GiftCardActivatedPublisher;
import k5.giftcard.domain.giftcard.repository.GiftcardRepository;
import k5.giftcard.domain.giftcard.service.GetGiftcardsForCustomerService;

@RestController
@RequestMapping("/api/v1/giftcards")
@Tag(name = "Gift Card API", description = "REST API for managing gift cards")
public class GiftcardController {
    
    private static final Logger logger = LoggerFactory.getLogger(GiftcardController.class);
    
    private final BeanFactory beanFactory;
    private final GiftcardRepository giftcardRepository;
    private final GiftCardActivatedPublisher giftCardActivatedPublisher;
    
    /**
     * Constructor with dependency injection
     *
     * @param beanFactory Spring BeanFactory for creating command instances
     * @param giftcardRepository Repository for gift card persistence operations
     * @param giftCardActivatedPublisher publisher used to emit gift card activation events
     */
    public GiftcardController(
            BeanFactory beanFactory,
            GiftcardRepository giftcardRepository,
            GiftCardActivatedPublisher giftCardActivatedPublisher) {
        this.beanFactory = beanFactory;
        this.giftcardRepository = giftcardRepository;
        this.giftCardActivatedPublisher = giftCardActivatedPublisher;
    }
    
    /**
     * Creates a new gift card
     * 
     * This endpoint creates and activates a new gift card with the specified balance,
     * currency code, and customer identifier. It triggers the ActivateCommand to create
     * the gift card instance and persist it to the database.
     * 
     * @param request The request body containing gift card creation details
     * @return ResponseEntity with the created gift card information and HTTP 201 status
     */
    @PostMapping
    @Operation(
        summary = "Create a new gift card",
        description = "Creates a new gift card incl. balance, customer reference etc."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Gift card successfully created / activated",
            content = @Content(schema = @Schema(implementation = GiftcardResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request - Invalid input data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<GiftcardResponse> createGiftcard(
            @Valid @RequestBody CreateGiftcardRequest request) {
        
        logger.debug("Received POST request to /api/v1/giftcards with body: {}", request);
        
        // Create and execute the ActivateCommand using BeanFactory
        // The ActivateCommand.execute() method creates a new Giftcard instance with the provided parameters
        // Input: customerId, balance, currencyCode
        // Output: Giftcard entity with generated ID and ACTIVATE transaction
        ActivateCommand command = beanFactory.getBean(ActivateCommand.class);
        command.withCustomerId(request.getCustomerId())
               .withBalance(request.getBalance())
               .withCurrencyCode(request.getCurrencyCode());
        
        Giftcard giftcard = command.execute();
        
        // Persist the gift card to the database
        giftcard = giftcardRepository.save(giftcard);

        GiftCardActivated giftCardActivatedEvent = new GiftCardActivated()
                .setGiftcardId(giftcard.getGiftcardId())
                .setCustomerId(giftcard.getCustomerId())
                .setActivatedAt(giftcard.getTransactions().getFirst().getExecutionTimestamp())
                .setBalance(giftcard.getBalance())
                .setCurrencyCode(giftcard.getCurrencyCode());

        // Publish the activation event after persistence succeeds.
        giftCardActivatedPublisher.publish(giftCardActivatedEvent);
        
        logger.info("POST request to /api/v1/giftcards completed with status 201. Created giftcard: {}",
                   giftcard.getGiftcardId());
        
        // Map the domain entity to response DTO
        GiftcardResponse response = GiftcardMapper.toGiftcardResponse(giftcard);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Gets the balance of a specific gift card
     * 
     * This endpoint retrieves the current balance information for a gift card
     * identified by its ID.
     * 
     * @param giftCardId The unique identifier of the gift card
     * @return ResponseEntity with the gift card balance and HTTP 200 status
     */
    @GetMapping("/{giftCardId}/balance")
    @Operation(
        summary = "Get gift card balance",
        description = "Provides the balance for particular gift card."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Giftcard balance successfully retrieved",
            content = @Content(schema = @Schema(implementation = GiftcardBalanceResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Gift card not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<GiftcardBalanceResponse> getGiftcardBalance(
            @Parameter(description = "Gift card identifier", required = true)
            @PathVariable String giftCardId) {
        
        logger.debug("Received GET request to /api/v1/giftcards/{}/balance", giftCardId);
        
        // Retrieve the gift card from the database
        Giftcard giftcard = giftcardRepository.findById(giftCardId)
                .orElseThrow(() -> {
                    logger.warn("GET request to /api/v1/giftcards/{}/balance resulted in 404: Gift card not found", giftCardId);
                    return new GiftcardNotFoundException("Gift card not found with id: " + giftCardId);
                });
        
        logger.info("GET request to /api/v1/giftcards/{}/balance completed with status 200", giftCardId);
        
        // Map the domain entity to response DTO
        GiftcardBalanceResponse response = GiftcardMapper.toGiftcardBalanceResponse(giftcard);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Gets all transactions for a specific gift card
     * 
     * This endpoint retrieves the complete transaction history for a gift card
     * identified by its ID.
     * 
     * @param giftCardId The unique identifier of the gift card
     * @return ResponseEntity with the list of transactions and HTTP 200 status
     */
    @GetMapping("/{giftCardId}/transactions")
    @Operation(
        summary = "Get gift card payment transactions",
        description = "Provides the transactions for a particular gift card."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Transactions for Giftcard successfully retrieved",
            content = @Content(schema = @Schema(implementation = TransactionResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Gift card not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<List<TransactionResponse>> getGiftcardTransactions(
            @Parameter(description = "Gift card identifier", required = true)
            @PathVariable String giftCardId) {
        
        logger.debug("Received GET request to /api/v1/giftcards/{}/transactions", giftCardId);
        
        // Retrieve the gift card from the database
        Giftcard giftcard = giftcardRepository.findById(giftCardId)
                .orElseThrow(() -> {
                    logger.warn("GET request to /api/v1/giftcards/{}/transactions resulted in 404: Gift card not found", giftCardId);
                    return new GiftcardNotFoundException("Gift card not found with id: " + giftCardId);
                });
        
        logger.info("GET request to /api/v1/giftcards/{}/transactions completed with status 200", giftCardId);
        
        // Map the transactions to response DTOs
        List<TransactionResponse> response = GiftcardMapper.toTransactionResponseList(giftcard.getTransactions());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Gets all gift cards for a specific customer
     * 
     * This endpoint retrieves all gift cards owned by a customer identified by
     * their customer ID. It triggers the GetGiftcardsForCustomerService domain service.
     * 
     * @param customerId The unique identifier of the customer
     * @return ResponseEntity with the list of gift cards and HTTP 200 status
     */
    @GetMapping("/{customerId}/giftcards")
    @Operation(
        summary = "Get giftcards for customer",
        description = "Provides all giftcards for a customer."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Giftcards successfully retrieved",
            content = @Content(schema = @Schema(implementation = GiftcardResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<List<GiftcardResponse>> getGiftcardsForCustomer(
            @Parameter(description = "Customer identifier", required = true)
            @PathVariable String customerId) {
        
        logger.debug("Received GET request to /api/v1/giftcards/{}/giftcards", customerId);
        
        // Call the domain service to retrieve gift cards for the customer
        // The GetGiftcardsForCustomerService.execute() method queries the repository
        // Input: customerId
        // Output: List of GiftcardInfo objects containing essential gift card information
        GetGiftcardsForCustomerService service = beanFactory.getBean(GetGiftcardsForCustomerService.class);
        
        List<GetGiftcardsForCustomerService.GiftcardInfo> giftcardInfos = service.execute(customerId);
        
        // Convert GiftcardInfo to full Giftcard entities for mapping
        // In a real scenario, we might want to create a separate mapper or use GiftcardInfo directly
        List<Giftcard> giftcards = giftcardInfos.stream()
                .map(info -> {
                    Giftcard gc = new Giftcard();
                    gc.setGiftcardId(info.getGiftcardId());
                    gc.setBalance(info.getBalance());
                    gc.setCurrencyCode(info.getCurrencyCode());
                    gc.setCustomerId(customerId);
                    return gc;
                })
                .collect(java.util.stream.Collectors.toList());
        
        logger.info("GET request to /api/v1/giftcards/{}/giftcards completed with status 200. Found {} giftcards", 
                   customerId, giftcards.size());
        
        // Map the domain entities to response DTOs
        List<GiftcardResponse> response = GiftcardMapper.toGiftcardResponseList(giftcards);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Redeems an amount from a gift card
     * 
     * This endpoint processes a redemption by subtracting the specified amount from
     * the gift card balance. It triggers the RedeemCommand to validate the balance
     * and create a SUBTRACT transaction.
     * 
     * @param giftCardId The unique identifier of the gift card
     * @param request The request body containing redemption details
     * @return ResponseEntity with the updated gift card balance and HTTP 200 status
     */
    @PostMapping("/{giftCardId}/redeem")
    @Operation(
        summary = "Redeem gift card at checkout",
        description = "Redeems gift card for paying an order."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "The gift card has been successfully redeemed with the desired amount",
            content = @Content(schema = @Schema(implementation = GiftcardBalanceResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request - Insufficient balance or invalid input",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Gift card not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<GiftcardBalanceResponse> redeemGiftcard(
            @Parameter(description = "Gift card identifier", required = true)
            @PathVariable String giftCardId,
            @Valid @RequestBody RedeemFromGiftcardRequest request) {
        
        logger.debug("Received POST request to /api/v1/giftcards/{}/redeem with body: {}", giftCardId, request);
        
        // Retrieve the gift card from the database
        Giftcard giftcard = giftcardRepository.findById(giftCardId)
                .orElseThrow(() -> {
                    logger.warn("POST request to /api/v1/giftcards/{}/redeem resulted in 404: Gift card not found", giftCardId);
                    return new GiftcardNotFoundException("Gift card not found with id: " + giftCardId);
                });
        
        // Create and execute the RedeemCommand using BeanFactory
        // The RedeemCommand.execute() method validates balance and creates a SUBTRACT transaction
        // Input: giftcard, orderReference, amount, currencyCode
        // Output: boolean (true if successful)
        // Throws: BalanceNotSufficientException if balance is insufficient
        RedeemCommand command = beanFactory.getBean(RedeemCommand.class);
        command.withGiftcard(giftcard)
               .withOrderReference(request.getOrderReference())
               .withAmount(request.getAmount())
               .withCurrencyCode(request.getCurrencyCode());
        
        command.execute();
        
        // Persist the updated gift card to the database
        giftcard = giftcardRepository.save(giftcard);
        
        logger.info("POST request to /api/v1/giftcards/{}/redeem completed with status 200", giftCardId);
        
        // Map the domain entity to response DTO
        GiftcardBalanceResponse response = GiftcardMapper.toGiftcardBalanceResponse(giftcard);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Refunds an amount back to a gift card
     * 
     * This endpoint processes a refund by adding the specified amount back to the
     * gift card balance. It triggers the RefundCommand to validate the original
     * transaction and create a PLUS transaction.
     * 
     * @param giftCardId The unique identifier of the gift card
     * @param request The request body containing refund details
     * @return ResponseEntity with the updated gift card balance and HTTP 200 status
     */
    @PostMapping("/{giftCardId}/refund")
    @Operation(
        summary = "Refund giftcard",
        description = "Operation refunds amount which was before redeemed."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Giftcard refund successfully processed",
            content = @Content(schema = @Schema(implementation = GiftcardBalanceResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Bad Request - Transaction not found or refund amount exceeds original",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Gift card not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<GiftcardBalanceResponse> refundGiftcard(
            @Parameter(description = "Gift card identifier", required = true)
            @PathVariable String giftCardId,
            @Valid @RequestBody RefundToGiftcardRequest request) {
        
        logger.debug("Received POST request to /api/v1/giftcards/{}/refund with body: {}", giftCardId, request);
        
        // Retrieve the gift card from the database
        Giftcard giftcard = giftcardRepository.findById(giftCardId)
                .orElseThrow(() -> {
                    logger.warn("POST request to /api/v1/giftcards/{}/refund resulted in 404: Gift card not found", giftCardId);
                    return new GiftcardNotFoundException("Gift card not found with id: " + giftCardId);
                });
        
        // Create and execute the RefundCommand using BeanFactory
        // The RefundCommand.execute() method validates the original transaction and creates a PLUS transaction
        // Input: giftcard, orderReference, amount, currencyCode
        // Output: boolean (true if successful)
        // Throws: TransactionNotFoundException if original transaction not found
        // Throws: TransactionAmountLessRefundException if refund amount exceeds original
        RefundCommand command = beanFactory.getBean(RefundCommand.class);
        command.withGiftcard(giftcard)
               .withOrderReference(request.getOrderReference())
               .withAmount(request.getAmount())
               .withCurrencyCode(request.getCurrencyCode());
        
        command.execute();
        
        // Persist the updated gift card to the database
        giftcard = giftcardRepository.save(giftcard);
        
        logger.info("POST request to /api/v1/giftcards/{}/refund completed with status 200", giftCardId);
        
        // Map the domain entity to response DTO
        GiftcardBalanceResponse response = GiftcardMapper.toGiftcardBalanceResponse(giftcard);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Custom exception for gift card not found scenarios
     */
    public static class GiftcardNotFoundException extends RuntimeException {
        public GiftcardNotFoundException(String message) {
            super(message);
        }
    }
}

// Made with Bob