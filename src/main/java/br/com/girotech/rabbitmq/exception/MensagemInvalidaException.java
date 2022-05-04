package br.com.girotech.rabbitmq.exception;

public class MensagemInvalidaException extends RuntimeException {
    public MensagemInvalidaException(String mensagem) {
        super(mensagem);
    }
}
