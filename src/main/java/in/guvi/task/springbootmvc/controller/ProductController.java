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

@Controller
@AllArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public String productPage() {
        return "productPage";
    }

    @GetMapping("/displayProduct")
    public String displayAllProducts(Model model) {
        List<ProductResponseDto> productList = productService.displayProducts();
        model.addAttribute("products", productList);
        return "product/displayProductsPage";
    }

    @GetMapping("/addProduct")
    public String addProductPage(Model model) {
        model.addAttribute("product", new ProductRequestDto());
        return "product/addProductPage";
    }

    @PostMapping("/addProduct")
    public String addProduct(@Valid @ModelAttribute("product") ProductRequestDto requestDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "product/addProductPage";
        }
        productService.saveProduct(requestDto);
        return "redirect:/product/displayProduct";
    }

    @GetMapping("/edit/{id}")
    public String editProductPage(@PathVariable Long id, Model model) {
        // Fetch existing data and send it to the form
        model.addAttribute("product", productService.getProductByIdForUpdate(id));
        model.addAttribute("productId", id); // We need the ID to build the POST URL
        return "product/updateProductPage";
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Long id, @Valid @ModelAttribute("product") ProductRequestDto requestDto, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("productId", id);
            return "product/updateProductPage";
        }
        productService.updateProduct(id, requestDto);
        return "redirect:/product/displayProduct";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/product/displayProduct";
    }
}
