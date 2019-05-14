package com.telran.data;

import com.telran.data.entity.Advert;
import com.telran.dto.AddAdvertResponseDto;
import com.telran.dto.AdvertDto;
import com.telran.dto.AdvertListDto;
import com.telran.dto.ErrorResponseDto;
import com.telran.protocol.Protocol;
import com.telran.protocol.RawHttpRequest;
import com.telran.protocol.RawHttpResponse;
import com.telran.utils.Utils;

import java.net.URI;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AdvertController implements Protocol {
    private static final String HEADER_LENGTH = "Content-Length";
    private static final String HEADER_TYPE = "Content-Type";
    private static final String HEADER_SERVER = "Server";
    private static final String HEADER_DATE = "Date";

    private static final String CONTENT_TYPE_TEXT = "*/*; charset=UTF-8";
    private Map<String, Function<RawHttpRequest, RawHttpResponse>> mapper;
    private AdvertRepository repository;

    public AdvertController(AdvertRepository repository) {
        this.repository = repository;
        mapper = new HashMap<>();
        mapper.put("/advert", this::advertMapping);
    }


    @Override
    public RawHttpResponse getResponse(RawHttpRequest request) {
        try {
            String path = request.uri.getPath();
            Function<RawHttpRequest, RawHttpResponse> mappedFunc = mapper.get(path);
            if (mappedFunc == null) {
                throw new RuntimeException("Wrong path: " + path);
            }
            RawHttpResponse response;
            try {
                response =  mappedFunc.apply(request);
            }catch (DateTimeParseException ex){
                ex.printStackTrace();
                response = getErrorResponse(RawHttpResponse.ResponseCode.BAD_REQUEST,"Bad request","Wrong date format!Date format should be dd/MM/yyyy, Example 31/12/2019");
            }catch (IllegalArgumentException ex){
                ex.printStackTrace();
                response = getErrorResponse(RawHttpResponse.ResponseCode.BAD_REQUEST,"Bad request","Wrong id format");
            }catch (RuntimeException ex){
                ex.printStackTrace();
                response = getErrorResponse(RawHttpResponse.ResponseCode.BAD_REQUEST,"Bad request",ex.getMessage());
            }
            return response;
        } catch (Exception ex) {
            return getErrorResponse(RawHttpResponse.ResponseCode.NOT_FOUND, "Not Found", ex.getMessage());
        }
    }

    @Override
    public RawHttpResponse getErrorResponse(int code, String reasonPhrase, String errorBody) {
        Map<String, String> headers = new HashMap<>();
        headers.put(HEADER_DATE, LocalDateTime.now().toString());
        headers.put(HEADER_TYPE, CONTENT_TYPE_TEXT);
        headers.put(HEADER_SERVER, "TelRan Java Server");
        headers.put(HEADER_LENGTH, String.valueOf(errorBody.length()));
        ErrorResponseDto body = new ErrorResponseDto(errorBody);
        return new RawHttpResponse(code, reasonPhrase, headers, body.toString());
    }

    private RawHttpResponse advertMapping(RawHttpRequest request) {
        RawHttpResponse response;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Map<String, String> queries;
        switch (request.method) {
            case GET:
                Objects.requireNonNull(request.uri.getQuery(),"Wrong request format");
                queries = Utils.parseQuery(request.uri.getQuery());
                if(queries.containsKey("owner")){
                    Iterable<Advert> list = repository.find(queries.get("owner"));
                    Objects.requireNonNull(list,"Owner does not exist");
                    response = mapListToResponse(list);
                }else if(queries.containsKey("date")){
                    LocalDate date = LocalDate.parse(queries.get("date"),formatter);
                    Iterable<Advert> list = repository.find(date);
                    response = mapListToResponse(list);
                }else if(queries.containsKey("start") && queries.containsKey("end")){
                    LocalDate start = LocalDate.parse(queries.get("start"),formatter);
                    LocalDate end = LocalDate.parse(queries.get("end"),formatter);
                    Iterable<Advert> list = repository.find(start,end);
                    response = mapListToResponse(list);
                }else{
                    throw new RuntimeException("Wrong request format");
                }
                break;
            case POST:
                AdvertDto dto = Utils.parseTextData(request.body, AdvertDto.class);
                UUID id = UUID.randomUUID();
                if (repository.addAdvert(new Advert(id, dto.getOwner(), dto.getDate(), dto.getContent()))) {
                    AddAdvertResponseDto responseDto = new AddAdvertResponseDto(id);
                    response = createResponse(RawHttpResponse.ResponseCode.OK, "OK", responseDto.toString());
                } else {
                    response = getErrorResponse(RawHttpResponse.ResponseCode.SERVER_ERROR, "Server Error", "Error adding advert! Try again");
                }
                break;
            case DELETE:
                Objects.requireNonNull(request.uri.getQuery(),"Wrong request format");
                queries = Utils.parseQuery(request.uri.getQuery());
                String queryId = queries.get("id");
                Objects.requireNonNull(queryId,"Wrong id format");
                Advert advert = repository.remove(UUID.fromString(queryId));
                Objects.requireNonNull(advert,"Advert with " + queryId + " does not exist!");
                AdvertDto advertDto = new AdvertDto(advert.id(), advert.owner(), advert.date(), advert.content());
                response = createResponse(RawHttpResponse.ResponseCode.OK, "OK", advertDto.toString());
                break;
            default:
                response = getErrorResponse(RawHttpResponse.ResponseCode.BAD_REQUEST, "Bad request", "Unsupported request method: " + request.method);
        }
        return response;

//        return switch(request.method){
//            case GET -> {
//                Objects.requireNonNull(request.uri.getQuery(),"Wrong request format");
//                queries = Utils.parseQuery(request.uri.getQuery());
//                if(queries.containsKey("owner")){
//                    Iterable<Advert> list = repository.find(queries.get("owner"));
//                    Objects.requireNonNull(list,"Owner does not exist");
//                    break mapListToResponse(list);
//                }else if(queries.containsKey("date")){
//                    LocalDate date = LocalDate.parse(queries.get("date"),formatter);
//                    Iterable<Advert> list = repository.find(date);
//                    break mapListToResponse(list);
//                }else if(queries.containsKey("start") && queries.containsKey("end")){
//                    LocalDate start = LocalDate.parse(queries.get("start"),formatter);
//                    LocalDate end = LocalDate.parse(queries.get("end"),formatter);
//                    Iterable<Advert> list = repository.find(start,end);
//                    break mapListToResponse(list);
//                }else{
//                    throw new RuntimeException("Wrong request format");
//                }
//            }
//            case POST -> {
//                AdvertDto dto = Utils.parseTextData(request.body, AdvertDto.class);
//                UUID id = UUID.randomUUID();
//                if (repository.add(new Advert(id, dto.getOwner(), dto.getDate(), dto.getContent()))) {
//                    AddAdvertResponseDto responseDto = new AddAdvertResponseDto(id);
//                    break createResponse(RawHttpResponse.ResponseCode.OK, "OK", responseDto.toString());
//                } else {
//                    break getErrorResponse(RawHttpResponse.ResponseCode.SERVER_ERROR, "Server Error", "Error adding advert! Try again");
//                }
//            }
//            case DELETE -> {
//                Objects.requireNonNull(request.uri.getQuery(),"Wrong request format");
//                queries = Utils.parseQuery(request.uri.getQuery());
//                String queryId = queries.get("id");
//                Objects.requireNonNull(queryId,"Wrong id format");
//                Advert advert = repository.remove(UUID.fromString(queryId));
//                Objects.requireNonNull(advert,"Advert with " + queryId + " does not exist!");
//                AdvertDto advertDto = new AdvertDto(advert.id(), advert.owner(), advert.date(), advert.content());
//                break createResponse(RawHttpResponse.ResponseCode.OK, "OK", advertDto.toString());
//            }
//            default -> getErrorResponse(RawHttpResponse.ResponseCode.BAD_REQUEST, "Bad request", "Unsupported request method: " + request.method);
//        };
    }

    private RawHttpResponse mapListToResponse(Iterable<Advert> list){
        if(list!=null){
            AdvertListDto dto = new AdvertListDto(StreamSupport.stream(list.spliterator(),false)
                    .map(a -> new AdvertDto(a.id(),a.owner(),a.date(),a.content()))
                    .collect(Collectors.toList()));
            return createResponse(RawHttpResponse.ResponseCode.OK,"OK",dto.toString());
        }
        return createResponse(RawHttpResponse.ResponseCode.OK,"OK","");
    }

    private RawHttpResponse createResponse(int code, String reasonPhrase, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put(HEADER_DATE, LocalDateTime.now().toString());
        headers.put(HEADER_TYPE, CONTENT_TYPE_TEXT);
        headers.put(HEADER_SERVER, "TelRan Java Server");
        if (body != null) {
            headers.put(HEADER_LENGTH, String.valueOf(body.length()));
        } else {
            headers.put(HEADER_LENGTH, "0");
        }

        return new RawHttpResponse(code, reasonPhrase, headers, body);
    }
}
