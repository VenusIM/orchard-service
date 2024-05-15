package com.orchard.domain.ncp.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.json.simple.JSONObject;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class NcpCommonResponse extends AbstractResponse {

	public String requestId;
	public String requestTime;
	public String statusCode;
	public String statusName;

	public JSONObject failover;
	public List<JSONObject> messages;

}
