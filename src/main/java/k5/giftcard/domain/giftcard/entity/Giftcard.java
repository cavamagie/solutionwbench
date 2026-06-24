/**
 * Giftcard Entity - Aggregate Root
 * 
 * A Gift Card is a prepaid digital card that holds a specific monetary value,
 * which can be redeemed by the recipient for goods or services within the store.
 * 
 * It includes attributes like the balance, currency and status (active, redeemed, expired).
 * 
 * This entity is the aggregate root, meaning it is the only entity that external objects
 * are allowed to hold references to. It is the entry point for accessing the aggregate.
 * 
 * Properties:
 * - giftcardId: Unique identifier for the gift card
 * - customerId: Identifier of the customer who owns the gift card
 * - name: Name or description of the gift card
 * - balance: Current balance of the gift card
 * - currencyCode: Currency code for the gift card balance
 * - transactions: List of transactions associated with this gift card
 */
package k5.giftcard.domain.giftcard.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "giftcards")
public class Giftcard {
    
    private static final Logger logger = LoggerFactory.getLogger(Giftcard.class);
    
    @Id
    private String giftcardId;
    private String customerId;
    private String name;
    private BigDecimal balance;
    private String currencyCode;
    private List<Transaction> transactions;
    
    /**
     * Default constructor
     * Initializes the transactions list to an empty ArrayList
     */
    public Giftcard() {
        logger.debug("Creating new Giftcard instance with default constructor");
        this.transactions = new ArrayList<>();
    }
    
    /**
     * Constructor with all parameters
     * 
     * @param giftcardId Unique identifier for the gift card
     * @param customerId Identifier of the customer who owns the gift card
     * @param name Name or description of the gift card
     * @param balance Current balance of the gift card
     * @param currencyCode Currency code for the gift card balance
     * @param transactions List of transactions associated with this gift card
     */
    public Giftcard(String giftcardId, String customerId, String name, 
                   BigDecimal balance, String currencyCode, List<Transaction> transactions) {
        logger.debug("Creating new Giftcard instance with parameters: giftcardId={}, customerId={}, name={}, balance={}, currencyCode={}, transactions count={}", 
                    giftcardId, customerId, name, balance, currencyCode, 
                    transactions != null ? transactions.size() : 0);
        this.giftcardId = giftcardId;
        this.customerId = customerId;
        this.name = name;
        this.balance = balance;
        this.currencyCode = currencyCode;
        this.transactions = transactions != null ? transactions : new ArrayList<>();
    }
    
    /**
     * Gets the gift card ID
     * 
     * @return The unique identifier for the gift card
     */
    public String getGiftcardId() {
        return giftcardId;
    }
    
    /**
     * Sets the gift card ID
     * 
     * @param giftcardId The unique identifier for the gift card
     * @return This Giftcard instance for method chaining
     */
    public Giftcard setGiftcardId(String giftcardId) {
        this.giftcardId = giftcardId;
        return this;
    }
    
    /**
     * Gets the customer ID
     * 
     * @return The identifier of the customer who owns the gift card
     */
    public String getCustomerId() {
        return customerId;
    }
    
    /**
     * Sets the customer ID
     * 
     * @param customerId The identifier of the customer who owns the gift card
     * @return This Giftcard instance for method chaining
     */
    public Giftcard setCustomerId(String customerId) {
        this.customerId = customerId;
        return this;
    }
    
    /**
     * Gets the gift card name
     * 
     * @return The name or description of the gift card
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the gift card name
     * 
     * @param name The name or description of the gift card
     * @return This Giftcard instance for method chaining
     */
    public Giftcard setName(String name) {
        this.name = name;
        return this;
    }
    
    /**
     * Gets the gift card balance
     * 
     * @return The current balance of the gift card
     */
    public BigDecimal getBalance() {
        return balance;
    }
    
    /**
     * Sets the gift card balance
     * 
     * @param balance The current balance of the gift card
     * @return This Giftcard instance for method chaining
     */
    public Giftcard setBalance(BigDecimal balance) {
        this.balance = balance;
        return this;
    }
    
    /**
     * Gets the currency code
     * 
     * @return The currency code for the gift card balance
     */
    public String getCurrencyCode() {
        return currencyCode;
    }
    
    /**
     * Sets the currency code
     * 
     * @param currencyCode The currency code for the gift card balance
     * @return This Giftcard instance for method chaining
     */
    public Giftcard setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }
    
    /**
     * Gets the list of transactions
     * 
     * @return The list of transactions associated with this gift card
     */
    public List<Transaction> getTransactions() {
        return transactions;
    }
    
    /**
     * Sets the list of transactions
     * 
     * @param transactions The list of transactions associated with this gift card
     * @return This Giftcard instance for method chaining
     */
    public Giftcard setTransactions(List<Transaction> transactions) {
        this.transactions = transactions != null ? transactions : new ArrayList<>();
        return this;
    }
    
    /**
     * Adds a transaction to the gift card
     * 
     * @param transaction The transaction to add
     * @return This Giftcard instance for method chaining
     */
    public Giftcard addTransaction(Transaction transaction) {
        if (transaction != null) {
            if (this.transactions == null) {
                this.transactions = new ArrayList<>();
            }
            this.transactions.add(transaction);
            logger.debug("Added transaction {} to gift card {}", transaction.getTransactionId(), this.giftcardId);
        }
        return this;
    }
}

// Made with Bob
