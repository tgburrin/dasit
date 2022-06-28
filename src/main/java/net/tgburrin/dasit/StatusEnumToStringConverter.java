package net.tgburrin.dasit;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class StatusEnumToStringConverter implements Converter<StatusEnum, String> {

	@Override
	public String convert(StatusEnum source) {
		return source.toString();
	}

}
