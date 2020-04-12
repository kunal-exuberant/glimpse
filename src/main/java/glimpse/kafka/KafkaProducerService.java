package glimpse.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Slf4j
public class KafkaProducerService {

    private static KafkaConfiguration kafkaConfiguration;

    static {
        try {
            kafkaConfiguration = KafkaProperties.loadConfiguration();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static KafkaProducer kafkaProducer = create(kafkaConfiguration);

    public static Properties getProducerProperties(KafkaConfiguration kafkaConfiguration){
        Properties properties = new Properties();
        properties.put(KafkaProperties.bootstrapServers, kafkaConfiguration.getBootstrapServers());
        properties.put(KafkaProperties.linger, kafkaConfiguration.getLinger());
        properties.put(KafkaProperties.retries, 0);
        properties.put(KafkaProperties.keySerializer, kafkaConfiguration.getKeySerializer());
        properties.put(KafkaProperties.valueSerializer, kafkaConfiguration.getValueSerializer());
        return properties;
    }

    public static KafkaProducer create(KafkaConfiguration kafkaConfiguration){
        KafkaProducer kafkaProducer = new KafkaProducer(getProducerProperties(kafkaConfiguration));
        return kafkaProducer;
    }

    public static void sendRecords(String messageKey, String messageValue) {
        log.info("Sending {} to Kafka topic {}",messageValue, kafkaConfiguration.getTopics().get(0));
        List<String> topics = kafkaConfiguration.getTopics();
        ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(topics.get(0), messageKey, messageValue);
        kafkaProducer.send(producerRecord);
        //kafkaProducer.close();
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        sendRecords("kafkaIntroduction", "HelloKafka");
    }
}
