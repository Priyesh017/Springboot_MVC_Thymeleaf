package in.guvi.task.springbootmvc;

import io.github.cdimascio.dotenv.Dotenv;
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
	 *
	 * <p>Before handing off to Spring, we load the {@code .env} file (if present)
	 * and inject each entry as a Java system property. This makes variables like
	 * {@code DB_URL} available for Spring's {@code ${...}} placeholder resolution
	 * in {@code application.properties}.
	 *
	 * <p>On Render (production), no {@code .env} file exists — dotenv silently
	 * skips loading and the real OS environment variables are used instead.
	 *
	 * @param args command-line arguments passed at startup (not used in this app)
	 */
	public static void main(String[] args) {
		// Load .env into system properties BEFORE Spring reads application.properties
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()   // Safe on Render — no .env file needed in production
				.ignoreIfMalformed() // Skip malformed lines without crashing
				.load();

		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);

		SpringApplication.run(SpringbootmvcApplication.class, args);
	}

}

