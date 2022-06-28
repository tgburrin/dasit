package net.tgburrin.dasit;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class StringToStatusEnumConverter implements Converter<String, StatusEnum> {

	@Override
	public StatusEnum convert(String source) {
		return StatusEnum.getEnum(source);
	}
}
