package br.com.dsousasantos91.assembleia.exception;

public class GenericNotFoundException extends RuntimeException {

    private static final String MESSAGE = "Recurso não encontrado";

    public GenericNotFoundException() {
        super(MESSAGE);
    }

    public GenericNotFoundException(String message) {
        super(MESSAGE + ": " + message);
    }
}
