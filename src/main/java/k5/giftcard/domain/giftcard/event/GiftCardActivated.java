/**
 * GiftCardActivated domain event.
 *
 * Represents the successful activation of a gift card and carries the
 * business payload required for asynchronous integration with other systems.
 */
package k5.giftcard.domain.giftcard.event;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event payload published when a gift card has been activated.
 */
public class GiftCardActivated {

    private String giftcardId;
    private String customerId;
    private Instant activatedAt;
    private BigDecimal balance;
    private String currencyCode;

    /**
     * Default constructor for serialization frameworks.
     */
    public GiftCardActivated() {
        // Default constructor required for Kafka/Jackson deserialization.
    }

    /**
     * Creates a fully populated gift card activated event.
     *
     * @param giftcardId unique identifier of the activated gift card
     * @param customerId unique identifier of the customer owning the gift card
     * @param activatedAt timestamp when the gift card activation occurred
     * @param balance activated balance amount
     * @param currencyCode ISO currency code of the activated balance
     */
    public GiftCardActivated(
            String giftcardId,
            String customerId,
            Instant activatedAt,
            BigDecimal balance,
            String currencyCode) {
        this.giftcardId = giftcardId;
        this.customerId = customerId;
        this.activatedAt = activatedAt;
        this.balance = balance;
        this.currencyCode = currencyCode;
    }

    /**
     * Returns the gift card identifier.
     *
     * @return gift card identifier
     */
    public String getGiftcardId() {
        return giftcardId;
    }

    /**
     * Sets the gift card identifier.
     *
     * @param giftcardId gift card identifier
     * @return current event instance for fluent chaining
     */
    public GiftCardActivated setGiftcardId(String giftcardId) {
        this.giftcardId = giftcardId;
        return this;
    }

    /**
     * Returns the customer identifier.
     *
     * @return customer identifier
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the customer identifier.
     *
     * @param customerId customer identifier
     * @return current event instance for fluent chaining
     */
    public GiftCardActivated setCustomerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    /**
     * Returns the activation timestamp.
     *
     * @return activation timestamp
     */
    public Instant getActivatedAt() {
        return activatedAt;
    }

    /**
     * Sets the activation timestamp.
     *
     * @param activatedAt activation timestamp
     * @return current event instance for fluent chaining
     */
    public GiftCardActivated setActivatedAt(Instant activatedAt) {
        this.activatedAt = activatedAt;
        return this;
    }

    /**
     * Returns the activated balance.
     *
     * @return activated balance
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Sets the activated balance.
     *
     * @param balance activated balance
     * @return current event instance for fluent chaining
     */
    public GiftCardActivated setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }

    /**
     * Returns the currency code.
     *
     * @return currency code
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * Sets the currency code.
     *
     * @param currencyCode ISO currency code
     * @return current event instance for fluent chaining
     */
    public GiftCardActivated setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }
}

// Made with Bob
