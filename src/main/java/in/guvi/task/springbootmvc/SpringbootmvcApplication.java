package in.guvi.task.springbootmvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class SpringbootmvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootmvcApplication.class, args);
	}

}
