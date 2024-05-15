package com.orchard.domain.ncp.core;

import org.springframework.lang.Nullable;

public enum NcpErrorStatus {

	READ_TIMEOUT(900, "read time out"),
	NO_HTTP_RESPONSE(901, "failed to respond")
	;

	private final int value;

	private final String reasonPhrase;

	NcpErrorStatus(int value, String reasonPhrase) {
		this.value = value;
		this.reasonPhrase = reasonPhrase;
	}

	public int value() {
		return this.value;
	}

	public String getReasonPhrase() {
		return this.reasonPhrase;
	}

	public static NcpErrorStatus valueOf(int statusCode) {
		NcpErrorStatus status = resolve(statusCode);
		if (status == null) {
			throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
		}
		return status;
	}

	@Nullable
	public static NcpErrorStatus resolve(int statusCode) {
		for (NcpErrorStatus status : values()) {
			if (status.value == statusCode) {
				return status;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return this.value + " " + name();
	}

}
