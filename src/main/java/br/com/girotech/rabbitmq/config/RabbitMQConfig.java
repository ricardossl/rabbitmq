package br.com.girotech.rabbitmq.config;

import br.com.girotech.rabbitmq.exception.RabbitMQCustomErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.util.ErrorHandler;

@Configuration
@Slf4j
public class RabbitMQConfig {
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${spring.rabbitmq.virtualhost}")
    private String virtualhost;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;

    @Bean
    public ConnectionFactory connectionFactory() {
        log.info("Conectando com RabbitMq em {}:{}", this.host, this.port);
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(this.host, this.port); // Não é necessário criar uma conexão para cada listener
        cachingConnectionFactory.setVirtualHost(this.virtualhost);
        cachingConnectionFactory.setUsername(this.username);
        cachingConnectionFactory.setPassword(this.password);
        return cachingConnectionFactory;
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new RabbitMQCustomErrorHandler();
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    } //The RabbitAdmin component can declare exchanges, queues and bindings on startup.
    // It does this lazily, through a ConnectionListener, so if the broker is not present on startup it doesn't matter.

    @Bean
    public RabbitTemplate rabbitTemplate() {
        var rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean("usuarioFactory")
    public SimpleRabbitListenerContainerFactory usuarioFactory() {
        return this.generateContainerFactory(new SimpleRabbitListenerContainerFactory());
    }

    private SimpleRabbitListenerContainerFactory generateContainerFactory(SimpleRabbitListenerContainerFactory factory) {
        factory.setBatchListener(false);
        factory.setConsumerBatchEnabled(false);
        factory.setDefaultRequeueRejected(false);
        factory.setConnectionFactory(connectionFactory());
        factory.setAdviceChain(retries());
        factory.setErrorHandler(errorHandler());
        return factory;
    }

    @Bean
    public RetryOperationsInterceptor retries() {
        return RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .backOffOptions(5000, 3.0, 30000)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }
}
