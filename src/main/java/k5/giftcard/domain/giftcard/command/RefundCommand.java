/**
 * RefundCommand - Instance Command
 * 
 * Adds amount in case there was a return of the order.
 * 
 * This is an instance command that modifies an existing Giftcard aggregate.
 * Refunds a gift card redemption (partly). For example necessary when for this
 * gift card a redemption was performed and the order was (partly) returned,
 * so that the or a part of the money shall be refunded.
 * 
 * Command Logic:
 * - First, the original transaction must be determined (by the orderReference) -
 *   if not found a TransactionNotFound business error shall be thrown
 * - Then check further whether the to be refunded amount is not greater than the
 *   original transaction value - otherwise a different Business Error
 *   TransactionAmountLessRefund needs to be sent
 * - Create transaction for the refund and update gift card balance
 * 
 * Preconditions:
 * - Existing gift card, redemption was processed before
 * 
 * Input:
 * - orderReference
 * - amount
 * - currencyCode
 * 
 * Postconditions:
 * - The refund was performed and the balance adjusted
 * - A transaction for the refund is present
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
import k5.giftcard.domain.giftcard.exception.TransactionAmountLessRefundException;
import k5.giftcard.domain.giftcard.exception.TransactionNotFoundException;

public class RefundCommand {
    
    private static final Logger logger = LoggerFactory.getLogger(RefundCommand.class);
    
    private Giftcard giftcard;
    private String orderReference;
    private BigDecimal amount;
    private String currencyCode;
    
    /**
     * Default constructor
     */
    public RefundCommand() {
        logger.debug("Creating new RefundCommand instance with default constructor");
    }
    
    /**
     * Constructor with all parameters
     * 
     * @param giftcard The gift card to refund to
     * @param orderReference The order reference for the original transaction
     * @param amount The amount to refund
     * @param currencyCode The currency code for the refund
     */
    public RefundCommand(Giftcard giftcard, String orderReference, BigDecimal amount, String currencyCode) {
        logger.debug("Creating new RefundCommand instance with parameters: giftcardId={}, orderReference={}, amount={}, currencyCode={}", 
                    giftcard != null ? giftcard.getGiftcardId() : null, orderReference, amount, currencyCode);
        this.giftcard = giftcard;
        this.orderReference = orderReference;
        this.amount = amount;
        this.currencyCode = currencyCode;
    }
    
    /**
     * Executes the refund command to add amount back to the gift card
     * 
     * This method:
     * 1. Finds the original transaction by order reference
     * 2. Validates that the refund amount does not exceed the original transaction amount
     * 3. Creates a PLUS transaction with the refund details
     * 4. Adds the amount back to the gift card balance
     * 5. Adds the transaction to the gift card
     * 6. Returns true if successful
     * 
     * @return true if the refund was successful
     * @throws TransactionNotFoundException if the original transaction cannot be found
     * @throws TransactionAmountLessRefundException if the refund amount exceeds the original transaction amount
     */
    public boolean execute() {
        logger.debug("Executing RefundCommand with parameters: giftcardId={}, orderReference={}, amount={}, currencyCode={}", 
                    giftcard != null ? giftcard.getGiftcardId() : null, orderReference, amount, currencyCode);
        
        try {
            // Find the original transaction by order reference
            Transaction originalTransaction = giftcard.getTransactions().stream()
                .filter(t -> orderReference.equals(t.getOrderReference()))
                .filter(t -> Transaction.Action.SUBTRACT.equals(t.getAction()))
                .findFirst()
                .orElse(null);
            
            if (originalTransaction == null) {
                String errorMessage = String.format(
                    "Transaction not found for orderReference: %s", orderReference
                );
                logger.warn("RefundCommand failed: {}", errorMessage);
                throw new TransactionNotFoundException(errorMessage);
            }
            
            logger.debug("Found original transaction with id: {}, amount: {}", 
                        originalTransaction.getTransactionId(), originalTransaction.getAmount());
            
            // Check if refund amount exceeds original transaction amount
            if (amount.compareTo(originalTransaction.getAmount()) > 0) {
                String errorMessage = String.format(
                    "Refund amount (%s) exceeds original transaction amount (%s) for orderReference: %s",
                    amount, originalTransaction.getAmount(), orderReference
                );
                logger.warn("RefundCommand failed: {}", errorMessage);
                throw new TransactionAmountLessRefundException(errorMessage);
            }
            
            logger.debug("Refund amount validation passed. Original amount: {}, Refund amount: {}", 
                        originalTransaction.getAmount(), amount);
            
            // Create a PLUS transaction for the refund
            String transactionId = UUID.randomUUID().toString();
            Transaction refundTransaction = new Transaction();
            refundTransaction.setTransactionId(transactionId)
                            .setOrderReference(orderReference)
                            .setAction(Transaction.Action.PLUS)
                            .setAmount(amount)
                            .setCurrencyCode(currencyCode)
                            .setExecutionTimestamp(Instant.now());
            
            logger.debug("Created PLUS transaction with id: {}", transactionId);
            
            // Add the amount back to the balance
            BigDecimal newBalance = giftcard.getBalance().add(amount);
            giftcard.setBalance(newBalance);
            
            logger.debug("Updated gift card balance from {} to {}", 
                        giftcard.getBalance().subtract(amount), newBalance);
            
            // Add the transaction to the gift card
            giftcard.addTransaction(refundTransaction);
            
            logger.info("RefundCommand completed successfully for giftcardId: {}, orderReference: {}, amount: {}", 
                       giftcard.getGiftcardId(), orderReference, amount);
            
            // Note: The caller is responsible for persisting the updated gift card to the database
            
            return true;
            
        } catch (TransactionNotFoundException | TransactionAmountLessRefundException e) {
            logger.error("Error executing RefundCommand: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error executing RefundCommand: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * Gets the gift card
     * 
     * @return The gift card to refund to
     */
    public Giftcard getGiftcard() {
        return giftcard;
    }
    
    /**
     * Sets the gift card using fluent interface
     * 
     * @param giftcard The gift card to refund to
     * @return This RefundCommand instance for method chaining
     */
    public RefundCommand withGiftcard(Giftcard giftcard) {
        this.giftcard = giftcard;
        return this;
    }
    
    /**
     * Gets the order reference
     * 
     * @return The order reference for the original transaction
     */
    public String getOrderReference() {
        return orderReference;
    }
    
    /**
     * Sets the order reference using fluent interface
     * 
     * @param orderReference The order reference for the original transaction
     * @return This RefundCommand instance for method chaining
     */
    public RefundCommand withOrderReference(String orderReference) {
        this.orderReference = orderReference;
        return this;
    }
    
    /**
     * Gets the amount
     * 
     * @return The amount to refund
     */
    public BigDecimal getAmount() {
        return amount;
    }
    
    /**
     * Sets the amount using fluent interface
     * 
     * @param amount The amount to refund
     * @return This RefundCommand instance for method chaining
     */
    public RefundCommand withAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }
    
    /**
     * Gets the currency code
     * 
     * @return The currency code for the refund
     */
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    /**
     * Sets the currency code using fluent interface
     * 
     * @param currencyCode The currency code for the refund
     * @return This RefundCommand instance for method chaining
     */
    public RefundCommand withCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }
}

// Made with Bob
