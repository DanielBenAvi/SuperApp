package superapp.miniapps.marketplaceMiniApp;


import java.util.List;

public class Supplier {
	
	private List<Product> products;
	private String businessName;

	private String phoneNumber;

	public Supplier() {
	}

	public List<Product> getProducts() {
		return products;
	}

	public Supplier setProducts(List<Product> products) {
		this.products = products;
		return this;
	}

	public Supplier addProduct(Product product) {
		if (productIsValid(product)) {
			products.add(product);
		}
		return this;
	}

	private boolean productIsValid(Product product) {
		if (product == null) {
			System.out.println("Invalid product. Product object is null.");
			return false;
		}

		if (products.contains(product)) {
			System.out.println("Invalid product. Product already exists.");
			return false;
		}

		if (product.getName() == null || product.getName().isEmpty()) {
			System.out.println("Invalid product. Name is required.");
			return false;
		}

		if (product.getPrice() <= 0) {
			System.out.println("Invalid product. Price must be a positive value.");
			return false;
		}

		if (product.getCategory() == null || product.getCategory().isEmpty()) {
			System.out.println("Invalid product. Category is required.");
			return false;
		}

		if (!isValidCurrency(product.getCurrency())) {
			System.out.println("Invalid product. Currency is not valid.");
			return false;
		}

		return true;
	}

	private boolean isValidCurrency(Product.Currency currency) {
		for (Product.Currency validCurrency : Product.Currency.values()) {
			if (validCurrency == currency) {
				return true;
			}
		}
		return false;
	}

	public Supplier removeProduct(Product product) {
		products.remove(product);
		return this;
	}
	public String getBusinessName() {
		return businessName;
	}

	public Supplier setBusinessName(String businessName) {
		this.businessName = businessName;
		return this;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String toString() {
		return "Supplier [products=" + products + ", businessName=" + businessName + "]";
	}

}
