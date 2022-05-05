package br.com.girotech.rabbitmq.controller;

import br.com.girotech.rabbitmq.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@Slf4j
public class UsuarioController {
    private final RabbitTemplate template;

    @PostMapping(value = "/enviar-usuario")
    public void enviarUsuario(@RequestBody Usuario usuario) {
        log.info("Enviando usuario: " + usuario);
        template.convertAndSend("girotech", "usuarioRoutingKey", usuario);
        log.info("Usuario enviado");
    }
}
