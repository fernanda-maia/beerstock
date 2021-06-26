package one.digitalinnovation.beerstock;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BeerstockApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeerstockApplication.class, args);
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(new Info()
				.title("Beer Stock Manager API")
				.version("1.0.0")
				.termsOfService("http://swagger.io/terms")
				.license(new License()
						.name("Apache 2.0")
						.url("http://springdoc.org")));
	}

}
