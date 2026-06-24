/**
 * ErrorResponse DTO
 * 
 * Standard error response body for API errors.
 * Contains error code, message, type, and optional detailed error information.
 */
package k5.giftcard.api.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response containing error details")
public class ErrorResponse {
    
    @Schema(description = "Error code", example = "BALANCE_NOT_SUFFICIENT", required = true)
    private String code;
    
    @Schema(description = "Error message", example = "Balance not sufficient to process redemption", required = true)
    private String message;
    
    @Schema(description = "Detailed error message", example = "Current balance: 10.00, Requested amount: 25.00")
    private String detailMessage;
    
    @Schema(description = "Error type", example = "BusinessError")
    private String type;
    
    @Schema(description = "List of detailed error information")
    private List<ErrorDetail> errorDetails;
    
    /**
     * Default constructor
     */
    public ErrorResponse() {
        this.errorDetails = new ArrayList<>();
    }
    
    /**
     * Constructor with code and message
     * 
     * @param code Error code
     * @param message Error message
     */
    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.errorDetails = new ArrayList<>();
    }
    
    /**
     * Constructor with all parameters
     * 
     * @param code Error code
     * @param message Error message
     * @param detailMessage Detailed error message
     * @param type Error type
     */
    public ErrorResponse(String code, String message, String detailMessage, String type) {
        this.code = code;
        this.message = message;
        this.detailMessage = detailMessage;
        this.type = type;
        this.errorDetails = new ArrayList<>();
    }
    
    /**
     * Gets the error code
     * 
     * @return Error code
     */
    public String getCode() {
        return code;
    }
    
    /**
     * Sets the error code
     * 
     * @param code Error code
     */
    public void setCode(String code) {
        this.code = code;
    }
    
    /**
     * Gets the error message
     * 
     * @return Error message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Sets the error message
     * 
     * @param message Error message
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * Gets the detailed error message
     * 
     * @return Detailed error message
     */
    public String getDetailMessage() {
        return detailMessage;
    }
    
    /**
     * Sets the detailed error message
     * 
     * @param detailMessage Detailed error message
     */
    public void setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
    }
    
    /**
     * Gets the error type
     * 
     * @return Error type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Sets the error type
     * 
     * @param type Error type
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * Gets the list of error details
     * 
     * @return List of error details
     */
    public List<ErrorDetail> getErrorDetails() {
        return errorDetails;
    }
    
    /**
     * Sets the list of error details
     * 
     * @param errorDetails List of error details
     */
    public void setErrorDetails(List<ErrorDetail> errorDetails) {
        this.errorDetails = errorDetails;
    }
    
    /**
     * Adds an error detail to the list
     * 
     * @param errorDetail Error detail to add
     */
    public void addErrorDetail(ErrorDetail errorDetail) {
        if (this.errorDetails == null) {
            this.errorDetails = new ArrayList<>();
        }
        this.errorDetails.add(errorDetail);
    }
    
    /**
     * ErrorDetail inner class
     * 
     * Represents detailed error information for a specific field or location.
     */
    @Schema(description = "Detailed error information")
    public static class ErrorDetail {
        
        @Schema(description = "Error code for this specific detail", example = "INVALID_AMOUNT", required = true)
        private String code;
        
        @Schema(description = "Location of the error (e.g., field name)", example = "amount")
        private String location;
        
        @Schema(description = "Error message for this specific detail", example = "Amount must be positive", required = true)
        private String message;
        
        /**
         * Default constructor
         */
        public ErrorDetail() {
        }
        
        /**
         * Constructor with all parameters
         * 
         * @param code Error code
         * @param location Location of the error
         * @param message Error message
         */
        public ErrorDetail(String code, String location, String message) {
            this.code = code;
            this.location = location;
            this.message = message;
        }
        
        /**
         * Gets the error code
         * 
         * @return Error code
         */
        public String getCode() {
            return code;
        }
        
        /**
         * Sets the error code
         * 
         * @param code Error code
         */
        public void setCode(String code) {
            this.code = code;
        }
        
        /**
         * Gets the location
         * 
         * @return Location of the error
         */
        public String getLocation() {
            return location;
        }
        
        /**
         * Sets the location
         * 
         * @param location Location of the error
         */
        public void setLocation(String location) {
            this.location = location;
        }
        
        /**
         * Gets the error message
         * 
         * @return Error message
         */
        public String getMessage() {
            return message;
        }
        
        /**
         * Sets the error message
         * 
         * @param message Error message
         */
        public void setMessage(String message) {
            this.message = message;
        }
    }
}

// Made with Bob