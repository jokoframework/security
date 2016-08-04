package io.github.jokoframework.security;

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
}
