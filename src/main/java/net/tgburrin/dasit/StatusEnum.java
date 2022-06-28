package net.tgburrin.dasit;

import java.util.stream.Stream;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusEnum {
	ACTIVE("A"),
	INACTIVE("I")
	;

	private final String s;
	StatusEnum(String v) {
		s = v;
	}

	public static StatusEnum getEnum(String value) {
		for(StatusEnum es : values())
			if(es.toString().equals(value)) return es;
		throw new IllegalArgumentException();
	}

	@Override
	public String toString() {
		return s;
	}

	@JsonCreator
	public static StatusEnum decode(final String code) {
		return Stream.of(StatusEnum.values()).filter(targetEnum -> targetEnum.s.equals(code)).findFirst().orElse(null);
	}

	@JsonValue
	public String getCode() {
		return s;
	}
}
