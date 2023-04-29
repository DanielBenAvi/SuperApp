package superapp.miniapps.marketplaceMiniApp;

import java.util.Arrays;

public class Supplier {
	
	private Product[] products;
	private String businessName;
	
	public Supplier() {
	}

	public Product[] getProducts() {
		return products;
	}

	public Supplier setProducts(Product[] products) {
		this.products = products;
		return this;
	}

	public String getBusinessName() {
		return businessName;
	}

	public Supplier setBusinessName(String businessName) {
		this.businessName = businessName;
		return this;
	}

	@Override
	public String toString() {
		return "Supplier [products=" + Arrays.toString(products) + ", businessName=" + businessName + "]";
	}

}
