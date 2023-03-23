package marketplaceMiniApp;

import java.util.Arrays;

public class Supplier {
	
	private Product[] products;
	private String businessName;
	
	public Supplier() {
	}

	public Product[] getProducts() {
		return products;
	}

	public void setProducts(Product[] products) {
		this.products = products;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	@Override
	public String toString() {
		return "Supplier [products=" + Arrays.toString(products) + ", businessName=" + businessName + "]";
	}

}
