package io.github.jokoframework.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jokoframework.common.dto.DTOConvertable;
import io.github.jokoframework.common.errors.JokoApplicationException;
import io.github.jokoframework.security.controller.SecurityConstants;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;

import jakarta.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 */
// TODO evaluar los metodos de esta clase vs SecurityUtils
public class JokoUtils {

    public static final String UNKNOWN = "unknown";
    private static Pattern interpolationPattern = Pattern.compile("\\{(\\w+)(.*?)\\}");

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JokoUtils() {

    }

    /**
     * Retorna el IP del cliente asumiendo que el Web Server está detras de un
     * proxy
     *
     * @param request El request de donde quiere obtenerse el clientIpAddr
     * @return el Nro. de IP del cliente, o "UNKWON" si no se pudo obtenerlo
     */
    public static String getClientIpAddr(HttpServletRequest request) {
        List<String> headers = Arrays.asList("X-Forwarded-For","Proxy-Client-IP", "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR");
        String ip = UNKNOWN;
        for (String header : headers) {
            ip = request.getHeader(header);
            if (!isEmtpyOrUnknown(ip)) {
                break;
            }
        }
        if (isEmtpyOrUnknown(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private static boolean isEmtpyOrUnknown(String pIp) {
        return StringUtils.isEmpty(pIp) || UNKNOWN.equalsIgnoreCase(pIp);
    }

    public static <T> String join(Collection<T> list, String separator) {
        StringBuilder buffer = new StringBuilder();
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
     * @param entities la lista de Entities que se desea convertir
     * @param <T> El tipo de dato que se espera, se deduce de la asignación donde se almacena el retorno
     * @return la lista de DTOs generados.
     */
    @SuppressWarnings("unchecked")
	public static <T> List<T> fromEntityToDTO(List<? extends DTOConvertable> entities) {
        List<T> list = new ArrayList<>();

        List<DTOConvertable> l = (List<DTOConvertable>) entities;
        list.addAll(l.stream().map(o -> (T) o.toDTO()).collect(Collectors.toList()));
        return list;
    }

    public static String formatMap(String format, Map<String, Object> values) {
        StringBuilder formatter = new StringBuilder(format);
        List<Object> valueList = new ArrayList<>();

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
     * @param jsonObject el objeto a ser serializado
     * @return la representación JSON del objeto
     */
    public static String toJSON(Object jsonObject) {
        String json = null;
        try {
            json = MAPPER.writeValueAsString(jsonObject);
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
