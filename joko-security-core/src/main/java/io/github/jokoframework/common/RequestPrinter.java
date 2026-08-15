package io.github.jokoframework.common;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

public class RequestPrinter {

    private static final Logger LOGGER = LogManager.getLogger(RequestPrinter.class.getSimpleName());

    public static final String INDENT_UNIT = "\t";
    public static final String SEPARATOR_NL = "', \n";
    public static final String SEPARATOR_2 = ", \n";
    public static final String APOSTROPHE = "'";
    public static final String SEPARATOR_3 = "',\n";
    public static final String SEPARATOR_4 = ",\n";
    public static final String LINE_SEPARATOR = "-------\n";

    private RequestPrinter() {
    }

    // Private helper methods

    private static String debugStringSession(HttpSession session, int indent) {
        String indentString = RequestPrinter.repeat(INDENT_UNIT, indent);
        if (session == null)
            return indentString + "{ }";
        StringBuilder sb = new StringBuilder();
        sb.append(indentString).append("{\n");
        sb.append(indentString).append(INDENT_UNIT).append("'id': '").append(session.getId()).append(SEPARATOR_NL);
        sb.append(indentString).append(INDENT_UNIT).append("'last_accessed_time': ").append(session.getLastAccessedTime()).append(SEPARATOR_2);
        sb.append(indentString).append(INDENT_UNIT).append("'max_inactive_interval': ").append(session.getMaxInactiveInterval()).append(SEPARATOR_2);
        sb.append(indentString).append(INDENT_UNIT).append("'is_new': '").append(session.isNew()).append(SEPARATOR_NL);
        sb.append(indentString).append(INDENT_UNIT).append("'attributes': {\n");
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            Object o = session.getAttribute(attributeName);
            sb.
                    append(indentString).
                    append(INDENT_UNIT).
                    append(APOSTROPHE).append(attributeName).append("': ").
                    append(APOSTROPHE).append(o.toString()).append(SEPARATOR_3);
        }
        sb.append(indentString).append(INDENT_UNIT).append("}\n");
        sb.append(indentString).append("}\n");
        return sb.toString();
    }

    private static String debugStringParameter(String indentString, String parameterName, String[] parameterValues) {
        StringBuilder sb = new StringBuilder();
        sb.
                append(indentString).
                append(INDENT_UNIT).
                append(APOSTROPHE).append(parameterName).append("': ");
        if (ArrayUtils.isEmpty(parameterValues)) {
            sb.append("None");
        } else {
            if (parameterValues.length > 1) {
                sb.append("[");
            }
            sb.append(RequestPrinter.join(parameterValues, ","));
            if (parameterValues.length > 1) {
                sb.append("]");
            }
        }
        return sb.toString();
    }

    private static String debugStringHeader(String indentString, String headerName, List<String> headerValues) {
        StringBuilder sb = new StringBuilder();
        sb.
                append(indentString).
                append(INDENT_UNIT).
                append(APOSTROPHE).append(headerName).append("': ");
        if (CollectionUtils.isEmpty(headerValues)) {
            sb.append("None");
        } else {
            if (headerValues.size() > 1) sb.append("[");
            sb.append(RequestPrinter.join(headerValues, ","));
            if (headerValues.size() > 1) sb.append("]");
        }
        return sb.toString();
    }

    private static String debugStringParameters(HttpServletRequest request, int indent) {
        String indentString = RequestPrinter.repeat(INDENT_UNIT, indent);
        StringBuilder sb = new StringBuilder();
        sb.append(indentString).append("{\n");
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            String[] parameterValues = request.getParameterValues(parameterName);
            sb.
                    append(RequestPrinter.debugStringParameter(indentString, parameterName, parameterValues)).
                    append(",\n");
        }
        sb.append(indentString).append("}\n");
        return sb.toString();
    }

    private static String debugStringCookie(Cookie cookie, String indentString) {
        if (cookie == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(indentString).append("{ \n");
        sb.append(indentString).append(INDENT_UNIT).append("'name': '").append(cookie.getName()).append(SEPARATOR_NL);
        sb.append(indentString).append(INDENT_UNIT).append("'value': '").append(cookie.getValue()).append(SEPARATOR_NL);
        sb.append(indentString).append(INDENT_UNIT).append("'domain': '").append(cookie.getDomain()).append(SEPARATOR_NL);
        sb.append(indentString).append(INDENT_UNIT).append("'path': '").append(cookie.getPath()).append(SEPARATOR_NL);
        sb.append(indentString).append(INDENT_UNIT).append("'max_age': ").append(cookie.getMaxAge()).append(SEPARATOR_2);
        sb.append(indentString).append(INDENT_UNIT).append("'version': ").append(cookie.getVersion()).append(SEPARATOR_2);
        sb.append(indentString).append(INDENT_UNIT).append("'comment': '").append(cookie.getComment()).append(SEPARATOR_NL);
        sb.append(indentString).append(INDENT_UNIT).append("'secure': '").append(cookie.getSecure()).append(SEPARATOR_3);
        sb.append(indentString).append("}");
        return sb.toString();
    }

    private static String debugStringCookies(HttpServletRequest request, int indent) {
        if (request.getCookies() == null) {
            return "";
        }
        String indentString = RequestPrinter.repeat(INDENT_UNIT, indent);
        StringBuilder sb = new StringBuilder();
        sb.append(indentString).append("[\n");
        int cookieCount = 0;
        for (Cookie cookie : request.getCookies()) {
            sb.append(RequestPrinter.debugStringCookie(cookie, indentString + INDENT_UNIT)).append(SEPARATOR_4);
            cookieCount++;
        }
        if (cookieCount > 0) {
            sb.delete(sb.length() - ",\n".length(), sb.length());
        }
        sb.append("\n").append(indentString).append("]\n");
        return sb.toString();
    }

    private static String debugStringHeaders(HttpServletRequest request, int indent) {
        String indentString = RequestPrinter.repeat(INDENT_UNIT, indent);
        StringBuilder sb = new StringBuilder();
        sb.append(indentString).append("{\n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headerValues = request.getHeaders(headerName);
            List<String> headerValuesList = new ArrayList<>();
            while (headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                headerValuesList.add(headerValue);
            }
            sb.
                    append(RequestPrinter.debugStringHeader(indentString, headerName, headerValuesList)).
                    append(",\n");
        }
        sb.append(indentString).append("}\n");
        return sb.toString();
    }

    // API

    // HELPER methods

    // Added a few helper methods to use.
    // alternatively, you could use Apache's Common Lang library

    // Alternative: org.apache.commons.lang.StringUtils.repeat
    // Note: I guess performance wise, this is probably way worse than Apache's repeat
    public static String repeat(String what, int times) {
        if (times <= 0)
            return "";
        StringBuilder sb = new StringBuilder();
        int i;
        for (i = 0; i < times; i++)
            sb.append(what);
        return sb.toString();
    }

    // Alternative: org.apache.commons.lang.StringUtils.join
    // Note: do keep in mind that RequestPrinter.join will add single-quotes to values
    public static String join(List<String> values, String conjuction) {
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            sb.append(APOSTROPHE).append(value).append(APOSTROPHE).append(conjuction);
        }
        sb.delete(sb.length() - conjuction.length(), sb.length());
        return sb.toString();
    }

    public static String join(String[] values, String conjuction) {
        return RequestPrinter.join(Arrays.asList(values), conjuction);
    }


    /**
     * Debug request's headers
     *
     * @param request Request parameter.
     * @return A string with debug information on Request's header
     */
    public static String debugStringHeaders(HttpServletRequest request) {
        return RequestPrinter.debugStringHeaders(request, 0);
    }

    /**
     * Debug request's parameters
     *
     * @param request Request parameter.
     * @return A string with debug information on Request's header
     */
    public static String debugStringParameters(HttpServletRequest request) {
        return RequestPrinter.debugStringParameters(request, 0);
    }

    /**
     * Debug request's cookies
     *
     * @param request Request parameter
     * @return A string with debug information on Request's cookies
     */
    public static String debugStringCookies(HttpServletRequest request) {
        return RequestPrinter.debugStringCookies(request, 0);
    }

    /**
     * @param session La sesión HTTP para obtener los parámetros
     * @return El string que se obtiene de la sesión para depurar el estado.
     */
    public static String debugStringSession(HttpSession session) {
        return RequestPrinter.debugStringSession(session, 0);
    }

    /**
     * Debug complete request
     *
     * @param request      Request parameter.
     * @param printSession Enable session information printing
     * @return A string with debug information on Request's header
     */
    public static String debugString(HttpServletRequest request, boolean printSession) {
        StringBuilder sb = new StringBuilder();

        // GENERAL INFO
        sb.append(debugStringGeneralInfo(request));

        // COOKIES
        sb.append("COOKIES:\n");
        sb.append(LINE_SEPARATOR);
        sb.append(RequestPrinter.debugStringCookies(request, 1));

        // PARAMETERS
        sb.append("PARAMETERS:\n");
        sb.append(LINE_SEPARATOR);
        sb.append(RequestPrinter.debugStringParameters(request, 1));

        // HEADERS
        sb.append("HEADERS:\n");
        sb.append(LINE_SEPARATOR);
        sb.append(RequestPrinter.debugStringHeaders(request, 1));

        // SESSION
        if (printSession) {
            sb.append("SESSION:\n");
            sb.append(LINE_SEPARATOR);
            HttpSession session = request.getSession(false);
            if (session != null) {
                sb.append(RequestPrinter.debugStringSession(session, 1));
            } else {
                sb.append("NO SESSION AVAILABLE\n");
            }
        }

        return sb.toString();
    }

    public static String debugStringGeneralInfo(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("PROTOCOL: ").append(request.getProtocol()).append("\n");
        sb.append("METHOD: ").append(request.getMethod()).append("\n");
        sb.append("QUERY STRING: ").append(request.getQueryString()).append("\n");
        sb.append("REQUEST URI: ").append(request.getRequestURI()).append("\n");
        return sb.toString();
    }


    /**
     * Call debugString with 'false' value on session information
     *
     * @param request Request parameter.
     * @return A string with debug information on Request's header but no information on session
     */
    public static String debugString(HttpServletRequest request) {
        return RequestPrinter.debugString(request, false);
    }


    public static void main(String[] args) {
        List<String> strs = new ArrayList<>();
        strs.add("Hello");
        LOGGER.debug(RequestPrinter.join(strs, ",\n"));
    }

}
