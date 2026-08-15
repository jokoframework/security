package io.github.jokoframework.security.util;

import io.github.jokoframework.common.JokoUtils;
import io.github.jokoframework.security.constantes.JokoConstants;
import io.github.jokoframework.security.controller.SecurityConstants;
import io.github.jokoframework.security.springex.JokoSecurityFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper para encapsular la logica de HttpServletRequest API
 */
public class JokoRequestContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(JokoRequestContext.class);

    private String userAgent;
    private String remoteAddress;
    private int version;
    private String token;
    private HttpServletRequest request;

    public JokoRequestContext(HttpServletRequest pRequest) {
        setRequest(pRequest);
        setUserAgent(getRequest().getHeader(JokoConstants.HEADER_USER_AGENT));
        setRemoteAddress(JokoUtils.getClientIpAddr(getRequest()));
        String versionStr = getRequest().getHeader(SecurityConstants.VERSION_HEADER_NAME);
        version = 10;
        if (!StringUtils.isEmpty(versionStr)) {
            try {
                version = (int) (Double.parseDouble(versionStr) * 10);
            } catch (NumberFormatException e) {
                LOGGER.warn("Incorrect version header {}", versionStr);
            }
        }
        token = JokoSecurityFilter.getTokenFromHeader(getRequest());
    }

    public String getUserAgent() {
        if(StringUtils.isBlank(userAgent)) {
            userAgent = JokoConstants.NOT_AVAILABLE;
        }
        return userAgent;
    }

    public String getRemoteAddress() {
        if(StringUtils.isBlank(remoteAddress)) {
            remoteAddress = JokoConstants.NOT_AVAILABLE;
        }
        return remoteAddress;
    }

    public int getVersion() {
        return version;
    }

    public String getVersionString() {
        return String.format(Locale.US, "v%.1f", version / 10d);
    }

    public String getToken() {
        return token;
    }

    public String getSession() {
        return SecurityUtils.sha256(getUserAgent() + ":" + getRemoteAddress() + ":" + getRequest().getSession().getId());
    }

    //BEGIN-IGNORE-SONARQUBE
    public String getBrowserName() {
        Pattern pattern = Pattern.compile("([\\w ]+)/([\\d\\.]+)");
        Matcher matcher = pattern.matcher(getUserAgent());

        String agent = JokoConstants.NOT_AVAILABLE;
        String versionLocal = JokoConstants.NOT_AVAILABLE;
        while (matcher.find()) {
            agent = matcher.group(1);
            versionLocal = matcher.group(2);
            if (JokoConstants.FIREFOX.equals(agent) && !getUserAgent().contains(JokoConstants.SEAMONKEY)) {
                break;
            } else if (JokoConstants.SEAMONKEY.equals(agent)) {
                break;
            } else if (JokoConstants.CHROME.equals(agent) && !getUserAgent().contains(JokoConstants.CHROMIUM)) {
                break;
            } else if (agent != null && agent.contains(JokoConstants.CHROMIUM)) {
                break;
            } else if (JokoConstants.SAFARI.equals(agent) && !getUserAgent().contains(JokoConstants.CHROME) && !getUserAgent().contains(JokoConstants.CHROMIUM)) {
                break;
            } else if (JokoConstants.OPR.equals(agent) || JokoConstants.OPERA.equals(agent)) {
                agent = JokoConstants.OPERA;
                break;
            } else if (getUserAgent().contains(JokoConstants.MSIE)) {
                agent = JokoConstants.INTERNET_EXPLORER;
                break;
            }
        }
        return String.format("%s %s", agent, versionLocal);
    }

    public void setUserAgent(String pUserAgent) {
        userAgent = pUserAgent;
    }

    public void setRemoteAddress(String pRemoteAddress) {
        remoteAddress = pRemoteAddress;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest pRequest) {
        request = pRequest;
    }
    //END-IGNORE-SONARQUBE
}
