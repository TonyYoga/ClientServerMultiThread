package com.telran.data.entity;

import com.telran.data.AdvertRepository;
import com.telran.protocol.Protocol;
import com.telran.protocol.RawHttpRequest;
import com.telran.protocol.RawHttpResponse;

public class AdvertController implements Protocol {
    private final AdvertRepository advertRepository;

    public AdvertController(AdvertRepository advertRepository) {
        this.advertRepository = advertRepository;
    }

    @Override
    public RawHttpResponse getResponse(RawHttpRequest request) {
        return null;
    }

    @Override
    public RawHttpResponse getErrorResponse(int code, String reasonPhrase, String errorBody) {
        return null;
    }
}
