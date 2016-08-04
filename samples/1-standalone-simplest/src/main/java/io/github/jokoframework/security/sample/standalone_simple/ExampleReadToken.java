package io.github.jokoframework.security.sample.standalone_simple;

import java.io.IOException;

import io.github.jokoframework.security.JokoJWTClaims;
import io.github.jokoframework.security.util.JokoTokenParser;

public class ExampleReadToken {

    public static void main(String[] args) throws IOException {
        String secretFile = "/opt/joko/secret.key";
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzdHJpbmciLCJleHAiOjE0NjQyOTAzODUsImlhdCI6MTQ2NDI5MDA4NSwianRpIjoiTlpHUTdEQlM1T1ZKM0c3NlZOTUEiLCJqb2tvIjp7InR5cGUiOiJBQ0NFU1MiLCJyb2xlcyI6WyJib3NzIl0sInByb2ZpbGUiOiJERUZBVUxUIn19.k0MsGv9ZaANGXB7-qa6_dcIMLm-jpiGXecP5ShHKprWTy_iGiWi5RCFq1YCTZ31-_szrHEWl20gJRfhZfwuNvw";

        JokoTokenParser parser = new JokoTokenParser(secretFile);
        JokoJWTClaims tokenClaims = parser.parse(token);
        System.out.println("Token valido " + tokenClaims.getId());
        System.out.println("issue at " + tokenClaims.getIssuedAt());
        System.out.println("expires " + tokenClaims.getExpiration());
    }
}
