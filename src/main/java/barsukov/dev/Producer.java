package barsukov.dev;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

class Producer {

    private final static String TOPIC             = "alerts";
    private final static String BOOTSTRAP_SERVERS =
            "localhost:2181,localhost:9092,localhost:9000";

    static void sendMessage(String msg) throws Exception {
        final org.apache.kafka.clients.producer.Producer producer = createProducer();
        long time = System.currentTimeMillis();
        try {
            final ProducerRecord<Long, String> record =
                    new ProducerRecord<>(TOPIC, msg);

            RecordMetadata metadata = (RecordMetadata) producer.send(record).get();

            long elapsedTime = System.currentTimeMillis() - time;

        } finally {
            producer.flush();
            producer.close();
        }
    }

    private static org.apache.kafka.clients.producer.Producer createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }
}
