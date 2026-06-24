/**
 * GiftCardActivatedConsumer consumes gift card activation events from Kafka.
 *
 * This service listens for activation events, logs the received payload, and
 * provides the integration point for downstream asynchronous business logic.
 */
package k5.giftcard.domain.giftcard.event.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import k5.giftcard.domain.giftcard.event.GiftCardActivated;

/**
 * Kafka consumer for {@link k5.giftcard.domain.giftcard.event.GiftCardActivated}.
 */
@Service
public class GiftCardActivatedConsumer {

    private static final Logger logger = LoggerFactory.getLogger(GiftCardActivatedConsumer.class);

    private final String topicName;

    /**
     * Creates the consumer with the configured topic name.
     *
     * @param topicName configured Kafka topic name for gift card activation events
     */
    public GiftCardActivatedConsumer(
            @Value("${giftcard.kafka.topics.gift-card-activated:gift-card-activated}") String topicName) {
        this.topicName = topicName;
    }

    /**
     * Handles incoming gift card activation events from Kafka.
     *
     * @param event deserialized gift card activation event payload
     */
    @KafkaListener(
            topics = "${giftcard.kafka.topics.gift-card-activated:gift-card-activated}",
            groupId = "${giftcard.kafka.consumer.group-id:giftcard-application}")
    public void consume(GiftCardActivated event) {
        logger.debug(
                "Received {} event from topic {} with payload: {}",
                GiftCardActivated.class.getSimpleName(),
                topicName,
                event);

        try {
            logger.info(
                    "{} event from topic {} processed successfully",
                    GiftCardActivated.class.getSimpleName(),
                    topicName);
        } catch (Exception exception) {
            logger.error(
                    "Error processing {} event from topic {}: {}",
                    GiftCardActivated.class.getSimpleName(),
                    topicName,
                    exception.getMessage(),
                    exception);
            throw exception;
        }
    }
}

// Made with Bob
