package in.guvi.task.springbootmvc.controller;

import in.guvi.task.springbootmvc.dto.ProductRequestDto;
import in.guvi.task.springbootmvc.dto.ProductResponseDto;
import in.guvi.task.springbootmvc.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MVC Controller handling all web requests related to the Product feature.
 *
 * <p>Provides full CRUD (Create, Read, Update, Delete) operations for products
 * through Thymeleaf-rendered HTML pages. All endpoints are prefixed with {@code /product}.
 *
 * <p>{@code @AllArgsConstructor} (Lombok) generates a constructor for all fields,
 * enabling Spring to inject the {@link ProductService} dependency automatically.
 *
 * <p>This controller follows the MVC pattern — it delegates all business and
 * persistence logic to the service layer and only handles HTTP request/response wiring.
 */
@Controller
@AllArgsConstructor
@RequestMapping("/product")
public class ProductController {

    /** Service layer responsible for product business logic and database interactions. */
    private final ProductService productService;

    /**
     * GET /product
     *
     * <p>Placeholder endpoint — currently returns a view name {@code "productPage"}
     * which can be used as an intermediate landing page for the product module.
     *
     * @return the Thymeleaf view name for the product module landing page
     */
    @GetMapping
    public String productPage() {
        return "productPage";
    }

    /**
     * GET /product/displayProduct
     *
     * <p>Fetches all product records from the database via the service layer and
     * exposes them to the Thymeleaf template as a list of {@link ProductResponseDto} objects.
     * The template iterates over this list to render a product catalog table/grid.
     *
     * @param model Spring's {@link Model} used to pass data attributes to the view
     * @return the Thymeleaf view name for the product display/list page
     */
    @GetMapping("/displayProduct")
    public String displayAllProducts(Model model) {
        // Fetch all products from service (entity → DTO mapping happens in service)
        List<ProductResponseDto> productList = productService.displayProducts();
        // Expose the list to the template under the key "products"
        model.addAttribute("products", productList);
        return "product/displayProductsPage";
    }

    /**
     * GET /product/addProduct
     *
     * <p>Renders the blank "Add Product" form.
     * An empty {@link ProductRequestDto} is added to the model so that Thymeleaf
     * can bind form fields using {@code th:object} and {@code th:field}.
     *
     * @param model Spring's {@link Model} used to pass the empty DTO to the view
     * @return the Thymeleaf view name for the add-product form page
     */
    @GetMapping("/addProduct")
    public String addProductPage(Model model) {
        // Bind an empty DTO so Thymeleaf can use th:field to link input fields
        model.addAttribute("product", new ProductRequestDto());
        return "product/addProductPage";
    }

    /**
     * POST /product/addProduct
     *
     * <p>Processes the submitted "Add Product" form.
     * {@code @Valid} triggers Bean Validation on the {@link ProductRequestDto};
     * any constraint violations are captured in {@link BindingResult}.
     * <ul>
     *   <li>On validation failure — re-render the form with field-level error messages.</li>
     *   <li>On success — persist the product and redirect to the product list (PRG pattern).</li>
     * </ul>
     *
     * @param requestDto    form data bound to a {@link ProductRequestDto}
     * @param bindingResult holds any validation errors triggered by {@code @Valid}
     * @return add-product form (on failure) or redirect to product list (on success)
     */
    @PostMapping("/addProduct")
    public String addProduct(@Valid @ModelAttribute("product") ProductRequestDto requestDto,
                             BindingResult bindingResult) {
        // Validation failed — return to the form so error messages are displayed
        if (bindingResult.hasErrors()) {
            return "product/addProductPage";
        }
        // Delegate to service: map DTO → Product entity and persist
        productService.saveProduct(requestDto);
        // PRG (Post/Redirect/Get) pattern prevents duplicate form submission on refresh
        return "redirect:/product/displayProduct";
    }

    /**
     * GET /product/edit/{id}
     *
     * <p>Fetches the current data of a product by its ID and pre-populates the edit form.
     * The product ID is extracted from the URL path variable and passed to the template
     * so it can construct the correct POST action URL for the update form submission.
     *
     * @param id    the unique database ID of the product to edit (from URL path)
     * @param model Spring's {@link Model} used to pass pre-populated data to the view
     * @return the Thymeleaf view name for the product update/edit form page
     */
    @GetMapping("/edit/{id}")
    public String editProductPage(@PathVariable Long id, Model model) {
        // Fetch existing data and send it to the form so fields are pre-filled
        model.addAttribute("product", productService.getProductByIdForUpdate(id));
        // We need the ID to build the POST URL in the template: th:action="@{/product/edit/{id}}"
        model.addAttribute("productId", id);
        return "product/updateProductPage";
    }

    /**
     * POST /product/edit/{id}
     *
     * <p>Processes the submitted product update form.
     * On validation failure, the edit page is re-rendered with errors, and the product ID
     * is re-added to the model so the form's POST action URL remains valid.
     * On success, the existing product entity is updated in the database and the user
     * is redirected to the product list (PRG pattern).
     *
     * @param id            the unique database ID of the product to update
     * @param requestDto    updated form data bound to a {@link ProductRequestDto}
     * @param bindingResult holds validation errors (if any)
     * @param model         Spring's {@link Model} used to pass data back on failure
     * @return redirect to product list (on success) or update form (on failure)
     */
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id,
                                @Valid @ModelAttribute("product") ProductRequestDto requestDto,
                                BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            // Re-add productId so the form action URL can still be rendered correctly
            model.addAttribute("productId", id);
            return "product/updateProductPage";
        }
        // Delegate update: fetch existing entity by ID, apply new values, save
        productService.updateProduct(id, requestDto);
        return "redirect:/product/displayProduct";
    }

    /**
     * GET /product/delete/{id}
     *
     * <p>Deletes a product record identified by the given ID.
     * A GET-based delete is used here for simplicity since standard HTML {@code <form>}
     * elements only support GET and POST methods natively.
     * After deletion, the user is redirected back to the product list (PRG pattern).
     *
     * @param id the unique database ID of the product to delete
     * @return redirect to the product display/list page
     */
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/product/displayProduct";
    }
}
