package io.github.jokoframework.security.springex;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jokoframework.common.JokoUtils;
import io.github.jokoframework.common.dto.JokoBaseResponse;
import io.github.jokoframework.security.controller.SecurityConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JokoAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(JokoAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        String username = "Unknown user ";
        if (principal != null) {
            username = (String) principal;
        }

        String uri = request.getRequestURI();
        LOGGER.warn(username + " Tried to access resource " + uri + ", but it doesn't have enough access rights");


        JokoBaseResponse error = new JokoBaseResponse(SecurityConstants.ERROR_NOT_ALLOWED);
        error.setMessage("Not authorized to execute " + JokoUtils.formatLogString(uri));

        ObjectMapper mapper = new ObjectMapper();

        response.setHeader("Content-type", "application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        PrintWriter out = response.getWriter();
        mapper.writeValue(out, error);
    }

}
