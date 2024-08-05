package App.exception;

import org.springframework.http.HttpStatus;

public class TranslationException extends RuntimeException {
    private final HttpStatus status;

    public TranslationException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}