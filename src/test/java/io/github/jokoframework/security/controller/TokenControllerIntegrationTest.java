package io.github.jokoframework.security.controller;

import static io.github.jokoframework.security.SecurityTestConstants.EXPIRATION_SECURITY_PROFILE;
import static io.github.jokoframework.security.SecurityTestConstants.REMOTE_IP;
import static io.github.jokoframework.security.SecurityTestConstants.ROLES;
import static io.github.jokoframework.security.SecurityTestConstants.SECURITY_PROFILE;
import static io.github.jokoframework.security.SecurityTestConstants.USER;
import static io.github.jokoframework.security.SecurityTestConstants.USER_AGENT;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import io.github.jokoframework.common.dto.JokoBaseResponse;
import io.github.jokoframework.security.ApiPaths;
import io.github.jokoframework.security.Application;
import io.github.jokoframework.security.JokoJWTExtension.TOKEN_TYPE;
import io.github.jokoframework.security.JokoTokenWrapper;
import io.github.jokoframework.security.SecurityMockObjects;
import io.github.jokoframework.security.errors.JokoUnauthenticatedException;
import io.github.jokoframework.security.services.ITokenService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Transactional
public class TokenControllerIntegrationTest extends AbstractControllerTest {

	private static final Integer DEFAULT_PROFILE_EXPIRATION_IN_SECONDS = 14440;

	private MockMvc mockMvc;
	
	@Autowired
	private WebApplicationContext wac;
	
	@Autowired
	private ITokenService tokenService;
	
	private JokoBaseResponse revokedResponse;

	private JokoBaseResponse expiredResponse;
	
	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		
		revokedResponse = SecurityMockObjects.buildJokoErrorResponse(
					JokoUnauthenticatedException.ERROR_REVOKED_TOKEN, 
					JokoUnauthenticatedException.DEFAULT_ERROR_MSG
				);
		
		expiredResponse = SecurityMockObjects.buildJokoErrorResponse(
				JokoUnauthenticatedException.ERROR_EXPIRED_TOKEN, 
				JokoUnauthenticatedException.DEFAULT_ERROR_MSG
			);
		
		
	}
	
	@Test
	//@Ignore
	//FIXME ver issue https://github.com/jokoframework/security/issues/15
	public void requestingTokenInfoShouldReturnOk() throws Exception {
		// 1. Creamos el refresh token
    	JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(USER, SECURITY_PROFILE, TOKEN_TYPE.REFRESH,
                USER_AGENT, REMOTE_IP, ROLES);
       
    	// 2. Pedimos información del token
    	mockMvc.perform(get(ApiPaths.TOKEN_INFO + "?accessToken={accessToken}", token.getToken()))
    	.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.userId", is(USER)))
    	.andExpect(jsonPath("$.expiresIn", lessThan(DEFAULT_PROFILE_EXPIRATION_IN_SECONDS)));
    	
	}
	
	@Test
	public void requestingRevokedTokenShouldReturnUnauthorized() throws Exception {
		// 1. Creamos el refresh token
    	JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(USER, SECURITY_PROFILE, TOKEN_TYPE.REFRESH,
                USER_AGENT, REMOTE_IP, ROLES);
       
    	// 2. Revocamos 
    	tokenService.revokeToken(token.getClaims().getId());
    	
    	// 3. Pedimos información del token
    	mockMvc.perform(get(ApiPaths.TOKEN_INFO + "?accessToken={accessToken}", token.getToken()))
    	.andDo(print())
		.andExpect(status().isUnauthorized())
		.andExpect(content().json(this.mapToJson(revokedResponse)));
	}
	
	@Test
	public void requestingExpiredTokenShouldReturnUnauthorized() throws Exception {
		// 1. Creamos el refresh token que tiene 0 segundos como expiración
    	JokoTokenWrapper token = tokenService.createAndStoreRefreshToken(USER, EXPIRATION_SECURITY_PROFILE, TOKEN_TYPE.REFRESH,
                USER_AGENT, REMOTE_IP, ROLES);
       
    	// 2. Pedimos información del token
    	mockMvc.perform(get(ApiPaths.TOKEN_INFO + "?accessToken={accessToken}", token.getToken()))
    	.andDo(print())
		.andExpect(status().isUnauthorized())
		.andExpect(content().json(this.mapToJson(expiredResponse)));
	}
}
