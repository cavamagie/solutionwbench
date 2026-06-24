/**
 * Transaction Value Object
 * 
 * Holds details for a single transaction, e.g., for activation or redemption.
 * This is a value object that represents a domain concept defined entirely by its attributes
 * and has no identity. Value objects are immutable and are compared by value rather than identity.
 * 
 * Properties:
 * - transactionId: Unique identifier for the transaction
 * - orderReference: Reference to the associated order
 * - action: Type of transaction action (ACTIVATE, EXPIRE, PLUS, SUBTRACT)
 * - amount: Transaction amount
 * - currencyCode: Currency code for the transaction
 * - executionTimestamp: Timestamp when the transaction was executed
 */
package k5.giftcard.domain.giftcard.entity;

import java.math.BigDecimal;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.Field;

public class Transaction {
    
    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);
    
    /**
     * Transaction action types
     */
    public enum Action {
        ACTIVATE,
        EXPIRE,
        PLUS,
        SUBTRACT
    }
    
    @Field("transactionId")
    private String transactionId;

    @Field("orderReference")
    private String orderReference;

    @Field("action")
    private Action action;

    @Field("amount")
    private BigDecimal amount;

    @Field("currencyCode")
    private String currencyCode;

    @Field("executionTimestamp")
    private Instant executionTimestamp;
    
    /**
     * Default constructor
     */
    public Transaction() {
        logger.debug("Creating new Transaction instance with default constructor");
    }
    
    /**
     * Constructor with all parameters
     * 
     * @param transactionId Unique identifier for the transaction
     * @param orderReference Reference to the associated order
     * @param action Type of transaction action
     * @param amount Transaction amount
     * @param currencyCode Currency code for the transaction
     * @param executionTimestamp Timestamp when the transaction was executed
     */
    public Transaction(String transactionId, String orderReference, Action action, 
                      BigDecimal amount, String currencyCode, Instant executionTimestamp) {
        logger.debug("Creating new Transaction instance with parameters: transactionId={}, orderReference={}, action={}, amount={}, currencyCode={}, executionTimestamp={}", 
                    transactionId, orderReference, action, amount, currencyCode, executionTimestamp);
        this.transactionId = transactionId;
        this.orderReference = orderReference;
        this.action = action;
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.executionTimestamp = executionTimestamp;
    }
    
    /**
     * Gets the transaction ID
     * 
     * @return The unique identifier for the transaction
     */
    public String getTransactionId() {
        return transactionId;
    }
    
    /**
     * Sets the transaction ID
     * 
     * @param transactionId The unique identifier for the transaction
     * @return This Transaction instance for method chaining
     */
    public Transaction setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }
    
    /**
     * Gets the order reference
     * 
     * @return The reference to the associated order
     */
    public String getOrderReference() {
        return orderReference;
    }
    
    /**
     * Sets the order reference
     * 
     * @param orderReference The reference to the associated order
     * @return This Transaction instance for method chaining
     */
    public Transaction setOrderReference(String orderReference) {
        this.orderReference = orderReference;
        return this;
    }
    
    /**
     * Gets the transaction action
     * 
     * @return The type of transaction action
     */
    public Action getAction() {
        return action;
    }
    
    /**
     * Sets the transaction action
     * 
     * @param action The type of transaction action
     * @return This Transaction instance for method chaining
     */
    public Transaction setAction(Action action) {
        this.action = action;
        return this;
    }
    
    /**
     * Gets the transaction amount
     * 
     * @return The transaction amount
     */
    public BigDecimal getAmount() {
        return amount;
    }
    
    /**
     * Sets the transaction amount
     * 
     * @param amount The transaction amount
     * @return This Transaction instance for method chaining
     */
    public Transaction setAmount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }
    
    /**
     * Gets the currency code
     * 
     * @return The currency code for the transaction
     */
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    /**
     * Sets the currency code
     * 
     * @param currencyCode The currency code for the transaction
     * @return This Transaction instance for method chaining
     */
    public Transaction setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }
    
    /**
     * Gets the execution timestamp
     * 
     * @return The timestamp when the transaction was executed
     */
    public Instant getExecutionTimestamp() {
        return executionTimestamp;
    }
    
    /**
     * Sets the execution timestamp
     * 
     * @param executionTimestamp The timestamp when the transaction was executed
     * @return This Transaction instance for method chaining
     */
    public Transaction setExecutionTimestamp(Instant executionTimestamp) {
        this.executionTimestamp = executionTimestamp;
        return this;
    }
}

// Made with Bob
