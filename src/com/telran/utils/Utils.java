package com.telran.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Utils {

    public <T> T parseTextData(String data, Class<T> clazz) {
        T result = null;
        try {
            Constructor<T> constructor = clazz.getConstructor();
            result = constructor.newInstance();
            Map<String, String> params = parseParams(data);
            for (Map.Entry<String, String> param : params.entrySet()) {
                try {
                    Field field = clazz.getDeclaredField(param.getKey());
                    field.setAccessible(true);
                    Function<String, ?> mapper = getTypeParser().get(field.getType().getSimpleName());
                    if (mapper == null) {
                        throw new RuntimeException();
                    }
                    field.set(result, mapper.apply(param.getValue()));
                } catch (NoSuchFieldException |
                        IllegalAccessException |
                        IllegalArgumentException |
                        SecurityException ex) {
                    //Catch reflection exceptions for skip fields
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException("Parse Exception " + ex);
        }
        return result;
    }

    private static Map<String, Function<String, ?>> getTypeParser() {
        Map<String, Function<String, ?>> mapper = new HashMap<>();
        mapper.put(Integer.class.getSimpleName(), Integer::parseInt);
        mapper.put(int.class.getSimpleName(), Integer::parseInt);
        mapper.put(String.class.getSimpleName(), v -> v);
        mapper.put(LocalDateTime.class.getSimpleName(), LocalDateTime::parse);
        mapper.put(LocalDate.class.getSimpleName(), LocalDate::parse);
        return mapper;
    }

    private static Map<String, String> parseParams(String query) {
        if (query == null) {
            return null;
        }
        return Arrays.stream(query.split("&"))
                .map(pair -> pair.split("="))
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
    }
}
