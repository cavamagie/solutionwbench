/**
 * GiftcardBalanceResponse DTO
 * 
 * Response body containing gift card balance information.
 * Includes the current balance, currency code, and gift card ID.
 */
package k5.giftcard.api.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Gift card balance information response")
public class GiftcardBalanceResponse {
    
    @Schema(description = "Current balance of the gift card", example = "75.50", required = true)
    private BigDecimal balance;
    
    @Schema(description = "Currency code for the gift card balance", example = "EUR", required = true)
    private String currencyCode;
    
    @Schema(description = "Unique identifier of the gift card", example = "gc-123456")
    private String giftcardId;
    
    /**
     * Default constructor
     */
    public GiftcardBalanceResponse() {
    }
    
    /**
     * Constructor with all parameters
     * 
     * @param balance Current balance of the gift card
     * @param currencyCode Currency code for the gift card balance
     * @param giftcardId Unique identifier of the gift card
     */
    public GiftcardBalanceResponse(BigDecimal balance, String currencyCode, String giftcardId) {
        this.balance = balance;
        this.currencyCode = currencyCode;
        this.giftcardId = giftcardId;
    }
    
    /**
     * Gets the balance
     * 
     * @return Current balance of the gift card
     */
    public BigDecimal getBalance() {
        return balance;
    }
    
    /**
     * Sets the balance
     * 
     * @param balance Current balance of the gift card
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
     * Gets the gift card ID
     * 
     * @return Unique identifier of the gift card
     */
    public String getGiftcardId() {
        return giftcardId;
    }
    
    /**
     * Sets the gift card ID
     * 
     * @param giftcardId Unique identifier of the gift card
     */
    public void setGiftcardId(String giftcardId) {
        this.giftcardId = giftcardId;
    }
}

// Made with Bob