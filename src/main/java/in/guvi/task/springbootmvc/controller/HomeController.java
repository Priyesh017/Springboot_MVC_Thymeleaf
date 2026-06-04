package in.guvi.task.springbootmvc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller responsible for serving the application's home/landing page.
 *
 * <p>{@code @Controller} (not {@code @RestController}) tells Spring MVC that the return values
 * of handler methods are <em>view names</em>, not raw response bodies. Thymeleaf resolves
 * these view names to HTML templates under {@code src/main/resources/templates/}.
 *
 * <p>{@code @RequiredArgsConstructor} (Lombok) generates a constructor for all
 * {@code final} fields, enabling constructor-based dependency injection without
 * writing boilerplate {@code @Autowired} constructors.
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    /**
     * Handles GET requests to the root URL ("/").
     *
     * <p>Returns the logical view name {@code "homePage"}, which Thymeleaf resolves to
     * {@code src/main/resources/templates/homePage.html}. This page acts as the
     * central dashboard linking users to the Product and Feedback modules.
     *
     * @return the Thymeleaf view name for the home/dashboard page
     */
    @GetMapping("/")
    public String homePage() {
        return "homePage";
    }
}
