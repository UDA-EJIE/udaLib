package com.ejie.x38.serialization;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class BigDecimalTestObject {
	
	
	private BigDecimal prop = null;
	
	@JsonSerialize(using = JsonBigDecimalSerializer.class)
	public BigDecimal getProp() {
		return this.prop;
	}
	
	@JsonDeserialize(using = JsonBigDecimalDeserializer.class)
	public void setProp(BigDecimal bigDecimal) {
		this.prop = bigDecimal;
	}
}
