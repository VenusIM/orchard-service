package com.orchard.domain.ncp.core;

import groovy.transform.ToString;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@ToString
@Getter
@EqualsAndHashCode(callSuper = true)
public class SmsResponse extends AbstractResponse{
    private String requestId;
    private String requestTime;
    private String statusCode;
    private String statusName;
}
