package co.com.sofka.cargame.infra.bus;

import co.com.sofka.business.generic.BusinessException;
import co.com.sofka.cargame.infra.config.JuegoConfig;
import co.com.sofka.domain.generic.DomainEvent;
import co.com.sofka.infraestructure.bus.EventBus;
import co.com.sofka.infraestructure.bus.notification.ErrorNotification;
import co.com.sofka.infraestructure.bus.notification.SuccessNotification;
import co.com.sofka.infraestructure.bus.serialize.ErrorNotificationSerializer;
import co.com.sofka.infraestructure.bus.serialize.SuccessNotificationSerializer;
import co.com.sofka.infraestructure.event.ErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.logging.Level;

@Component
public class RabbitMQEventBus implements EventBus{

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQEventBus.class);
    private static final String TOPIC_ERROR = "cargame.error";
    private static final String TOPIC_BUSINESS_ERROR = "cargame.business.error";
    private final RabbitTemplate rabbitTemplate;
    private final MongoTemplate mongoTemplate;

    public RabbitMQEventBus(RabbitTemplate rabbitTemplate, MongoTemplate mongoTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void publish(DomainEvent event) {
        var notification = SuccessNotification.wrapEvent(JuegoConfig.ORIGIN, event);
        var notificationSerialization = SuccessNotificationSerializer.instance().serialize(notification);
        logger.info("Publicando: {}", notificationSerialization);
        rabbitTemplate.convertAndSend(JuegoConfig.ORIGIN, event.type, notificationSerialization.getBytes());
        mongoTemplate.save(event, event.type);
    }

    @Override
    public void publishError(ErrorEvent errorEvent) {

        if (errorEvent.error instanceof BusinessException) {
            publishToTopic(TOPIC_BUSINESS_ERROR, errorEvent);
        } else {
            publishToTopic(TOPIC_ERROR, errorEvent);
        }
        logger.info(errorEvent.error.getMessage());
    }

    public void publishToTopic(String topic, ErrorEvent errorEvent) {
        var notification = ErrorNotification.wrapEvent(JuegoConfig.ORIGIN, errorEvent);
        var notificationSerialization = ErrorNotificationSerializer.instance().serialize(notification);
        rabbitTemplate.convertAndSend(topic + "." + errorEvent.identify, notificationSerialization.getBytes());
        logger.info("###### Error Event published to " + topic);
    }
}
