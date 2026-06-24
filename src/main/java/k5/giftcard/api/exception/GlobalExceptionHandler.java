/**
 * GlobalExceptionHandler
 * 
 * Global exception handler for the REST API.
 * Catches domain exceptions and converts them to appropriate HTTP error responses.
 * Handles validation errors, business errors, and general exceptions.
 */
package k5.giftcard.api.exception;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import k5.giftcard.api.controller.GiftcardController.GiftcardNotFoundException;
import k5.giftcard.api.dto.ErrorResponse;
import k5.giftcard.api.dto.ErrorResponse.ErrorDetail;
import k5.giftcard.domain.giftcard.exception.BalanceNotSufficientException;
import k5.giftcard.domain.giftcard.exception.TransactionAmountLessRefundException;
import k5.giftcard.domain.giftcard.exception.TransactionNotFoundException;

/**
 * Global exception handler that intercepts exceptions thrown by REST controllers
 * and converts them into appropriate HTTP error responses with standardized error format.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Handles GiftcardNotFoundException
     * Returns HTTP 404 Not Found
     * 
     * @param ex The exception
     * @return ResponseEntity with error details and 404 status
     */
    @ExceptionHandler(GiftcardNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGiftcardNotFoundException(GiftcardNotFoundException ex) {
        logger.warn("Gift card not found: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            "GIFTCARD_NOT_FOUND",
            "Gift card not found",
            ex.getMessage(),
            "NotFoundError"
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Handles BalanceNotSufficientException
     * Returns HTTP 400 Bad Request
     * 
     * @param ex The exception
     * @return ResponseEntity with error details and 400 status
     */
    @ExceptionHandler(BalanceNotSufficientException.class)
    public ResponseEntity<ErrorResponse> handleBalanceNotSufficientException(BalanceNotSufficientException ex) {
        logger.warn("Balance not sufficient: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            "BALANCE_NOT_SUFFICIENT",
            "Balance not sufficient to process redemption",
            ex.getMessage(),
            "BusinessError"
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handles TransactionNotFoundException
     * Returns HTTP 400 Bad Request
     * 
     * @param ex The exception
     * @return ResponseEntity with error details and 400 status
     */
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTransactionNotFoundException(TransactionNotFoundException ex) {
        logger.warn("Transaction not found: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            "TRANSACTION_NOT_FOUND",
            "Transaction not found for the specified order reference",
            ex.getMessage(),
            "BusinessError"
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handles TransactionAmountLessRefundException
     * Returns HTTP 400 Bad Request
     * 
     * @param ex The exception
     * @return ResponseEntity with error details and 400 status
     */
    @ExceptionHandler(TransactionAmountLessRefundException.class)
    public ResponseEntity<ErrorResponse> handleTransactionAmountLessRefundException(TransactionAmountLessRefundException ex) {
        logger.warn("Refund amount exceeds original transaction: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            "REFUND_AMOUNT_EXCEEDS_ORIGINAL",
            "Refund amount exceeds the original transaction amount",
            ex.getMessage(),
            "BusinessError"
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handles validation errors from @Valid annotations
     * Returns HTTP 400 Bad Request
     * 
     * @param ex The exception
     * @return ResponseEntity with validation error details and 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        logger.warn("Validation error: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            "VALIDATION_ERROR",
            "Request validation failed",
            "One or more fields have validation errors",
            "ValidationError"
        );
        
        // Add detailed error information for each field
        ex.getBindingResult().getAllErrors().forEach(objectError -> {
            String fieldName = objectError instanceof FieldError 
                ? ((FieldError) objectError).getField() 
                : objectError.getObjectName();
            String message = objectError.getDefaultMessage();
            
            error.addErrorDetail(new ErrorDetail(
                "INVALID_FIELD",
                fieldName,
                message
            ));
        });
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handles JWT authentication errors
     * Returns HTTP 401 Unauthorized
     * 
     * @param ex The exception
     * @return ResponseEntity with error details and 401 status
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex) {
        logger.error("JWT authentication error: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            "AUTHENTICATION_FAILED",
            "Authentication failed",
            "Invalid or expired token",
            "AuthenticationError"
        );
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    /**
     * Handles access denied errors
     * Returns HTTP 403 Forbidden
     * 
     * @param ex The exception
     * @return ResponseEntity with error details and 403 status
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        logger.error("Access denied: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            "ACCESS_DENIED",
            "Access denied",
            "You do not have permission to access this resource",
            "AuthorizationError"
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
    
    /**
     * Handles all other unhandled exceptions
     * Returns HTTP 500 Internal Server Error
     * 
     * @param ex The exception
     * @return ResponseEntity with error details and 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "An unexpected error occurred",
            ex.getMessage(),
            "ServerError"
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

// Made with Bob