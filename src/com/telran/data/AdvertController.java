package com.telran.data;

import com.telran.data.entity.Advert;
import com.telran.protocol.Protocol;
import com.telran.protocol.RawHttpRequest;
import com.telran.protocol.RawHttpResponse;
import com.telran.utils.Utils;
import dto.AddAdvertResponseDto;
import dto.AdvertDto;
import dto.ErrorResponseDto;

import java.net.URI;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class AdvertController implements Protocol {
    private static final String HEADER_LENGTH = "Content-Length";
    private static final String HEADER_TYPE = "Content-Type";
    private static final String HEADER_SERVER = "Server";
    private static final String HEADER_DATE = "Date";
    private static final String CONTENT_TYPE_TEXT = "*/*; charset=UTF-8";
    private Map<String, Function<RawHttpRequest, RawHttpResponse>> mapper;
    
    private final AdvertRepository advertRepository;

    public AdvertController(AdvertRepository advertRepository) {
        this.advertRepository = advertRepository;
        mapper = new HashMap<>();
        mapper.put("/advert", this::advert);
    }

    private RawHttpResponse advert(RawHttpRequest request) {
        RawHttpResponse response = null;
        Advert advert;
        //TODO class Solution
        switch (request.method) {
            case GET:
                //TODO class Solution
//                response = getAdvertResponse(request);
                break;
            case POST:
                AdvertDto dto = Utils.parseTextData(request.body, AdvertDto.class);
                UUID id = UUID.randomUUID();
                if (advertRepository.addAdvert(new Advert(id, dto.getOwner(), dto.getContent(), dto.getDateTime())) != null) {
                    AddAdvertResponseDto responseDto = new AddAdvertResponseDto(id);
                    response = createResponse(RawHttpResponse.ResponseCode.OK, "Ok", responseDto.toString());
                } else {
                    response = getErrorResponse(RawHttpResponse.ResponseCode.SERVER_ERROR, "Server Error", " Error adding advert< try again later!");
                }
                break;
            case DELETE:
                //TODO class Solution
//                response = removeAdvert(request);
                break;
                
            default:
                response = getErrorResponse(RawHttpResponse.ResponseCode.BAD_REQUEST, "Bad request", "Unsupported request method " + request.method);
        }
        
        return response;
    }

//    private RawHttpResponse removeAdvert(RawHttpRequest request) {
//        RawHttpResponse response = null;
//        Map<String, String> params = Utils.parseParams(request.uri.getQuery());
//        if (params != null && params.containsKey("id")) {
//            Advert res = advertRepository.remove(UUID.fromString(params.get("id")));
//            if (res != null) {
//                response = createResponse(RawHttpResponse.ResponseCode.OK, "OK", "Advert deleted" + res.getId());
//            } else {
//                response = getErrorResponse(RawHttpResponse.ResponseCode.BAD_REQUEST, "Not Found", "Advert not found ");
//            }
//        }
//        return response;
//    }
//
//    private RawHttpResponse getAdvertResponse(RawHttpRequest request) {
//        RawHttpResponse response = null;
//        String params = request.uri.getQuery();
//        if (params != null) {
//            String[] arr = params.split("&");
//            for (String param : arr) {
//                String[] keyValue = param.split("=");
//
//                if (keyValue[0].equals("owner")) {
//                    Iterable<Advert> adverts = advertRepository.find(keyValue[1]);
//                    response = createResponse(RawHttpResponse.ResponseCode.OK, "OK", Utils.createBodyFromList(adverts));
//                } else if (keyValue[0].equals("date")) {
//                    LocalDate localDate = LocalDate.parse(keyValue[1]);
//                    Iterable<Advert> adverts = advertRepository.find(localDate);
//                    response = createResponse(RawHttpResponse.ResponseCode.OK, "OK", Utils.createBodyFromList(adverts));
//                } else if (keyValue[0].equals("start") && keyValue[2].equals("end")) {
//                    LocalDate start = LocalDate.parse(keyValue[1]);
//                    LocalDate end = LocalDate.parse(keyValue[3]);
//                    Iterable<Advert> adverts = advertRepository.find(start, end);
//                    response = createResponse(RawHttpResponse.ResponseCode.OK, "OK", Utils.createBodyFromList(adverts));
//                }
//            }
//        } else {
//            response = getErrorResponse(RawHttpResponse.ResponseCode.BAD_REQUEST,"Bad request", "Wrong query params!");
//        }
//
//        return response;
//    }

    private RawHttpResponse createResponse(int code, String reasonPhrase, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put(HEADER_DATE, LocalDateTime.now().toString());
        headers.put(HEADER_TYPE, CONTENT_TYPE_TEXT);
        headers.put(HEADER_SERVER, "Advert Repository Server");
        if (body != null) {
            headers.put(HEADER_LENGTH, String.valueOf(body.length()));
        } else {
            headers.put(HEADER_LENGTH, "0");
        }
        return new RawHttpResponse(code, reasonPhrase, headers, body);
    }
    
    @Override
    public RawHttpResponse getResponse(RawHttpRequest request) {
        try {
            URI path = request.uri;
            Function<RawHttpRequest, RawHttpResponse> mapperFunc = mapper.get(path);
            if (mapperFunc == null) {
                throw new RuntimeException("Wrong path: " + path);
            }
            RawHttpResponse response; //TODO
            try {
                return mapperFunc.apply(request);
            } catch (DateTimeException ex) {
                //TODO get from class Solution
            }

        } catch (Exception ex) {
            return getErrorResponse(RawHttpResponse.ResponseCode.BAD_REQUEST, "Not Found", ex.getMessage());
        }
    }

    @Override
    public RawHttpResponse getErrorResponse(int code, String reasonPhrase, String errorBody) {
        String body = new ErrorResponseDto(errorBody).toString();
        Map<String, String> headers = new HashMap<>();
        headers.put(HEADER_DATE, LocalDateTime.now().toString());
        headers.put(HEADER_TYPE, CONTENT_TYPE_TEXT);
        headers.put(HEADER_SERVER, "Advert Repository Server");
        headers.put(HEADER_LENGTH, String.valueOf(body.length()));
        return new RawHttpResponse(code, reasonPhrase, headers, body);
    }
}
