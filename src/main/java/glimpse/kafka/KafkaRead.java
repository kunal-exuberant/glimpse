/*
package glimpse.kafka;

import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.elasticsearch.ingest.Pipeline;

import java.util.Collection;

import static glimpse.kafka.KafkaProperties.bootstrapServers;
import static glimpse.kafka.KafkaProperties.topics;

public class KafkaRead {

    public Collection<BeamRecord> buildIOReader(Pipeline pipeline) {
        KafkaIO.Read<byte[], byte[]> kafkaRead = null;
        if (topics != null) {
            kafkaRead = KafkaIO.<byte[], byte[]>read()
                    .withBootstrapServers(bootstrapServers)
                    .withTopics(topics)
                    .updateConsumerProperties(configUpdates)
                    .withKeyDeserializerAndCoder(ByteArrayDeserializer.class, ByteArrayCoder.of())
                    .withValueDeserializerAndCoder(ByteArrayDeserializer.class, ByteArrayCoder.of());
        } else if (topicPartitions != null) {
            kafkaRead = KafkaIO.<byte[], byte[]>read()
                    .withBootstrapServers(bootstrapServers)
                    .withTopicPartitions(topicPartitions)
                    .updateConsumerProperties(configUpdates)
                    .withKeyDeserializerAndCoder(ByteArrayDeserializer.class, ByteArrayCoder.of())
                    .withValueDeserializerAndCoder(ByteArrayDeserializer.class, ByteArrayCoder.of());
        } else {
            throw new IllegalArgumentException("One of topics and topicPartitions must be configurated.");
        }

        return PBegin.in(pipeline).apply("read", kafkaRead.withoutMetadata())
                .apply("in_format", getPTransformForInput());
    }
}
*/
