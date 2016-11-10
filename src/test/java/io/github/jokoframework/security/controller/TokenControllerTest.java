package io.github.jokoframework.security.controller;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.jokoframework.common.dto.JokoTokenInfoResponse;
import io.github.jokoframework.security.ApiPaths;
import io.github.jokoframework.security.services.ITokenService;

public class TokenControllerTest {
	
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
		        .revoked(false)
		        .build();
	    String accessToken = NOT_VALID_TOKEN;
		when(tokenService.tokenInfo(accessToken)).thenReturn(response);
		
		mockMvc.perform(get(ApiPaths.TOKEN_INFO + "?accessToken={accessToken}", accessToken))
			.andExpect(status().isOk())
			.andExpect(content().json(mapToJson(response)));
		
		verify(tokenService).tokenInfo(accessToken);
	}

	private String mapToJson(Object object) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(object);
	}
	
}
