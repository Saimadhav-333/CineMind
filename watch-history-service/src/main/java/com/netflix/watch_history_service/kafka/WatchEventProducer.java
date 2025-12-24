package com.netflix.watch_history_service.kafka;



import com.netflix.watch_history_service.event.WatchEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WatchEventProducer {

    private static final String TOPIC_NAME = "watch-events";

    private final KafkaTemplate<String, WatchEvent> kafkaTemplate;

    public WatchEventProducer(KafkaTemplate<String, WatchEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishWatchEvent(WatchEvent event) {
        kafkaTemplate.send(TOPIC_NAME, event.getUserId(), event);
    }
}

