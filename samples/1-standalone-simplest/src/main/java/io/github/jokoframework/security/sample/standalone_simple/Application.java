package io.github.jokoframework.security.sample.standalone_simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@EnableScheduling
//@EnableAsync
//@EnableCaching
@ComponentScan(basePackages={"io.github.jokoframework.security","io.github.jokoframework.security.sample.standalone_simple"})
@EnableJpaRepositories(basePackages={"io.github.jokoframework.security"})
@EntityScan(basePackages={"io.github.jokoframework.security"})
public class Application extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
	}
}