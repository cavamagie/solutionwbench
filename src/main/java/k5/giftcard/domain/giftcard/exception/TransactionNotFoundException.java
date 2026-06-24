/**
 * TransactionNotFoundException
 * 
 * Business error exception thrown when a transaction cannot be found by its
 * order reference or transaction ID.
 * 
 * This exception represents a domain-level business rule violation where the
 * requested transaction does not exist in the gift card's transaction history.
 */
package k5.giftcard.domain.giftcard.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionNotFoundException extends RuntimeException {
    
    private static final Logger logger = LoggerFactory.getLogger(TransactionNotFoundException.class);
    
    /**
     * Default constructor
     */
    public TransactionNotFoundException() {
        super("Transaction could not be found");
        logger.error("TransactionNotFoundException thrown: Transaction could not be found");
    }
    
    /**
     * Constructor with custom message
     * 
     * @param message The error message
     */
    public TransactionNotFoundException(String message) {
        super(message);
        logger.error("TransactionNotFoundException thrown: {}", message);
    }
    
    /**
     * Constructor with custom message and cause
     * 
     * @param message The error message
     * @param cause The cause of the exception
     */
    public TransactionNotFoundException(String message, Throwable cause) {
        super(message, cause);
        logger.error("TransactionNotFoundException thrown: {}", message, cause);
    }
}

// Made with Bob
