package br.com.girotech.rabbitmq.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.util.ErrorHandler;

@Slf4j
public class RabbitMQCustomErrorHandler  implements ErrorHandler {
    @Override
    public void handleError(final Throwable t) {
        log.error(t.getCause().getMessage());
        throw new AmqpRejectAndDontRequeueException("Error Handler converteu exception para fatal", t);
    }
}
