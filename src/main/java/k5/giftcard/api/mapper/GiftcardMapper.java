/**
 * GiftcardMapper
 * 
 * Utility class for mapping between domain entities and API DTOs.
 * Provides methods to convert Giftcard and Transaction entities to their corresponding response DTOs.
 */
package k5.giftcard.api.mapper;

import java.util.List;
import java.util.stream.Collectors;

import k5.giftcard.api.dto.GiftcardBalanceResponse;
import k5.giftcard.api.dto.GiftcardResponse;
import k5.giftcard.api.dto.TransactionResponse;
import k5.giftcard.domain.giftcard.entity.Giftcard;
import k5.giftcard.domain.giftcard.entity.Transaction;

public class GiftcardMapper {
    
    /**
     * Private constructor to prevent instantiation
     */
    private GiftcardMapper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Converts a Giftcard entity to a GiftcardResponse DTO
     * 
     * @param giftcard The Giftcard entity to convert
     * @return The converted GiftcardResponse DTO
     */
    public static GiftcardResponse toGiftcardResponse(Giftcard giftcard) {
        if (giftcard == null) {
            return null;
        }
        
        GiftcardResponse response = new GiftcardResponse();
        response.setGiftcardId(giftcard.getGiftcardId());
        response.setCustomerId(giftcard.getCustomerId());
        response.setName(giftcard.getName());
        response.setDescription(giftcard.getName()); // Using name as description
        response.setBalance(giftcard.getBalance());
        response.setCurrencyCode(giftcard.getCurrencyCode());
        
        return response;
    }
    
    /**
     * Converts a list of Giftcard entities to a list of GiftcardResponse DTOs
     * 
     * @param giftcards The list of Giftcard entities to convert
     * @return The converted list of GiftcardResponse DTOs
     */
    public static List<GiftcardResponse> toGiftcardResponseList(List<Giftcard> giftcards) {
        if (giftcards == null) {
            return null;
        }
        
        return giftcards.stream()
                .map(GiftcardMapper::toGiftcardResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Converts a Giftcard entity to a GiftcardBalanceResponse DTO
     * 
     * @param giftcard The Giftcard entity to convert
     * @return The converted GiftcardBalanceResponse DTO
     */
    public static GiftcardBalanceResponse toGiftcardBalanceResponse(Giftcard giftcard) {
        if (giftcard == null) {
            return null;
        }
        
        GiftcardBalanceResponse response = new GiftcardBalanceResponse();
        response.setGiftcardId(giftcard.getGiftcardId());
        response.setBalance(giftcard.getBalance());
        response.setCurrencyCode(giftcard.getCurrencyCode());
        
        return response;
    }
    
    /**
     * Converts a Transaction entity to a TransactionResponse DTO
     * 
     * @param transaction The Transaction entity to convert
     * @return The converted TransactionResponse DTO
     */
    public static TransactionResponse toTransactionResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        
        TransactionResponse response = new TransactionResponse();
        response.setTransactionId(transaction.getTransactionId());
        response.setAction(transaction.getAction() != null ? transaction.getAction().name() : null);
        response.setOrderReference(transaction.getOrderReference());
        response.setAmount(transaction.getAmount());
        response.setExecutionTimestamp(transaction.getExecutionTimestamp());
        
        return response;
    }
    
    /**
     * Converts a list of Transaction entities to a list of TransactionResponse DTOs
     * 
     * @param transactions The list of Transaction entities to convert
     * @return The converted list of TransactionResponse DTOs
     */
    public static List<TransactionResponse> toTransactionResponseList(List<Transaction> transactions) {
        if (transactions == null) {
            return null;
        }
        
        return transactions.stream()
                .map(GiftcardMapper::toTransactionResponse)
                .collect(Collectors.toList());
    }
}

// Made with Bob