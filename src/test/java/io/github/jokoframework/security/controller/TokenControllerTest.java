package io.github.jokoframework.security.controller;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.github.jokoframework.common.dto.JokoTokenInfoResponse;
import io.github.jokoframework.security.services.ITokenService;

public class TokenControllerTest {
	
	protected MockMvc mockMvc;
	
	@InjectMocks
	private TokenController controller;
	
	@Mock
	private ITokenService tokenService;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}
	
	@Test
	public void requestingTokenValidationShouldReturnIsValid() {
		JokoTokenInfoResponse response = new JokoTokenInfoResponse.Builder()
				.audience("some audience")
		        .userId("some user")
		        .expiresIn(123)
		        .revoked(false)
		        .build();
	    String accessToken = "not.valid.token";
		when(tokenService.tokenInfo(accessToken)).thenReturn(response);
	}
	
}
