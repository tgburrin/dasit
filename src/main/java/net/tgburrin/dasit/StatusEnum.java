package net.tgburrin.dasit;

public enum StatusEnum {
	ACTIVE("A"),
	INACTIVE("I")
	;

	private String s;

	StatusEnum(String s) {
		this.s = s;
	}

	public static StatusEnum getEnum(String value) {
		for(StatusEnum es : values())
			if(es.toString().equals(value)) return es;
		throw new IllegalArgumentException();
	}
}
