package net.tgburrin.dasit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidDataException extends Exception {
	public InvalidDataException(String message) {
		super(message);
	}
	
	public InvalidDataException(String message, Throwable err) {
		super(message, err);
	}
}