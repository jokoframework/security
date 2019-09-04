package io.github.jokoframework.security.sample.standalone_simple.config;

import com.google.common.base.Predicate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
@Profile(value = {"default"})
public class SwaggerConfig {


	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("public-api")
				.apiInfo(apiInfo()).select().paths(postPaths()).build();
	}

	private Predicate<String> postPaths() {
		return or(regex("/api/posts.*"), regex("/api/.*"));
	}


	private ApiInfo apiInfo() {
		Contact contact = new Contact("Sodep S.A.", "http://www.sodep.com.py", "joko@sodep.com.py");
		ApiInfo apiInfo = new ApiInfo("Joko-Starter-Kit API",
				"Joko-Starter-Kit API Licence Type",
				"1.1",
				"http://github.com/jokoframework",
				contact,
				"@sodepsa",
				"Apache 2.0",
				Collections.emptyList());
		return apiInfo;
	}
}