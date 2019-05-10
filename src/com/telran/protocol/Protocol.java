package com.telran.protocol;

public interface Protocol {
    RawHttpResponse getResponse(RawHttpRequest request);
    RawHttpResponse getErrorResponse(int code, String reasonPhraseig, String errorBody);
}
