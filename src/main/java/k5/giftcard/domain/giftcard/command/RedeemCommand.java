/**
 * RedeemCommand - Instance Command
 * 
 * Subtracts amount from the giftcard and uses it for payment.
 * 
 * This is an instance command that modifies an existing Giftcard aggregate.
 * 
 * Command Logic:
 * - Checks balance of gift card; if balance is less than the redemption amount
 *   throw business error BalanceNotSufficient
 * - If the balance is sufficient, create a Transaction instance and map the
 *   values from the input entity to it (the action is SUBTRACT, the transactionId
 *   need to be generated automatically)
 * - Subtract the transaction amount from the balance (in the Giftcard entity)
 * 
 * Preconditions:
 * - Existing gift card
 * 
 * Input:
 * - orderReference
 * - amount
 * - currencyCode
 * 
 * Postconditions:
 * - The redemption was performed and the balance adjusted
 * - A transaction for the redemption is present
 * - Adjusted values of gift card present in database
 */
package k5.giftcard.domain.giftcard.command;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import k5.giftcard.domain.giftcard.entity.Giftcard;
import k5.giftcard.domain.giftcard.entity.Transaction;
import k5.giftcard.domain.giftcard.exception.BalanceNotSufficientException;

public class RedeemCommand {
    
    private static final Logger logger = LoggerFactory.getLogger(RedeemCommand.class);
    
    private Giftcard giftcard;
    private String orderReference;
    private BigDecimal amount;
    private String currencyCode;
    
    /**
     * Default constructor
     */
    public RedeemCommand() {
        logger.debug("Creating new RedeemCommand instance with default constructor");
    }
    
    /**
     * Constructor with all parameters
     * 
     * @param giftcard The gift card to redeem from
     * @param orderReference The order reference for this redemption
     * @param amount The amount to redeem
     * @param currencyCode The currency code for the redemption
     */
    public RedeemCommand(Giftcard giftcard, String orderReference, BigDecimal amount, String currencyCode) {
        logger.debug("Creating new RedeemCommand instance with parameters: giftcardId={}, orderReference={}, amount={}, currencyCode={}", 
                    giftcard != null ? giftcard.getGiftcardId() : null, orderReference, amount, currencyCode);
        this.giftcard = giftcard;
        this.orderReference = orderReference;
        this.amount = amount;
        this.currencyCode = currencyCode;
    }
    
    /**
     * Executes the redeem command to subtract amount from the gift card
     * 
     * This method:
     * 1. Validates that the gift card balance is sufficient
     * 2. Creates a SUBTRACT transaction with the redemption details
     * 3. Subtracts the amount from the gift card balance
     * 4. Adds the transaction to the gift card
     * 5. Returns true if successful
     * 
     * @return true if the redemption was successful
     * @throws BalanceNotSufficientException if the balance is less than the redemption amount
     */
    public boolean execute() {
        logger.debug("Executing RedeemCommand with parameters: giftcardId={}, orderReference={}, amount={}, currencyCode={}", 
                    giftcard != null ? giftcard.getGiftcardId() : null, orderReference, amount, currencyCode);
        
        try {
            // Check if balance is sufficient
            if (giftcard.getBalance().compareTo(amount) < 0) {
                String errorMessage = String.format(
                    "Balance not sufficient to process redemption. Current balance: %s, Requested amount: %s",
                    giftcard.getBalance(), amount
                );
                logger.warn("RedeemCommand failed: {}", errorMessage);
                throw new BalanceNotSufficientException(errorMessage);
            }
            
            logger.debug("Balance check passed. Current balance: {}, Redemption amount: {}", 
                        giftcard.getBalance(), amount);
            
            // Create a SUBTRACT transaction
            String transactionId = UUID.randomUUID().toString();
            Transaction redeemTransaction = new Transaction();
            redeemTransaction.setTransactionId(transactionId)
                            .setOrderReference(orderReference)
                            .setAction(Transaction.Action.SUBTRACT)
                            .setAmount(amount)
                            .setCurrencyCode(currencyCode)
                            .setExecutionTimestamp(Instant.now());
            
            logger.debug("Created SUBTRACT transaction with id: {}", transactionId);
            
            // Subtract the amount from the balance
            BigDecimal newBalance = giftcard.getBalance().subtract(amount);
            giftcard.setBalance(newBalance);
            
            logger.debug("Updated gift card balance from {} to {}", 
                        giftcard.getBalance().add(amount), newBalance);
            
            // Add the transaction to the gift card
            giftcard.addTransaction(redeemTransaction);
            
            logger.info("RedeemCommand completed successfully for giftcardId: {}, orderReference: {}, amount: {}", 
                       giftcard.getGiftcardId(), orderReference, amount);
            
            // Note: The caller is responsible for persisting the updated gift card to the database
            
            return true;
            
        } catch (BalanceNotSufficientException e) {
            logger.error("Error executing RedeemCommand: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error executing RedeemCommand: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Gets the gift card
     * 
     * @return The gift card to redeem from
     */
    public Giftcard getGiftcard() {
        return giftcard;
    }
    
    /**
     * Sets the gift card using fluent interface
     * 
     * @param giftcard The gift card to redeem from
     * @return This RedeemCommand instance for method chaining
     */
    public RedeemCommand withGiftcard(Giftcard giftcard) {
        this.giftcard = giftcard;
        return this;
    }
    
    /**
     * Gets the order reference
     * 
     * @return The order reference for this redemption
     */
    public String getOrderReference() {
        return orderReference;
    }
    
    /**
     * Sets the order reference using fluent interface
     * 
     * @param orderReference The order reference for this redemption
     * @return This RedeemCommand instance for method chaining
     */
    public RedeemCommand withOrderReference(String orderReference) {
        this.orderReference = orderReference;
        return this;
    }
    
    /**
     * Gets the amount
     * 
     * @return The amount to redeem
     */
    public BigDecimal getAmount() {
        return amount;
    }
    
    /**
     * Sets the amount using fluent interface
     * 
     * @param amount The amount to redeem
     * @return This RedeemCommand instance for method chaining
     */
    public RedeemCommand withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }
    
    /**
     * Gets the currency code
     * 
     * @return The currency code for the redemption
     */
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    /**
     * Sets the currency code using fluent interface
     * 
     * @param currencyCode The currency code for the redemption
     * @return This RedeemCommand instance for method chaining
     */
    public RedeemCommand withCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }
}

// Made with Bob
