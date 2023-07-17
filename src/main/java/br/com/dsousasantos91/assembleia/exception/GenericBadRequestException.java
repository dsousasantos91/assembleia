package br.com.dsousasantos91.assembleia.exception;

public class GenericBadRequestException extends RuntimeException {

    private static final String MESSAGE = "Requisição inválida";

    public GenericBadRequestException() {
        super(MESSAGE);
    }

    public GenericBadRequestException(String message) {
        super(MESSAGE + ": " + message);
    }
}
