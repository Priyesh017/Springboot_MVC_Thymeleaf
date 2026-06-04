package in.guvi.task.springbootmvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Main entry point for the Spring Boot MVC application.
 *
 * <p>{@code @SpringBootApplication} is a convenience annotation that combines:
 * <ul>
 *   <li>{@code @Configuration}      - marks this class as a source of bean definitions</li>
 *   <li>{@code @EnableAutoConfiguration} - tells Spring Boot to start adding beans based on classpath</li>
 *   <li>{@code @ComponentScan}       - tells Spring to scan the current package and sub-packages for components</li>
 * </ul>
 *
 * <p>{@code @EnableWebMvc} explicitly activates the Spring MVC framework, enabling
 * full control over MVC configuration (e.g., message converters, view resolvers).
 */
@SpringBootApplication
@EnableWebMvc
public class SpringbootmvcApplication {

	/**
	 * Application bootstrap method.
	 * Delegates to {@link SpringApplication#run} to launch the embedded Tomcat server,
	 * load the Spring application context, and start all configured beans.
	 *
	 * @param args command-line arguments passed at startup (not used in this app)
	 */
	public static void main(String[] args) {
		SpringApplication.run(SpringbootmvcApplication.class, args);
	}

}
