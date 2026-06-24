/**
 * CommandConfiguration
 * 
 * Spring configuration class for registering domain commands as prototype beans.
 * Commands are registered as prototype-scoped beans to ensure a new instance
 * is created for each request, maintaining statelessness and thread safety.
 */
package k5.giftcard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import k5.giftcard.domain.giftcard.command.ActivateCommand;
import k5.giftcard.domain.giftcard.command.RedeemCommand;
import k5.giftcard.domain.giftcard.command.RefundCommand;

/**
 * Configuration class that defines command beans with prototype scope.
 * Prototype scope ensures that each time a command is requested from the BeanFactory,
 * a new instance is created, preventing state sharing between requests.
 */
@Configuration
public class CommandConfiguration {
    
    /**
     * Creates a prototype-scoped bean for ActivateCommand.
     * Each request for this bean will create a new instance.
     * 
     * @return A new ActivateCommand instance
     */
    @Bean
    @Scope("prototype")
    public ActivateCommand activateCommand() {
        return new ActivateCommand();
    }
    
    /**
     * Creates a prototype-scoped bean for RedeemCommand.
     * Each request for this bean will create a new instance.
     * 
     * @return A new RedeemCommand instance
     */
    @Bean
    @Scope("prototype")
    public RedeemCommand redeemCommand() {
        return new RedeemCommand();
    }
    
    /**
     * Creates a prototype-scoped bean for RefundCommand.
     * Each request for this bean will create a new instance.
     * 
     * @return A new RefundCommand instance
     */
    @Bean
    @Scope("prototype")
    public RefundCommand refundCommand() {
        return new RefundCommand();
    }
}

// Made with Bob