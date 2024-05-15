package com.orchard.domain.ncp.core;

public enum NcpHeaders {
    API_GATEWAY_TIMESTAMP("x-ncp-apigw-timestamp"),
    IAM_ACCESS_KEY("x-ncp-iam-access-key"),
    API_GATEWAY_SIGNATURE("x-ncp-apigw-signature-v2");

    private String key;

    NcpHeaders(String key) {
        this.key = key;
    }

    public String get() {
        return key;
    }
}