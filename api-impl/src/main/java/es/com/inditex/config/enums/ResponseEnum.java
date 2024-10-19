package es.com.inditex.config.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseEnum {
	SUCCESS(
			"00",
			"Operacion Exitosa",
			HttpStatus.OK),
	REQUEST_INVALIDO(
			"10",
			"Request inválido",
			HttpStatus.BAD_REQUEST),
	ERROR_INTERNO(
			"90",
			"Error Interno",
			HttpStatus.INTERNAL_SERVER_ERROR),

	EMPTY("","", HttpStatus.OK),
	;

	private String code;
	private String message;
	private HttpStatus status;
}
