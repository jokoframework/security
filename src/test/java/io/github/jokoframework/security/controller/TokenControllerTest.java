package io.github.jokoframework.security.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.github.jokoframework.common.dto.JokoTokenInfoResponse;
import io.github.jokoframework.security.ApiPaths;
import io.github.jokoframework.security.services.ITokenService;

public class TokenControllerTest extends AbstractControllerTest {
	
	private static final String NOT_VALID_TOKEN = "not.valid.token";

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
	public void requestingTokenInfoShouldReturnJsonWithInfo() throws Exception {
		JokoTokenInfoResponse response = new JokoTokenInfoResponse.Builder()
				.audience("some audience")
		        .userId("some user")
		        .expiresIn(123L)
		        .success(Boolean.TRUE)
		        .build();
	    String accessToken = NOT_VALID_TOKEN;
		when(tokenService.tokenInfo(accessToken)).thenReturn(response);
		
		mockMvc.perform(get(ApiPaths.TOKEN_INFO + "?accessToken={accessToken}", accessToken))
			.andExpect(status().isOk())
			.andDo(print())
			.andExpect(content().json(this.mapToJson(response)));
			
		verify(tokenService).tokenInfo(accessToken);
	}

	
	
}
