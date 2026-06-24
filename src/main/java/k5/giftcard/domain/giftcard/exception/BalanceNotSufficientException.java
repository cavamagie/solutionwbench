/**
 * BalanceNotSufficientException
 * 
 * Business error exception thrown when the gift card balance is not sufficient
 * to process a redemption operation.
 * 
 * This exception represents a domain-level business rule violation where the
 * requested redemption amount exceeds the available balance on the gift card.
 */
package k5.giftcard.domain.giftcard.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BalanceNotSufficientException extends RuntimeException {
    
    private static final Logger logger = LoggerFactory.getLogger(BalanceNotSufficientException.class);
    
    /**
     * Default constructor
     */
    public BalanceNotSufficientException() {
        super("Balance not sufficient to process redemption");
        logger.error("BalanceNotSufficientException thrown: Balance not sufficient to process redemption");
    }
    
    /**
     * Constructor with custom message
     * 
     * @param message The error message
     */
    public BalanceNotSufficientException(String message) {
        super(message);
        logger.error("BalanceNotSufficientException thrown: {}", message);
    }
    
    /**
     * Constructor with custom message and cause
     * 
     * @param message The error message
     * @param cause The cause of the exception
     */
    public BalanceNotSufficientException(String message, Throwable cause) {
        super(message, cause);
        logger.error("BalanceNotSufficientException thrown: {}", message, cause);
    }
}

// Made with Bob
