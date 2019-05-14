package com.telran.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Utils {

    public static <T> T parseTextData(String data, Class<T> clazz) {
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
                        throw new RuntimeException("Unsupported type: " + field.getName());
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
        mapper.put(LocalDateTime.class.getSimpleName(),
                v -> LocalDateTime.parse(v, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        mapper.put(LocalDate.class.getSimpleName(),
                v -> DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return mapper;
    }

    public static Map<String, String> parseParams(String query) {
        if (query == null) {
            return null;
        }
        return Arrays.stream(query.split("&"))
                .map(pair -> pair.split("="))
                .collect(Collectors.toMap(arr -> arr[0], arr -> arr[1]));
    }

    public static <T> String createBodyFromList(Iterable<T> values) {
        if (values == null) {
            return null;
        }
        List<T> list = StreamSupport.stream(values.spliterator(), false)
                .collect(Collectors.toList());
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            builder.append(list.get(i).toString());
            if (i < list.size() - 1) {
                builder.append(";\r\n");
            }
        }
        return builder.toString();
    }
}
