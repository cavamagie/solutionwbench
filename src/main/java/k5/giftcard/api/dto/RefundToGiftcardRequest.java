/**
 * RefundToGiftcardRequest DTO
 * 
 * Request body for refunding an amount back to a gift card.
 * Contains the amount to refund, currency code, gift card ID, and order reference.
 */
package k5.giftcard.api.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request body for refunding an amount back to a gift card")
public class RefundToGiftcardRequest {
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Amount to refund to the gift card", example = "15.00", required = true)
    private BigDecimal amount;
    
    @NotNull(message = "Currency code is required")
    @Schema(description = "Currency code for the refund", example = "EUR", required = true)
    private String currencyCode;
    
    @NotNull(message = "Gift card ID is required")
    @Schema(description = "Identifier of the gift card", example = "gc-123456", required = true)
    private String giftcardId;
    
    @NotNull(message = "Order reference is required")
    @Schema(description = "Reference to the original order that is being refunded", example = "ORD-98765", required = true)
    private String orderReference;
    
    /**
     * Default constructor
     */
    public RefundToGiftcardRequest() {
    }
    
    /**
     * Constructor with all parameters
     * 
     * @param amount Amount to refund to the gift card
     * @param currencyCode Currency code for the refund
     * @param giftcardId Identifier of the gift card
     * @param orderReference Reference to the original order
     */
    public RefundToGiftcardRequest(BigDecimal amount, String currencyCode, String giftcardId, String orderReference) {
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.giftcardId = giftcardId;
        this.orderReference = orderReference;
    }
    
    /**
     * Gets the amount
     * 
     * @return Amount to refund to the gift card
     */
    public BigDecimal getAmount() {
        return amount;
    }
    
    /**
     * Sets the amount
     * 
     * @param amount Amount to refund to the gift card
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    /**
     * Gets the currency code
     * 
     * @return Currency code for the refund
     */
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    /**
     * Sets the currency code
     * 
     * @param currencyCode Currency code for the refund
     */
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    
    /**
     * Gets the gift card ID
     * 
     * @return Identifier of the gift card
     */
    public String getGiftcardId() {
        return giftcardId;
    }
    
    /**
     * Sets the gift card ID
     * 
     * @param giftcardId Identifier of the gift card
     */
    public void setGiftcardId(String giftcardId) {
        this.giftcardId = giftcardId;
    }
    
    /**
     * Gets the order reference
     * 
     * @return Reference to the original order
     */
    public String getOrderReference() {
        return orderReference;
    }
    
    /**
     * Sets the order reference
     * 
     * @param orderReference Reference to the original order
     */
    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }
}

// Made with Bob