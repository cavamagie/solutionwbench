/**
 * ActivateCommand - Factory Command
 * 
 * Activates a gift card with the given amount, actually creates it with the
 * specified balance.
 * 
 * This is a factory command that creates a new instance of the Giftcard aggregate root.
 * 
 * Command Logic:
 * - Creates a giftcard instance
 * - Sets the amount and the currencyCode and assigns the card to the customer
 * - Sets the originalAmount of the gift card to the same value as balance
 * - Writes a transaction with status ACTIVATE
 * - Sends an event that the gift card was activated
 * 
 * Preconditions:
 * - customerId must be provided
 * - balance must be provided
 * - currencyCode must be provided
 * 
 * Postconditions:
 * - The gift card is valid and ready to use
 * - Gift card is created in database and has the fields set
 * - An activate transaction is present
 */
package k5.giftcard.domain.giftcard.command;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import k5.giftcard.domain.giftcard.entity.Giftcard;
import k5.giftcard.domain.giftcard.entity.Transaction;

public class ActivateCommand {
    
    private static final Logger logger = LoggerFactory.getLogger(ActivateCommand.class);
    
    private String customerId;
    private BigDecimal balance;
    private String currencyCode;
    
    /**
     * Default constructor
     */
    public ActivateCommand() {
        logger.debug("Creating new ActivateCommand instance with default constructor");
    }
    
    /**
     * Constructor with all parameters
     * 
     * @param customerId The ID of the customer who owns the gift card
     * @param balance The initial balance of the gift card
     * @param currencyCode The currency code for the gift card balance
     */
    public ActivateCommand(String customerId, BigDecimal balance, String currencyCode) {
        logger.debug("Creating new ActivateCommand instance with parameters: customerId={}, balance={}, currencyCode={}", 
                    customerId, balance, currencyCode);
        this.customerId = customerId;
        this.balance = balance;
        this.currencyCode = currencyCode;
    }
    
    /**
     * Executes the activate command to create and initialize a new gift card
     * 
     * This method:
     * 1. Creates a new Giftcard instance with a generated ID
     * 2. Sets the balance, currency code, and customer ID
     * 3. Creates an ACTIVATE transaction
     * 4. Adds the transaction to the gift card
     * 5. Returns the created gift card (ready to be persisted and event published)
     * 
     * @return The newly created and activated Giftcard instance
     */
    public Giftcard execute() {
        logger.debug("Executing ActivateCommand with parameters: customerId={}, balance={}, currencyCode={}", 
                    customerId, balance, currencyCode);
        
        try {
            // Generate a unique gift card ID
            String giftcardId = UUID.randomUUID().toString();
            logger.debug("Generated giftcardId: {}", giftcardId);
            
            // Create a new Giftcard instance
            Giftcard giftcard = new Giftcard();
            giftcard.setGiftcardId(giftcardId)
                    .setCustomerId(customerId)
                    .setBalance(balance)
                    .setCurrencyCode(currencyCode);
            
            logger.debug("Created Giftcard instance with id: {}", giftcardId);
            
            // Create an ACTIVATE transaction
            String transactionId = UUID.randomUUID().toString();
            Transaction activateTransaction = new Transaction();
            activateTransaction.setTransactionId(transactionId)
                              .setAction(Transaction.Action.ACTIVATE)
                              .setAmount(balance)
                              .setCurrencyCode(currencyCode)
                              .setExecutionTimestamp(Instant.now());
            
            logger.debug("Created ACTIVATE transaction with id: {}", transactionId);
            
            // Add the transaction to the gift card
            giftcard.addTransaction(activateTransaction);
            
            logger.info("ActivateCommand completed successfully for giftcardId: {}", giftcardId);
            
            // Note: The caller is responsible for:
            // - Persisting the gift card to the database
            // - Publishing the GiftCardActivated event
            
            return giftcard;
            
        } catch (Exception e) {
            logger.error("Error executing ActivateCommand: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Gets the customer ID
     * 
     * @return The ID of the customer who owns the gift card
     */
    public String getCustomerId() {
        return customerId;
    }
    
    /**
     * Sets the customer ID using fluent interface
     * 
     * @param customerId The ID of the customer who owns the gift card
     * @return This ActivateCommand instance for method chaining
     */
    public ActivateCommand withCustomerId(String customerId) {
        this.customerId = customerId;
        return this;
    }
    
    /**
     * Gets the balance
     * 
     * @return The initial balance of the gift card
     */
    public BigDecimal getBalance() {
        return balance;
    }
    
    /**
     * Sets the balance using fluent interface
     * 
     * @param balance The initial balance of the gift card
     * @return This ActivateCommand instance for method chaining
     */
    public ActivateCommand withBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }
    
    /**
     * Gets the currency code
     * 
     * @return The currency code for the gift card balance
     */
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    /**
     * Sets the currency code using fluent interface
     * 
     * @param currencyCode The currency code for the gift card balance
     * @return This ActivateCommand instance for method chaining
     */
    public ActivateCommand withCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }
}

// Made with Bob
