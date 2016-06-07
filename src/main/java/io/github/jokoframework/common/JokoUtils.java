package io.github.jokoframework.common;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.jokoframework.common.dto.DTOConvertable;
import io.github.jokoframework.common.errors.JokoApplicationException;
import io.github.jokoframework.security.controller.SecurityConstants;

/**
 * 
 */
// TODO evaluar los metodos de esta clase vs SecurityUtils
public class JokoUtils {

    private static Pattern interpolationPattern = Pattern.compile("\\{(\\w+)(.*?)\\}");

    private static final ObjectMapper mapper = new ObjectMapper();

    private JokoUtils() {

    }

    /**
     * Retorna el IP del cliente asumiendo que el Web Server está detras de un
     * proxy
     *
     * @param request
     * @return
     */
    public static String getClientIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static <T> String join(Collection<T> list, String separator) {
        StringBuffer buffer = new StringBuffer();
        boolean first = true;
        for (Object o : list) {
            if (!first) {
                buffer.append(separator);
            }
            buffer.append(o.toString());
            first = false;

        }
        return buffer.toString();
    }

    public static Calendar getUTCCurrentTime() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    }

    public static String formatLogString(String s) {
        return "\"" + s + "\"";
    }

    public static String formatLogString(Object s) {
        return "\"" + s.toString() + "\"";
    }

    public static String foramtLogId(Object s) {
        return "#" + s.toString();
    }

    /**
     * Recorre una lista de elemenos de tipo DTOConvertable, los conviente a DTO
     * y devuelve una lista de DTOs
     * 
     * @param entities
     * @param c
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> fromEntityToDTO(List<? extends DTOConvertable> entities, Class<T> c) {
        ArrayList<T> list = new ArrayList<T>();

        List<DTOConvertable> l = (List<DTOConvertable>) entities;
        for (DTOConvertable o : l) {
            list.add((T) o.toDTO());
        }
        return list;
    }

    public static String formatMap(String format, Map<String, Object> values) {
        StringBuilder formatter = new StringBuilder(format);
        ArrayList<Object> valueList = new ArrayList<Object>();

        Matcher matcher = interpolationPattern.matcher(format);

        while (matcher.find()) {
            String key = matcher.group(1);
            String rest = matcher.group(2);

            String formatKey = String.format("{%s%s}", key, rest);
            int index = formatter.indexOf(formatKey);

            if (index != -1) {
                Object value = null;
                if (values != null) {
                    value = values.get(key);
                }
                if (value != null) {
                    String formatValue = String.format("{%d%s}", valueList.size(), rest);
                    formatter.replace(index, index + formatKey.length(), formatValue);
                    valueList.add(value);
                } else {
                    throw new ArrayIndexOutOfBoundsException(
                            String.format("Pattern key %s not found in dictionary", key));
                }
            }
        }

        return MessageFormat.format(formatter.toString(), valueList.toArray());
    }

    public static String generateRandomString(int length) {
        BytesKeyGenerator consumerIdGenerator = KeyGenerators.secureRandom(length);
        byte[] b = consumerIdGenerator.generateKey();
        String s = Base64.encodeBase64URLSafeString(b);
        return s;
    }

    /**
     * Serializa un objeto a un string en formato JSON.
     *
     * @param jsonObject
     *            el objeto a ser serializado
     * @return la representación JSON del objeto
     */
    public static String toJSON(Object jsonObject) {
        String json = null;
        try {
            json = mapper.writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            throw new JokoApplicationException(e);
        }
        return json;
    }

    public static String formatDate(Date date) {
        if (date != null) {
            return DateFormatUtils.formatUTC(date, SecurityConstants.DATE_FORMAT);
        } else {
            return null;
        }
    }

    public static Date formatDateString(String dateString) {
        try {
            return DateUtils.parseDate(dateString, SecurityConstants.DATE_FORMAT);
        } catch (ParseException e) {
            return null;
        }
    }

}
