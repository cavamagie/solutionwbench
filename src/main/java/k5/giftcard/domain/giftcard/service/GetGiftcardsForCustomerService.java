/**
 * GetGiftcardsForCustomerService - Domain Service
 * 
 * Domain service that determines all gift cards including balance, id, etc. for a customer.
 * This service provides basic information of all gift cards for one customer.
 * 
 * Input:
 * - customerId: The identifier of the customer whose gift cards should be retrieved
 * 
 * Output:
 * - Array of GiftcardInfo elements containing:
 *   - giftcardId: Unique identifier of the gift card
 *   - balance: Current balance of the gift card
 *   - currencyCode: Currency code of the gift card balance
 * 
 * Service Logic:
 * 1. Determine all gift card instances with the provided customerId
 * 2. Map each found instance to the output format
 * 3. Return all as array (array is empty if no gift cards exist for this customer)
 * 
 * Postcondition:
 * - Gift cards for the provided customerId are returned
 */
package k5.giftcard.domain.giftcard.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import k5.giftcard.domain.giftcard.entity.Giftcard;
import k5.giftcard.domain.giftcard.repository.GiftcardRepository;

/**
 * Domain service for retrieving gift cards for a specific customer.
 * This service is stateless and orchestrates the retrieval and mapping of gift card data.
 */
@Service
public class GetGiftcardsForCustomerService {
    
    private static final Logger logger = LoggerFactory.getLogger(GetGiftcardsForCustomerService.class);
    
    private final GiftcardRepository giftcardRepository;
    
    /**
     * Constructor with dependency injection.
     * 
     * @param giftcardRepository Repository for accessing gift card data from MongoDB
     */
    public GetGiftcardsForCustomerService(GiftcardRepository giftcardRepository) {
        this.giftcardRepository = giftcardRepository;
        logger.debug("GetGiftcardsForCustomerService initialized with GiftcardRepository");
    }
    
    /**
     * Executes the domain service to retrieve all gift cards for a customer.
     * 
     * This method queries the repository for all gift cards belonging to the specified customer,
     * then maps each gift card to a simplified output format containing only the essential information.
     * 
     * @param customerId The identifier of the customer whose gift cards should be retrieved
     * @return List of GiftcardInfo objects containing gift card details, empty list if no gift cards found
     */
    public List<GiftcardInfo> execute(String customerId) {
        logger.debug("Executing GetGiftcardsForCustomerService with customerId: {}", customerId);
        
        // Determine all gift card instances with the provided customerId
        List<Giftcard> giftcards = giftcardRepository.findByCustomerId(customerId);
        logger.debug("Found {} gift cards for customerId: {}", giftcards.size(), customerId);
        
        // Map each found instance to the output format
        List<GiftcardInfo> result = mapToGiftcardInfoList(giftcards);
        
        logger.info("GetGiftcardsForCustomerService execution completed. Result: {} gift cards returned for customerId: {}", 
                    result.size(), customerId);
        
        return result;
    }
    
    /**
     * Maps a list of Giftcard entities to a list of GiftcardInfo output objects.
     * 
     * This helper method transforms the full Giftcard entities into simplified GiftcardInfo objects
     * containing only the essential information (giftcardId, balance, currencyCode).
     * 
     * @param giftcards List of Giftcard entities to map
     * @return List of GiftcardInfo objects with mapped data
     */
    private List<GiftcardInfo> mapToGiftcardInfoList(List<Giftcard> giftcards) {
        return giftcards.stream()
                .map(this::mapToGiftcardInfo)
                .collect(Collectors.toList());
    }
    
    /**
     * Maps a single Giftcard entity to a GiftcardInfo output object.
     * 
     * Extracts the essential information from a Giftcard entity and creates
     * a new GiftcardInfo object with giftcardId, balance, and currencyCode.
     * 
     * @param giftcard The Giftcard entity to map
     * @return GiftcardInfo object with mapped data
     */
    private GiftcardInfo mapToGiftcardInfo(Giftcard giftcard) {
        return new GiftcardInfo(
                giftcard.getGiftcardId(),
                giftcard.getBalance(),
                giftcard.getCurrencyCode()
        );
    }
    
    /**
     * Output data class representing essential gift card information.
     * 
     * This class encapsulates the output format for the domain service,
     * containing only the essential information about a gift card.
     */
    public static class GiftcardInfo {
        private final String giftcardId;
        private final BigDecimal balance;
        private final String currencyCode;
        
        /**
         * Constructor for GiftcardInfo.
         * 
         * @param giftcardId Unique identifier of the gift card
         * @param balance Current balance of the gift card
         * @param currencyCode Currency code of the gift card balance
         */
        public GiftcardInfo(String giftcardId, BigDecimal balance, String currencyCode) {
            this.giftcardId = giftcardId;
            this.balance = balance;
            this.currencyCode = currencyCode;
        }
        
        /**
         * Gets the gift card ID.
         * 
         * @return The unique identifier of the gift card
         */
        public String getGiftcardId() {
            return giftcardId;
        }
        
        /**
         * Gets the gift card balance.
         * 
         * @return The current balance of the gift card
         */
        public BigDecimal getBalance() {
            return balance;
        }
        
        /**
         * Gets the currency code.
         * 
         * @return The currency code of the gift card balance
         */
        public String getCurrencyCode() {
            return currencyCode;
        }
    }
}

// Made with Bob