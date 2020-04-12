package glimpse.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import glimpse.ESOperations;
import glimpse.RedisOperations;
import glimpse.models.Destination;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class KafkaConsumerService {

    public static Properties getConsumerProperties(KafkaConfiguration kafkaConfiguration){
        Properties properties = new Properties();
        properties.put(KafkaProperties.bootstrapServers, kafkaConfiguration.getBootstrapServers());
        properties.put(KafkaProperties.enableAutoCommit, kafkaConfiguration.getEnableAutoCommit());
        properties.put(KafkaProperties.autoCommitInterval, kafkaConfiguration.getAutoCommitInterval());
        properties.put(KafkaProperties.sessionTimeout, kafkaConfiguration.getSessionTimeout());
        properties.put(KafkaProperties.keyDeserializer, kafkaConfiguration.getKeyDeserializer());
        properties.put(KafkaProperties.valueDeserializer, kafkaConfiguration.getValueDeserializer());
        properties.put(KafkaProperties.groupId, kafkaConfiguration.getGroupId());
        properties.put(KafkaProperties.heartbeatInterval, kafkaConfiguration.getHeartbeatInterval());
        properties.put(KafkaProperties.groupMinSessionTimeout, kafkaConfiguration.getGroupMinSessionTimeout());
        properties.put(KafkaProperties.groupMaxSessionTimeout, kafkaConfiguration.getGroupMaxSessionTimeout());
        return properties;
    }

    public static KafkaConsumer create(KafkaConfiguration kafkaConfiguration) {

        KafkaConsumer kafkaConsumer = new KafkaConsumer(getConsumerProperties(kafkaConfiguration));
        kafkaConsumer.subscribe(kafkaConfiguration.getTopics());
        return kafkaConsumer;
    }

    public static void fetchRecords(KafkaConsumer kafkaConsumer, KafkaConfiguration kafkaConfiguration){

        while (true){
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(3000);
            log.info("Fetched {} consumer record ",consumerRecords.count());
            consumerRecords.forEach(record ->{
                System.out.println("Fetched records"+record.value());
                try {
                    writeToElasticSearch(record.key(), record.value());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void writeToElasticSearch(String messageKey, String messageValue) throws IOException {

        Destination destination = new ObjectMapper().readValue(messageValue, Destination.class);
        ESOperations.getClient();
        if (ESOperations.getDocumentById(destination.getName()) == null) {
            ESOperations.addDocument(destination.getName(), messageValue);
            RedisOperations.saveDestinationId();
        } else {
            System.out.println("Error: REPEAT, destination already exists: " + destination.getName());
        }
    }

    public static void main(String[] args) throws IOException {
        KafkaConfiguration kafkaConfiguration = KafkaProperties.loadConfiguration();
        KafkaConsumer kafkaConsumer = create(kafkaConfiguration);
        fetchRecords(kafkaConsumer, kafkaConfiguration);
    }
}
