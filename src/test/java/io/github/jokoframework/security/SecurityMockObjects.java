package io.github.jokoframework.security;

import io.github.jokoframework.common.dto.JokoBaseResponse;
import io.github.jokoframework.security.entities.SecurityProfile;

public class SecurityMockObjects {

	public static SecurityProfile getSecurityProfile(String key) {
		SecurityProfile profile = new SecurityProfile();
		profile.setKey(key);
		profile.setAccessTokenTimeoutSeconds(5 * 60);// 5 min
		profile.setMaxAccessTokenRequests(1);
		profile.setMaxNumberOfDevicesPerUser(2);
		profile.setName("Any name " + key);
		profile.setRefreshTokenTimeoutSeconds(8 * 60 * 60);// 8 hours
		profile.setRevocable(false);
		return profile;
	}

	public static JokoBaseResponse buildJokoErrorResponse(String errorCode, String message) {
		JokoBaseResponse resp = new JokoBaseResponse();
		resp.setSuccess(false);
		resp.setMessage(message);
		resp.setErrorCode(errorCode);
		return resp;
	}
}
