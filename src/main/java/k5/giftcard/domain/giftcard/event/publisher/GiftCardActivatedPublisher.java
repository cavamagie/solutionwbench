/**
 * GiftCardActivatedPublisher publishes gift card activation events to Kafka.
 *
 * This service encapsulates Kafka publishing concerns, including topic resolution,
 * message dispatching, and operational logging for distributed integrations.
 */
package k5.giftcard.domain.giftcard.event.publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import k5.giftcard.domain.giftcard.event.GiftCardActivated;

/**
 * Kafka publisher for {@link k5.giftcard.domain.giftcard.event.GiftCardActivated}.
 */
@Service
public class GiftCardActivatedPublisher {

    private static final Logger logger = LoggerFactory.getLogger(GiftCardActivatedPublisher.class);

    private final KafkaTemplate<String, GiftCardActivated> kafkaTemplate;
    private final String topicName;

    /**
     * Creates the publisher with injected Kafka infrastructure.
     *
     * @param kafkaTemplate Kafka template used to publish event payloads
     * @param topicName configured Kafka topic name for gift card activation events
     */
    public GiftCardActivatedPublisher(
            KafkaTemplate<String, GiftCardActivated> kafkaTemplate,
            @Value("${giftcard.kafka.topics.gift-card-activated:gift-card-activated}") String topicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.topicName = topicName;
    }

    /**
     * Publishes the provided gift card activation event to Kafka.
     *
     * @param event event payload to publish
     */
    public void publish(GiftCardActivated event) {
        logger.debug(
                "Publishing {} event to topic {} with payload: {}",
                GiftCardActivated.class.getSimpleName(),
                topicName,
                event);

        try {
            kafkaTemplate.send(topicName, event.getGiftcardId(), event);
            logger.info(
                    "{} event published to topic {} with key {}",
                    GiftCardActivated.class.getSimpleName(),
                    topicName,
                    event.getGiftcardId());
        } catch (Exception exception) {
            logger.error(
                    "Error publishing {} event to topic {}: {}",
                    GiftCardActivated.class.getSimpleName(),
                    topicName,
                    exception.getMessage(),
                    exception);
            throw exception;
        }
    }
}

// Made with Bob
