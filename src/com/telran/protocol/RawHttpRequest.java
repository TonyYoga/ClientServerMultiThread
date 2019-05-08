package com.telran.protocol;

import java.net.URI;
import java.util.Map;

public class RawHttpRequest {
    enum Method {
        GET, POST, PUT, DELETE
    }

    Method method;
    URI uri;
    Map<String, String> headers;
    String body;

    public RawHttpRequest(Method method, URI uri, Map<String, String> headers, String body) {
        this.method = method;
        this.uri = uri;
        this.headers = headers;
        this.body = body;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(method + " " + uri.toString() + " HTTP/1.1 \r\n");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            builder.append(header.getKey()).append(":").append(header.getValue());
        }
        builder.append("\r\n");
        if (body != null) {
            builder.append(body);
        }
        return builder.toString();
    }
}
