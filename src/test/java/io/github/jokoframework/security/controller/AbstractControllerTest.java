package io.github.jokoframework.security.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractControllerTest {

	private final static ObjectMapper mapper = new ObjectMapper();
	
	protected String mapToJson(Object object) throws JsonProcessingException {
		return mapper.writeValueAsString(object);
	}
}
