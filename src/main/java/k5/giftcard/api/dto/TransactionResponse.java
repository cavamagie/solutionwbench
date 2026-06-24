/**
 * TransactionResponse DTO
 * 
 * Response body containing transaction information.
 * Includes transaction ID, action, amount, execution timestamp, and order reference.
 */
package k5.giftcard.api.dto;

import java.math.BigDecimal;
import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Transaction information response")
public class TransactionResponse {
    
    @Schema(description = "Unique identifier of the transaction", example = "txn-789012", required = true)
    private String transactionId;
    
    @Schema(description = "Action performed in the transaction", example = "SUBTRACT", required = true, 
            allowableValues = {"PLUS", "SUBTRACT", "ACTIVATE", "EXPIRE"})
    private String action;
    
    @Schema(description = "Reference to the order associated with this transaction", example = "ORD-98765")
    private String orderReference;
    
    @Schema(description = "Amount involved in the transaction", example = "25.50", required = true)
    private BigDecimal amount;
    
    @Schema(description = "Timestamp when the transaction was executed", example = "2024-01-15T10:30:00Z", required = true)
    private Instant executionTimestamp;
    
    /**
     * Default constructor
     */
    public TransactionResponse() {
    }
    
    /**
     * Constructor with all parameters
     * 
     * @param transactionId Unique identifier of the transaction
     * @param action Action performed in the transaction
     * @param orderReference Reference to the order
     * @param amount Amount involved in the transaction
     * @param executionTimestamp Timestamp when the transaction was executed
     */
    public TransactionResponse(String transactionId, String action, String orderReference, 
                              BigDecimal amount, Instant executionTimestamp) {
        this.transactionId = transactionId;
        this.action = action;
        this.orderReference = orderReference;
        this.amount = amount;
        this.executionTimestamp = executionTimestamp;
    }
    
    /**
     * Gets the transaction ID
     * 
     * @return Unique identifier of the transaction
     */
    public String getTransactionId() {
        return transactionId;
    }
    
    /**
     * Sets the transaction ID
     * 
     * @param transactionId Unique identifier of the transaction
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    /**
     * Gets the action
     * 
     * @return Action performed in the transaction
     */
    public String getAction() {
        return action;
    }
    
    /**
     * Sets the action
     * 
     * @param action Action performed in the transaction
     */
    public void setAction(String action) {
        this.action = action;
    }
    
    /**
     * Gets the order reference
     * 
     * @return Reference to the order
     */
    public String getOrderReference() {
        return orderReference;
    }
    
    /**
     * Sets the order reference
     * 
     * @param orderReference Reference to the order
     */
    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }
    
    /**
     * Gets the amount
     * 
     * @return Amount involved in the transaction
     */
    public BigDecimal getAmount() {
        return amount;
    }
    
    /**
     * Sets the amount
     * 
     * @param amount Amount involved in the transaction
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    /**
     * Gets the execution timestamp
     * 
     * @return Timestamp when the transaction was executed
     */
    public Instant getExecutionTimestamp() {
        return executionTimestamp;
    }
    
    /**
     * Sets the execution timestamp
     * 
     * @param executionTimestamp Timestamp when the transaction was executed
     */
    public void setExecutionTimestamp(Instant executionTimestamp) {
        this.executionTimestamp = executionTimestamp;
    }
}

// Made with Bob