/**
 * GiftcardResponse DTO
 * 
 * Response body containing gift card information.
 * Includes balance, currency code, customer ID, name, and gift card ID.
 */
package k5.giftcard.api.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Gift card information response")
public class GiftcardResponse {
    
    @Schema(description = "Current balance of the gift card", example = "75.50")
    private BigDecimal balance;
    
    @Schema(description = "Currency code for the gift card balance", example = "EUR")
    private String currencyCode;
    
    @Schema(description = "Identifier of the customer who owns the gift card", example = "CUST-12345")
    private String customerId;
    
    @Schema(description = "Name or description of the gift card", example = "Birthday Gift Card")
    private String description;
    
    @Schema(description = "Name of the gift card", example = "Gift Card")
    private String name;
    
    @Schema(description = "Unique identifier of the gift card", example = "gc-123456")
    private String giftcardId;
    
    /**
     * Default constructor
     */
    public GiftcardResponse() {
    }
    
    /**
     * Constructor with all parameters
     * 
     * @param balance Current balance of the gift card
     * @param currencyCode Currency code for the gift card balance
     * @param customerId Identifier of the customer who owns the gift card
     * @param description Description of the gift card
     * @param name Name of the gift card
     * @param giftcardId Unique identifier of the gift card
     */
    public GiftcardResponse(BigDecimal balance, String currencyCode, String customerId, 
                           String description, String name, String giftcardId) {
        this.balance = balance;
        this.currencyCode = currencyCode;
        this.customerId = customerId;
        this.description = description;
        this.name = name;
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
    
    /**
     * Gets the description
     * 
     * @return Description of the gift card
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the description
     * 
     * @param description Description of the gift card
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the name
     * 
     * @return Name of the gift card
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name
     * 
     * @param name Name of the gift card
     */
    public void setName(String name) {
        this.name = name;
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