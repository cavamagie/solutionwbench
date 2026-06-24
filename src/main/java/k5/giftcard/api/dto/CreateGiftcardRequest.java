/**
 * CreateGiftcardRequest DTO
 * 
 * Request body for creating a new gift card.
 * Contains the required information to activate a gift card including balance,
 * currency code, and customer identifier.
 */
package k5.giftcard.api.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Request body for creating a new gift card")
public class CreateGiftcardRequest {
    
    @NotNull(message = "Balance is required")
    @Positive(message = "Balance must be positive")
    @Schema(description = "Initial balance of the gift card", example = "100.00", required = true)
    private BigDecimal balance;
    
    @NotNull(message = "Currency code is required")
    @Schema(description = "Currency code for the gift card balance", example = "EUR", required = true)
    private String currencyCode;
    
    @NotNull(message = "Customer ID is required")
    @Schema(description = "Identifier of the customer who owns the gift card", example = "CUST-12345", required = true)
    private String customerId;
    
    /**
     * Default constructor
     */
    public CreateGiftcardRequest() {
    }
    
    /**
     * Constructor with all parameters
     * 
     * @param balance Initial balance of the gift card
     * @param currencyCode Currency code for the gift card balance
     * @param customerId Identifier of the customer who owns the gift card
     */
    public CreateGiftcardRequest(BigDecimal balance, String currencyCode, String customerId) {
        this.balance = balance;
        this.currencyCode = currencyCode;
        this.customerId = customerId;
    }
    
    /**
     * Gets the balance
     * 
     * @return Initial balance of the gift card
     */
    public BigDecimal getBalance() {
        return balance;
    }
    
    /**
     * Sets the balance
     * 
     * @param balance Initial balance of the gift card
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    /**
     * Gets the currency code
     * 
     * @return Currency code for the gift card balance
     */
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    /**
     * Sets the currency code
     * 
     * @param currencyCode Currency code for the gift card balance
     */
    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
    
    /**
     * Gets the customer ID
     * 
     * @return Identifier of the customer who owns the gift card
     */
    public String getCustomerId() {
        return customerId;
    }
    
    /**
     * Sets the customer ID
     * 
     * @param customerId Identifier of the customer who owns the gift card
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}

// Made with Bob