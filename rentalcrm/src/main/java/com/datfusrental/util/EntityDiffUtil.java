package com.datfusrental.util;

import org.springframework.stereotype.Component;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class EntityDiffUtil {

    public Map<String, Map<String, Object>> getDifferences(Object oldObj, Object newObj) throws IllegalAccessException {
        Map<String, Map<String, Object>> diffs = new HashMap<>();
        if (oldObj == null || newObj == null) return diffs;

        Class<?> clazz = oldObj.getClass();

        for (Field field : clazz.getDeclaredFields()) {
            // ✅ Ignore static or synthetic fields
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                continue;
            }

            field.setAccessible(true);

            Object oldValue;
            Object newValue;

            try {
                oldValue = normalizeValue(field.get(oldObj));
                newValue = normalizeValue(field.get(newObj));
            } catch (IllegalArgumentException e) {
                // ✅ skip invalid reflective access (like String internals)
                continue;
            }

            if (Objects.equals(oldValue, newValue)) continue;

            // ✅ Handle arrays (byte[], char[], etc.)
            if (oldValue != null && oldValue.getClass().isArray()) oldValue = arrayToString(oldValue);
            if (newValue != null && newValue.getClass().isArray()) newValue = arrayToString(newValue);

            // ✅ Limit long text values for display
            if (oldValue instanceof String s && s.length() > 300) oldValue = s.substring(0, 300) + "...";
            if (newValue instanceof String s && s.length() > 300) newValue = s.substring(0, 300) + "...";

            diffs.put(field.getName(), Map.of("old", oldValue, "new", newValue));
        }

        System.out.println("DIFF : " + diffs);
        return diffs;
    }

    private static String arrayToString(Object array) {
        int len = Array.getLength(array);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < len; i++) {
            sb.append(Array.get(array, i));
            if (i < len - 1) sb.append(", ");
        }
        return sb.append("]").toString();
    }

    private Object normalizeValue(Object value) {
        if (value instanceof java.util.Date) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((java.util.Date) value);
        }
        return value;
    }
}
