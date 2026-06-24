/**
 * TransactionAmountLessRefundException
 * 
 * Business error exception thrown when the refund amount exceeds the original
 * transaction amount.
 * 
 * This exception represents a domain-level business rule violation where the
 * requested refund amount is greater than the original transaction value,
 * which is not allowed in the business logic.
 */
package k5.giftcard.domain.giftcard.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionAmountLessRefundException extends RuntimeException {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionAmountLessRefundException.class);
    
    /**
     * Default constructor
     */
    public TransactionAmountLessRefundException() {
        super("The original amount is less than the refund amount");
        logger.error("TransactionAmountLessRefundException thrown: The original amount is less than the refund amount");
    }
    
    /**
     * Constructor with custom message
     * 
     * @param message The error message
     */
    public TransactionAmountLessRefundException(String message) {
        super(message);
        logger.error("TransactionAmountLessRefundException thrown: {}", message);
    }
    
    /**
     * Constructor with custom message and cause
     * 
     * @param message The error message
     * @param cause The cause of the exception
     */
    public TransactionAmountLessRefundException(String message, Throwable cause) {
        super(message, cause);
        logger.error("TransactionAmountLessRefundException thrown: {}", message, cause);
    }
}

// Made with Bob
