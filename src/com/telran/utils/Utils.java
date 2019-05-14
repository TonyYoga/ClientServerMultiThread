package com.telran.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Utils {

    public static <T> T parseTextData(String data, Class<T> clazz) {
        T result = null;
        try {
            Constructor<T> constructor = clazz.getConstructor();
            result = constructor.newInstance();
            String[] params = data.split("&");
            for (String param : params) {
                try {
                    String[] keyValue = param.split("=");
                    Field field = clazz.getDeclaredField(keyValue[0]);
                    field.setAccessible(true);
                    if (field.getType() == LocalDateTime.class) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                        field.set(result, LocalDateTime.parse(keyValue[1], formatter));
                    } else {
                        Function<String, ?> mapper = getTypeParser().get(field.getType().getSimpleName());
                        if (mapper != null) {
                            field.set(result, mapper.apply(keyValue[1]));
                        } else {
                            throw new RuntimeException("Unsupported type: " + field.getType().getName());
                        }
                    }
                } catch (NoSuchFieldException | IllegalAccessException |
                        IllegalArgumentException | SecurityException ex) {
                    //Catch reflection exceptions for skip fields
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Parse exception", ex);
        }
        return result;
//        try {
//            Constructor<T> constructor = clazz.getConstructor();
//            result = constructor.newInstance();
//            Map<String, String> params = parseParams(data);
//            for (Map.Entry<String, String> param : params.entrySet()) {
//                try {
//                    Field field = clazz.getDeclaredField(param.getKey());
//                    field.setAccessible(true);
//                    Function<String, ?> mapper = getTypeParser().get(field.getType().getSimpleName());
//                    if (mapper == null) {
//                        throw new RuntimeException("Unsupported type: " + field.getName());
//                    }
//                    field.set(result, mapper.apply(param.getValue()));
//                } catch (NoSuchFieldException |
//                        IllegalAccessException |
//                        IllegalArgumentException |
//                        SecurityException ex) {
//                    //Catch reflection exceptions for skip fields
//                }
//            }
//
//        } catch (Exception ex) {
//            throw new RuntimeException("Parse Exception " + ex);
//        }
//        return result;
    }

    private static Map<String, Function<String, ?>> getTypeParser() {
        Map<String, Function<String, ?>> mapper = new HashMap<>();
        mapper.put(Integer.class.getSimpleName(), Integer::parseInt);
        mapper.put(int.class.getSimpleName(), Integer::parseInt);
        mapper.put(Double.class.getSimpleName(), Double::parseDouble);
        mapper.put(double.class.getSimpleName(), Double::parseDouble);
        mapper.put(float.class.getSimpleName(), Float::parseFloat);
        mapper.put(Float.class.getSimpleName(), Float::parseFloat);
        mapper.put(Boolean.class.getSimpleName(), Boolean::parseBoolean);
        mapper.put(boolean.class.getSimpleName(), Boolean::parseBoolean);
        mapper.put(Long.class.getSimpleName(), Long::parseLong);
        mapper.put(long.class.getSimpleName(), Long::parseLong);
        mapper.put(String.class.getSimpleName(), v -> v);
        mapper.put(UUID.class.getSimpleName(), UUID::fromString);
        mapper.put(LocalDateTime.class.getSimpleName(),
                v -> LocalDateTime.parse(v, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        mapper.put(LocalDate.class.getSimpleName(),
                v -> DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return mapper;
    }

    public static Map<String, String> parseQuery(String query) {
        Map<String,String> res = new HashMap<>();
        String[] arr = query.split("&");
        for (String q : arr){
            String[] pair = q.split("=");
            if(pair.length == 2){
                res.putIfAbsent(pair[0],pair[1]);
            }
        }
        return res;
//        return Arrays.stream(query.split("&"))
//                .map(pair -> pair.split("="))
//                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
    }

//    public static <T> String createBodyFromList(Iterable<T> values) {
//        if (values == null) {
//            return null;
//        }
//        List<T> list = StreamSupport.stream(values.spliterator(), false)
//                .collect(Collectors.toList());
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0; i < list.size(); i++) {
//            builder.append(list.get(i).toString());
//            if (i < list.size() - 1) {
//                builder.append(";\r\n");
//            }
//        }
//        return builder.toString();
//    }
}
