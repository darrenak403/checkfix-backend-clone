package ttldd.monitoringservice.config;

import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import ttldd.monitoringservice.dto.ApiLogDTO;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ttldd.monitoringservice.dto.IamLogDTO;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // Factory cho API logs
    @Bean
    public ConsumerFactory<String, ApiLogDTO> apiLogConsumerFactory() {
        JsonDeserializer<ApiLogDTO> deserializer = new JsonDeserializer<>(ApiLogDTO.class, false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "monitoring-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(deserializer)
        );
    }

    // Factory cho IAM logs
    @Bean
    public ConsumerFactory<String, IamLogDTO> iamLogConsumerFactory() {
        JsonDeserializer<IamLogDTO> deserializer = new JsonDeserializer<>(IamLogDTO.class, false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);
        deserializer.setRemoveTypeHeaders(true);
        deserializer.setUseTypeHeaders(false);

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "monitoring-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new ErrorHandlingDeserializer<>(deserializer)
        );
    }

    // Listener cho API logs
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ApiLogDTO> apiLogKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ApiLogDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(apiLogConsumerFactory());
        return factory;
    }

    // Listener cho IAM logs
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, IamLogDTO> iamLogKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, IamLogDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(iamLogConsumerFactory());
        return factory;
    }
}