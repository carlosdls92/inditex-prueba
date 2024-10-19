package es.com.inditex.config.error;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Data
@JsonInclude(Include.NON_NULL)
public class FlashCustomError {

    @Getter(onMethod = @__( @JsonIgnore))
    private HttpStatus status;
    private String responseCode;
    private String message;
    private List<String> errors;

    public FlashCustomError(HttpStatus status, String responseCode, String message, List<String> errors) {
        super();
        this.status = status;
        this.responseCode = responseCode;
        this.message = message;
        Optional.ofNullable(errors).ifPresent(x -> this.errors = x);
    }

    public FlashCustomError(HttpStatus status, String responseCode, String message, String error) {
        super();
        this.status = status;
        this.responseCode = responseCode;
        this.message = message;
        Optional.ofNullable(error).ifPresent(x -> this.errors = Arrays.asList(x));
    }

    @Override
    @SneakyThrows
    public String toString() { return (new ObjectMapper()).writeValueAsString(this); }
}