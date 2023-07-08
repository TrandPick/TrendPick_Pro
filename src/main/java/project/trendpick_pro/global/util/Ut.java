package project.trendpick_pro.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class Ut {
    public static class json {

        public static String toStr(Map map) {
            try {
                return new ObjectMapper().writeValueAsString(map);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    ;

    public static class time {
        public static String diffFormat1Human(LocalDateTime time1, LocalDateTime time2) {
            String suffix = time1.isAfter(time2) ? "전" : "후";

            // 두개의 시간의 차이를 초로 환산
            long diff = Math.abs(ChronoUnit.SECONDS.between(time1, time2));

            long diffSeconds = diff % 60; // 초 부분만
            long diffMinutes = diff / (60) % 60; // 분 부분만
            long diffHours = diff / (60 * 60) % 24; // 시간 부분만
            long diffDays = diff / (60 * 60 * 24); // 나머지는 일 부분으로

            StringBuilder sb = new StringBuilder();

            if (diffDays > 0) sb.append(diffDays).append("일 ");
            if (diffHours > 0) sb.append(diffHours).append("시간 ");
            if (diffMinutes > 0) sb.append(diffMinutes).append("분 ");
            if (diffSeconds > 0) sb.append(diffSeconds).append("초 ");

            if (sb.isEmpty()) sb.append("1초 ");

            return sb.append(suffix).toString();
        }
    }
    public static String nf(long number) {
        return String.format("%,d", (int) number);
    }

    public static class reflection {
        public static boolean setFieldValue(Object o, String fieldName, Object value) {
            Field field = null;

            try {
                field = o.getClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                return false;
            }

            field.setAccessible(true);

            try {
                field.set(o, value);
            } catch (IllegalAccessException e) {
                return false;
            }

            return true;
        }

        public static <T> T getFieldValue(Object o, String fieldName, T defaultValue) {
            Field field = null;

            try {
                field = o.getClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                return defaultValue;
            }

            field.setAccessible(true);

            try {
                return (T) field.get(o);
            } catch (IllegalAccessException e) {
                return defaultValue;
            }
        }

        public static <T> T call(Object obj, String methodName, Object... args) {
            try {
                Class<?>[] argTypes = Arrays.stream(args)
                        .map(Object::getClass)
                        .toArray(Class<?>[]::new);

                Method method = obj.getClass().getDeclaredMethod(methodName, argTypes);
                method.setAccessible(true);
                return (T) method.invoke(obj, args);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        public static <T> T callArr(Object obj, String methodName, Object... args) {
            return call(obj, methodName, new Object[]{args});
        }
    }

    public static class hash {
        private static final MessageDigest md;

        static {
            try {
                md = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        public static String sha256(String str) {
            // Convert the input string to bytes and update the MessageDigest
            byte[] inputBytes = str.getBytes(StandardCharsets.UTF_8);
            byte[] hashBytes = md.digest(inputBytes);

            // Convert the hashed bytes to a Base64 encoded string
            return Base64.getEncoder().encodeToString(hashBytes);
        }
    }
    public static <K, V> Map<K, V> mapOf(Object... args) {
        Map<K, V> map = new LinkedHashMap<>();

        int size = args.length / 2;

        for (int i = 0; i < size; i++) {
            int keyIndex = i * 2;
            int valueIndex = keyIndex + 1;

            K key = (K) args[keyIndex];
            V value = (V) args[valueIndex];

            map.put(key, value);
        }

        return map;
    }
    public static class url {
        public static String encode(String str) {
            return URLEncoder.encode(str, StandardCharsets.UTF_8);
        }

        public static String modifyQueryParam(String url, String paramName, String paramValue) {
            url = deleteQueryParam(url, paramName);
            url = addQueryParam(url, paramName, paramValue);

            return url;
        }

        public static String addQueryParam(String url, String paramName, String paramValue) {
            if (!url.contains("?")) {
                url += "?";
            }

            if (!url.endsWith("?") && !url.endsWith("&")) {
                url += "&";
            }

            url += paramName + "=" + paramValue;

            return url;
        }

        private static String deleteQueryParam(String url, String paramName) {
            int startPoint = url.indexOf(paramName + "=");
            if (startPoint == -1) return url;

            int endPoint = url.substring(startPoint).indexOf("&");

            if (endPoint == -1) {
                return url.substring(0, startPoint - 1);
            }

            String urlAfter = url.substring(startPoint + endPoint + 1);

            return url.substring(0, startPoint) + urlAfter;
        }
        public static String getQueryParamValue(String url, String paramName, String defaultValue) {
            String[] urlBits = url.split("\\?", 2);

            if (urlBits.length == 1) {
                return defaultValue;
            }

            urlBits = urlBits[1].split("&");

            String param = Arrays.stream(urlBits)
                    .filter(s -> s.startsWith(paramName + "="))
                    .findAny()
                    .orElse(paramName + "=" + defaultValue);

            String value = param.split("=", 2)[1].trim();

            return value.length() > 0 ? value : defaultValue;
        }
    }
    public static class date {
        public static LocalDateTime bitsToLocalDateTime(List<Integer> bits) {
            return LocalDateTime.of(bits.get(0), bits.get(1), bits.get(2), bits.get(3), bits.get(4), bits.get(5), bits.get(6));
        }

        public static int getEndDayOf(int year, int month) {
            String yearMonth = year + "-" + "%02d".formatted(month);

            return getEndDayOf(yearMonth);
        }

        public static int getEndDayOf(String yearMonth) {
            LocalDate convertedDate = LocalDate.parse(yearMonth + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            convertedDate = convertedDate.withDayOfMonth(
                    convertedDate.getMonth().length(convertedDate.isLeapYear()));

            return convertedDate.getDayOfMonth();
        }

        public static LocalDateTime parse(String pattern, String dateText) {
            return LocalDateTime.parse(dateText, DateTimeFormatter.ofPattern(pattern));
        }

        public static LocalDateTime parse(String dateText) {
            return parse("yyyy-MM-dd HH:mm:ss.SSSSSS", dateText);
        }

        public static String getCurrentYearMonth() {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }

        public static <K, V> Map<K, V> mapOf(Object... args) {
            Map<K, V> map = new LinkedHashMap<>();

            int size = args.length / 2;

            for (int i = 0; i < size; i++) {
                int keyIndex = i * 2;
                int valueIndex = keyIndex + 1;

                K key = (K) args[keyIndex];
                V value = (V) args[valueIndex];

                map.put(key, value);
            }

            return map;
        }

        public static String nf(long number) {
            return String.format("%,d", (int) number);
        }
    }

}
