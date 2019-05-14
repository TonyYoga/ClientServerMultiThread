package com.telran.protocol;

import java.util.Map;

public class RawHttpResponse {
    public static class ResponseCode {
        public static final int OK = 200;
        public static final int BAD_REQUEST = 300;
        public static final int NOT_FOUND = 404;
        public static final int SERVER_ERROR = 500;
    }

    public int code;
    public String reasonPhrase;
    public Map<String, String> headers;
    public String body;

    public RawHttpResponse(int code, String reasonPhrase, Map<String, String> headers, String body) {
        this.code = code;
        this.reasonPhrase = reasonPhrase;
        this.headers = headers;
        this.body = body;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("HTTP/1.1 " + code);
        if (reasonPhrase != null) {
            builder.append(" " + reasonPhrase);
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.append(header.getKey() + ":" + header.getValue() + "\r\n");
            }
            builder.append("\r\n");
            if (body != null) {
                builder.append(body);
            }
        }
        return builder.toString();
    }
}
