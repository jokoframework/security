package io.github.jokoframework.security.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import io.github.jokoframework.common.JokoUtils;
import io.github.jokoframework.security.controller.SecurityConstants;
import io.github.jokoframework.security.springex.JokoSecurityFilter;

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
		request = pRequest;
		userAgent = request.getHeader("User-agent");
		remoteAddress = JokoUtils.getClientIpAddr(request);
		String versionStr = request.getHeader(SecurityConstants.VERSION_HEADER_NAME);
		version = 10;
		if (!StringUtils.isEmpty(versionStr)) {
			try {
				version = (int) (Double.parseDouble(versionStr) * 10);
			} catch (NumberFormatException e) {
				LOGGER.warn("Incorrect version header {}", versionStr);
			}
		}
		token = JokoSecurityFilter.getTokenFromHeader(request);
	}

	public String getUserAgent() {
		return userAgent;
	}

	public String getRemoteAddress() {
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
		return SecurityUtils.sha256(userAgent + ":" + remoteAddress + ":" + request.getSession().getId());
	}

	public String getBrowserName() {
		Pattern p = Pattern.compile("([\\w ]+)/([\\d\\.]+)");
		Matcher m = p.matcher(userAgent);

		String agent = "N/A";
		String version = "N/A";
		while (m.find()) {
			agent = m.group(1);
			version = m.group(2);
			if (agent.equals("Firefox") && !userAgent.contains("Seamonkey")) {
				break;
			} else if (agent.equals("Seamonkey")) {
				break;
			} else if (agent.equals("Chrome") && !userAgent.contains("Chromium")) {
				break;
			} else if (agent.contains("Chromium")) {
				break;
			} else if (agent.equals("Safari") && !userAgent.contains("Chrome") && !userAgent.contains("Chromium")) {
				break;
			} else if (agent.equals("OPR") || agent.equals("Opera")) {
				agent = "Opera";
				break;
			} else if (userAgent.contains("MSIE")) {
				agent = "Internet Explorer";
				break;
			}
		}
		return String.format("%s %s", agent, version);
	}
}
