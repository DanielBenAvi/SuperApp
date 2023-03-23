package demo;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import marketplaceMiniApp.Product;


@RestController
public class MarketplaceController {
	
	/**
	 * Request specific product
	 * @return Product
	 * @author Ido & Yosef
	 */
	@GetMapping(
            path = {"/marketplace/product={productId}"},
            produces = {MediaType.APPLICATION_JSON_VALUE})
	
	public Product product(@PathVariable("productId") String productId) {
		
		return new Product(productId);
	}
	/**
	 * Request all products
	 * @return List of Product
	 * @author Ido & Yosef
	 */
	@RequestMapping(
			path = {"/marketplace/category={categoryName}"},
			method = {RequestMethod.GET},
			produces = {MediaType.APPLICATION_JSON_VALUE})
	public Map<String, Product> products(@PathVariable("categoryName") String categoryName){
		
		Map<String, Product> products = new HashMap<>();
		products.put("1", new Product().setCategory(categoryName).setCurrency("$"));
		products.put("2", new Product().setCategory(categoryName).setDescription("hello world"));
		
		return products;
	}
}
