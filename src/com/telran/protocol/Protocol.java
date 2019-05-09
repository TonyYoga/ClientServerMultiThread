package com.telran.protocol;

public interface Protocol {
    RawHttpResponse getResponse(RawHttpRequest request);
    RawHttpResponse gerErrorResponse(int code, String reasonPhraseig, String errorBody);
}
