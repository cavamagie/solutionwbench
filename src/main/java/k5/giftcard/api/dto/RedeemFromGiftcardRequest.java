/**
 * RedeemFromGiftcardRequest DTO
 * 
 * Request body for redeeming an amount from a gift card.
 * Contains the amount to redeem, currency code, and order reference.
 */
package k5.giftcard.api.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request body for redeeming an amount from a gift card")
public class RedeemFromGiftcardRequest {
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Schema(description = "Amount to redeem from the gift card", example = "25.50", required = true)
    private BigDecimal amount;
    
    @NotNull(message = "Currency code is required")
    @Schema(description = "Currency code for the redemption", example = "EUR", required = true)
    private String currencyCode;
    
    @NotNull(message = "Order reference is required")
    @Schema(description = "Reference to the order for which the gift card is being redeemed", example = "ORD-98765", required = true)
    private String orderReference;
    
    /**
     * Default constructor
     */
    public RedeemFromGiftcardRequest() {
    }
    
    /**
     * Constructor with all parameters
     * 
     * @param amount Amount to redeem from the gift card
     * @param currencyCode Currency code for the redemption
     * @param orderReference Reference to the order
     */
    public RedeemFromGiftcardRequest(BigDecimal amount, String currencyCode, String orderReference) {
        this.amount = amount;
        this.currencyCode = currencyCode;
        this.orderReference = orderReference;
    }
    
    /**
     * Gets the amount
     * 
     * @return Amount to redeem from the gift card
     */
    public BigDecimal getAmount() {
        return amount;
    }
    
    /**
     * Sets the amount
     * 
     * @param amount Amount to redeem from the gift card
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    /**
     * Gets the currency code
     * 
     * @return Currency code for the redemption
     */
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    /**
     * Sets the currency code
     * 
     * @param currencyCode Currency code for the redemption
     */
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
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
}

// Made with Bob