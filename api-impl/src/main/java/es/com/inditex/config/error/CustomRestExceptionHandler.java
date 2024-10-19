package es.com.inditex.config.error;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import es.com.inditex.config.enums.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler{

    private static final String GENERIC_ERROR_MESSAGE = "Error Generico";

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers,
                                                         HttpStatus status, WebRequest request) {
        FlashCustomError flashCustomError = builderFlashCustomError(
                        ResponseEnum.REQUEST_INVALIDO,
                        "Los campos del request no son válidos",
                        getErrors(ex.getBindingResult().getFieldErrors(), ex.getBindingResult().getGlobalErrors()).get(0));
        return handleExceptionInternal(ex, flashCustomError, headers, flashCustomError.getStatus(), request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatus status, WebRequest request) {
        FlashCustomError flashCustomError = builderFlashCustomError(
                ResponseEnum.REQUEST_INVALIDO,
                "Los campos del request no son válidos",
                getErrors(ex.getBindingResult().getFieldErrors(), ex.getBindingResult().getGlobalErrors()));
        return handleExceptionInternal(ex, flashCustomError, headers, flashCustomError.getStatus(), request);
    }

    private List<String> getErrors(List<FieldError> fieldErrors, List<ObjectError> objectErrors) {
        List<String> errors = new ArrayList<>();
        fieldErrors.forEach(fieldError -> errors.add(fieldError.getField() + ": " + fieldError.getDefaultMessage()));
        objectErrors.forEach(objectError -> errors.add(objectError.getObjectName() + ": " + objectError.getDefaultMessage()));
        return errors;
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        String message = ex.getParameterName() + " falta el parametro";

        FlashCustomError flashCustomError = builderFlashCustomError(
                ResponseEnum.REQUEST_INVALIDO,
                message,
                ex.getLocalizedMessage());
        return new ResponseEntity<>(flashCustomError, new HttpHeaders(), flashCustomError.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" método no es compatible con este request. Los métodos admitidos son: ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

        FlashCustomError flashCustomError = new FlashCustomError(
                HttpStatus.METHOD_NOT_ALLOWED,
                ResponseEnum.REQUEST_INVALIDO.getCode(),
                builder.toString(),
                ex.getLocalizedMessage());
        return new ResponseEntity<>(flashCustomError, new HttpHeaders(), flashCustomError.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        FlashCustomError flashCustomError = new FlashCustomError(
                HttpStatus.METHOD_NOT_ALLOWED,
                ResponseEnum.REQUEST_INVALIDO.getCode(),
                "Metodo no encontrado",
                ex.getLocalizedMessage()
                );

        return new ResponseEntity<>(flashCustomError, new HttpHeaders(), flashCustomError.getStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        final Throwable cause = ex.getCause();

        if(cause instanceof InvalidFormatException){
            return this.handleInvalidFormatException((InvalidFormatException) cause, status, request);
        }else if(cause instanceof JsonParseException){
            return this.handleJsonParseException((JsonParseException) cause, status, request);
        }else if(cause instanceof JsonMappingException){
            return newResponse(this.handleJsonMappingException((JsonMappingException) cause, status), HttpStatus.BAD_REQUEST);
        }

        String error = "Uh oh, recibimos un request vacío, vuelve a verificar";

        FlashCustomError flashCustomError = builderFlashCustomError(
                ResponseEnum.REQUEST_INVALIDO,
                "El RequestBody no esta presente",
                error);
        return new ResponseEntity<>(flashCustomError, new HttpHeaders(), flashCustomError.getStatus());
    }

    private <T> ResponseEntity<T> newResponse(T obj, HttpStatus status){
        return new ResponseEntity<>(obj, new HttpHeaders(), status);
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpStatus status, WebRequest request) {
        String error = ex.getName() + " debe ser de tipo " + ex.getRequiredType().getName();

        FlashCustomError flashCustomError = builderFlashCustomError(
                ResponseEnum.REQUEST_INVALIDO,
                error,
                ex.getLocalizedMessage());
        return new ResponseEntity<>(flashCustomError, new HttpHeaders(), flashCustomError.getStatus());
    }

    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolation(
            ConstraintViolationException ex, HttpStatus status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " +
                    violation.getPropertyPath() + ": " + violation.getMessage());
        }

        FlashCustomError flashCustomError = builderFlashCustomError(
                ResponseEnum.REQUEST_INVALIDO,
                ex.getLocalizedMessage(),
                errors.get(0));
        return new ResponseEntity<>(flashCustomError, new HttpHeaders(), flashCustomError.getStatus());
    }

    @ExceptionHandler({ InvalidFormatException.class })
    public ResponseEntity<Object> handleInvalidFormatException(
            InvalidFormatException ex, HttpStatus status, WebRequest request) {
        String error = "El valor '" + ex.getValue() + "' debe ser de tipo " + ex.getTargetType().getName();

        FlashCustomError flashCustomError = builderFlashCustomError(
                ResponseEnum.REQUEST_INVALIDO,
                GENERIC_ERROR_MESSAGE,
                error);
        return new ResponseEntity<>(flashCustomError, new HttpHeaders(), flashCustomError.getStatus());
    }

    @ExceptionHandler({ JsonParseException.class })
    public ResponseEntity<Object> handleJsonParseException(
            JsonParseException ex, HttpStatus status, WebRequest request){

        String error = null;
        try {
            error = "Hubo un error cerca del token '" + ex.getProcessor().getCurrentName() + "' compruébelo por favor.";
        } catch (IOException e) {
            logger.error("IOOException", e);
        }

        FlashCustomError flashCustomError = builderFlashCustomError(
                ResponseEnum.REQUEST_INVALIDO,
                GENERIC_ERROR_MESSAGE,
                error);
        return new ResponseEntity<>(flashCustomError, new HttpHeaders(), flashCustomError.getStatus());
    }

    @ExceptionHandler({ JsonMappingException.class })
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public FlashCustomError handleJsonMappingException(JsonMappingException ex, HttpStatus status){
        String error = "Hubo un error cerca del token '" + ex.getPathReference().split("\"")[1] + "' compruébelo por favor.";
        return builderFlashCustomError(
                ResponseEnum.REQUEST_INVALIDO,
                GENERIC_ERROR_MESSAGE,
                error);
    }

    private FlashCustomError builderFlashCustomError(ResponseEnum responseEnum, RuntimeException ex){
        return new FlashCustomError(
                responseEnum.getStatus(),
                responseEnum.getCode(),
                responseEnum.getMessage(),
                ex.getMessage());
    }

    private FlashCustomError builderFlashCustomError(ResponseEnum responseEnum, String message, String error){
        return new FlashCustomError(
                responseEnum.getStatus(),
                responseEnum.getCode(),
                message,
                error);
    }

    private FlashCustomError builderFlashCustomError(ResponseEnum responseEnum, String message, List<String> errors){
        return new FlashCustomError(
                responseEnum.getStatus(),
                responseEnum.getCode(),
                message,
                errors);
    }

    private FlashCustomError builderFlashCustomError(ResponseEnum responseEnum, String str){
        return new FlashCustomError(
                responseEnum.getStatus(),
                responseEnum.getCode(),
                str,
                responseEnum.getMessage());
    }

}
