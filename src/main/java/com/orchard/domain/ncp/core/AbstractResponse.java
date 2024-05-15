package com.orchard.domain.ncp.core;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class AbstractResponse {

	private int ncpStatusCode;
	private String ncpStatusMessage;

	private Throwable throwable;

	private Map<String, Object> data;

	public String getThrowable() {
		if(throwable == null) return null;
		return throwable.toString();
	}

	public String getData() {
		if(data == null) return null;
		return data.toString();
	}
}
