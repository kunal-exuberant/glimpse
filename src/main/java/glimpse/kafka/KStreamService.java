package glimpse.kafka;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

import java.io.IOException;
import java.util.Properties;

public class KStreamService {


    public static void create(KafkaConfiguration kafkaConfiguration){

        Properties properties = new Properties();
        properties.put(KafkaProperties.bootstrapServers, kafkaConfiguration.getBootstrapServers());
        properties.put(KafkaProperties.keyDeserializer, kafkaConfiguration.getValueDeserializer());
        properties.put(KafkaProperties.valueDeserializer, kafkaConfiguration.getValueDeserializer());
        //properties.put(KafkaProperties.topics, kafkaConfiguration.getTopics());
        properties.put(KafkaProperties.applicationId, kafkaConfiguration.getApplicationId());

        KStreamBuilder kStreamBuilder = new KStreamBuilder();
        KStream<String[], String[]> textLines = null;
        kStreamBuilder.stream("KStreamTest").foreach((x,y)->{
            System.out.println("Printing the stream output");
            System.out.println(x.toString());
            System.out.println(y.toString());
        });
/* *//*       Pattern pattern = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS);
        KTable<String, Long> wordCounts = textLines
                .flatMapValues(value -> Arrays.asList(pattern.split(Arrays.toString(value).toLowerCase())))
                .groupBy((key, word) -> word)
                .count();*//*

        wordCounts.foreach((a,b) ->{
            //System.out.println(b);
        });*/
        KafkaStreams streams = new KafkaStreams(kStreamBuilder, properties);
        streams.start();
        System.out.println("Stream received");
    }


    public static void main(String[] args) throws IOException {
        create(KafkaProperties.loadConfiguration());
    }
}
