package es.com.inditex.config.exception;

import lombok.Data;

@Data
public class CustomBaseRuntimeException extends RuntimeException {
    private String preText;
    public CustomBaseRuntimeException() {
        super();
    }
    public CustomBaseRuntimeException(String preText) {
        this.preText= preText;
    }
}
