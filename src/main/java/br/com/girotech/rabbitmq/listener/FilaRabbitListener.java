package br.com.girotech.rabbitmq.listener;

import br.com.girotech.rabbitmq.exception.MensagemInvalidaException;
import br.com.girotech.rabbitmq.model.Usuario;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FilaRabbitListener {
    @RabbitListener(queues = "usuario", containerFactory = "usuarioFactory")
    public void receberMensagemFilaTeste(Message message) {
        String body = new String(message.getBody());
        log.info("Mensagem recebida: {}", body);

        if (message.getBody().length == 0) {
            String reason = "Mensagem inválida";
            log.error("FillaRabbitListener.receberMensagemFilaTeste: {}", reason);
            throw new MensagemInvalidaException(reason);
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            Usuario usuario = mapper.readValue(body, Usuario.class);
            log.info("FillaRabbitListener.receberMensagemFilaTeste: {}", usuario);
        } catch (Exception e) {
            log.error("FillaRabbitListener.receberMensagemFilaTeste: {}", e.getMessage());
            throw new MensagemInvalidaException("Erro ao validar mensagem.");
        }
    }
}
