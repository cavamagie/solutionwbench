/**
 * KafkaConfig configures Kafka producer and consumer infrastructure for domain events.
 *
 * This configuration externalizes broker and security settings, enables Kafka listeners,
 * and provides typed producer and consumer factories for gift card event integration.
 */
package k5.giftcard.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import k5.giftcard.domain.giftcard.event.GiftCardActivated;

/**
 * Spring configuration for Kafka producer and consumer beans.
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    private final String bootstrapServers;
    private final String securityProtocol;
    private final String saslMechanism;
    private final String saslJaasConfig;
    private final String sslEndpointIdentificationAlgorithm;
    private final String consumerGroupId;

    /**
     * Creates the Kafka configuration with externalized connection and security properties.
     *
     * @param bootstrapServers Kafka bootstrap server list
     * @param securityProtocol Kafka security protocol
     * @param saslMechanism Kafka SASL mechanism
     * @param saslJaasConfig Kafka SASL JAAS configuration
     * @param sslEndpointIdentificationAlgorithm Kafka SSL endpoint identification algorithm
     * @param consumerGroupId Kafka consumer group identifier
     */
    public KafkaConfig(
            @Value("${spring.kafka.bootstrap-servers:localhost:9092}") String bootstrapServers,
            @Value("${spring.kafka.security.protocol:PLAINTEXT}") String securityProtocol,
            @Value("${spring.kafka.properties.sasl.mechanism:}") String saslMechanism,
            @Value("${spring.kafka.properties.sasl.jaas.config:}") String saslJaasConfig,
            @Value("${spring.kafka.properties.ssl.endpoint.identification.algorithm:https}") String sslEndpointIdentificationAlgorithm,
            @Value("${giftcard.kafka.consumer.group-id:giftcard-application}") String consumerGroupId) {
        this.bootstrapServers = bootstrapServers;
        this.securityProtocol = securityProtocol;
        this.saslMechanism = saslMechanism;
        this.saslJaasConfig = saslJaasConfig;
        this.sslEndpointIdentificationAlgorithm = sslEndpointIdentificationAlgorithm;
        this.consumerGroupId = consumerGroupId;
    }

    /**
     * Creates the Kafka producer factory for gift card activation events.
     *
     * @return producer factory configured for JSON event publishing
     */
    @Bean
    public ProducerFactory<String, GiftCardActivated> giftCardActivatedProducerFactory() {
        return new DefaultKafkaProducerFactory<>(buildCommonKafkaPropertiesWithProducerSettings());
    }

    /**
     * Creates the Kafka template used by event publishers.
     *
     * @return Kafka template for gift card activation events
     */
    @Bean
    public KafkaTemplate<String, GiftCardActivated> giftCardActivatedKafkaTemplate() {
        return new KafkaTemplate<>(giftCardActivatedProducerFactory());
    }

    /**
     * Creates the Kafka consumer factory for gift card activation events.
     *
     * @return consumer factory configured for JSON event consumption
     */
    @Bean
    public ConsumerFactory<String, GiftCardActivated> giftCardActivatedConsumerFactory() {
        JsonDeserializer<GiftCardActivated> jsonDeserializer = new JsonDeserializer<>(GiftCardActivated.class);
        jsonDeserializer.addTrustedPackages("k5.giftcard.domain.giftcard.event");
        jsonDeserializer.setUseTypeHeaders(false);

        Map<String, Object> consumerProperties = buildCommonKafkaPropertiesWithConsumerSettings();
        return new DefaultKafkaConsumerFactory<>(consumerProperties, new StringDeserializer(), jsonDeserializer);
    }

    /**
     * Creates the Kafka listener container factory used by Kafka listeners.
     *
     * @return listener container factory for gift card activation events
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, GiftCardActivated> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, GiftCardActivated> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(giftCardActivatedConsumerFactory());
        return factory;
    }

    /**
     * Builds the common Kafka properties and producer-specific serializer settings.
     *
     * @return producer configuration properties
     */
    private Map<String, Object> buildCommonKafkaPropertiesWithProducerSettings() {
        Map<String, Object> properties = buildCommonKafkaProperties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return properties;
    }

    /**
     * Builds the common Kafka properties and consumer-specific deserializer settings.
     *
     * @return consumer configuration properties
     */
    private Map<String, Object> buildCommonKafkaPropertiesWithConsumerSettings() {
        Map<String, Object> properties = buildCommonKafkaProperties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return properties;
    }

    /**
     * Builds Kafka properties shared by producers and consumers, including security settings.
     *
     * @return common Kafka client properties
     */
    private Map<String, Object> buildCommonKafkaProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        properties.put("sasl.mechanism", saslMechanism);
        properties.put("sasl.jaas.config", saslJaasConfig);
        properties.put("ssl.endpoint.identification.algorithm", sslEndpointIdentificationAlgorithm);
        return properties;
    }
}

// Made with Bob
