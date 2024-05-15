package com.orchard.domain.ncp.core;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@ToString
@Getter
@Component
@NoArgsConstructor
public class NcpApiOption {

    private String path;
    private String timestamp;
    private HttpMethod method;
    private MediaType mediaType;
    private String contentType;
    private String signatureKey;
    private JSONObject request;
    private Map<String, Object> data;

    private static String accessKey;
    @Value("${ncp.accessKey}")
    private void setAccessKey(String accessKey) {
        NcpApiOption.accessKey = accessKey;
    }

    private static String secretKey;
    @Value("${ncp.secretKey}")
    private void setSecretKey(String secretKey) {
        NcpApiOption.secretKey = secretKey;
    }

    public static String smsApiUrl;
    @Value("${ncp.smsApiUrl}")
    private void setApiUrl(String smsApiUrl) {
        NcpApiOption.smsApiUrl = smsApiUrl;
    }

    public static String getAccessKey() {
        return accessKey;
    }

    private NcpApiOption(Builder builder) {
        this.path = builder.path;
        this.method = builder.method;
        this.mediaType = builder.mediaType;
        this.contentType = builder.contentType;
        this.request = builder.request;
        this.data = builder.data.isEmpty() ? null : builder.data;
        this.timestamp = String.valueOf(System.currentTimeMillis());
        this.signatureKey = makeSignature(path, accessKey, secretKey, timestamp, method.name());
    }

    public void setRequest(JSONObject request) {
        this.request = request;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String path;
        private HttpMethod method;
        private MediaType mediaType = MediaType.APPLICATION_JSON;
        private String contentType = "application/json; charset=utf-8";

        private JSONObject request = new JSONObject();

        private Builder() {
        }

        private final Map<String, Object> data = new HashMap<>();

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder request(JSONObject request) {
            this.request = request;
            return this;
        }

        public Builder data(String key, Object value) {
            this.data.put(key, value);
            return this;
        }

        public Builder mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public NcpApiOption build() {
            return new NcpApiOption(this);
        }
    }

    public String makeSignature(String url, String accessKey, String secretKey, String timestamp, String method) {
        String space = " "; // one space
        String newLine = "\n"; // new line

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        byte[] key = secretKey.getBytes(StandardCharsets.UTF_8);
        byte[] input = message.getBytes(StandardCharsets.UTF_8);

        SecretKeySpec signingKey = new SecretKeySpec(key, "HmacSHA256");
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        if(mac == null) {
            return "";
        }

        return Base64.encodeBase64String(mac.doFinal(input));
    }
}
