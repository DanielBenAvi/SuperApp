package superapp.miniapps.marketplaceMiniApp;


public class Product {
	public enum Currency {
		ILS,
		USD,
		EUR
	}
	private String id;
	private String description;
	private final int maxLengthDescription=100;
	private String name;
	private String category;
	
	private double price;
	private Currency currency;
	
	
	private String imageUrl;
	private String status;
	
	
	public Product() {

	}
	
	public Product(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public Product setId(String id) {
		this.id = id;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Product setDescription(String description) {
		if(description.length() <= maxLengthDescription)
			this.description = description;
		return this;
	}

	public int getMaxDescriptionLength() {
		return maxLengthDescription;
	}

	public String getName() {
		return name;
	}

	public Product setName(String name) {
		this.name = name;
		return this;
	}

	public String getCategory() {
		return category;
	}

	public Product setCategory(String category) {
		this.category = category;
		return this;
	}

	public double getPrice() {
		return price;
	}

	public Product setPrice(double price) {
		this.price = price;
		return this;
	}

	public Currency getCurrency() {
		return currency;
	}

	public Product setCurrency(Currency currency) {
		if (isValidCurrency(currency)) {
			this.currency = currency;
		} else {
			throw new IllegalArgumentException("Invalid currency: " + currency);
		}
		return this;
	}

	private boolean isValidCurrency(Currency currency) {
		for (Currency validCurrency : Currency.values()) {
			if (validCurrency == currency) {
				return true;
			}
		}
		return false;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public Product setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public Product setStatus(String status) {
		this.status = status;
		return this;
	}

	@Override
	public String toString() {
		return "Product{" +
				"id='" + id + '\'' +
				", description='" + description + '\'' +
				", name='" + name + '\'' +
				", category='" + category + '\'' +
				", price=" + price +
				", currency='" + currency + '\'' +
				", imageUrl='" + imageUrl + '\'' +
				", status='" + status + '\'' +
				'}';
	}
}