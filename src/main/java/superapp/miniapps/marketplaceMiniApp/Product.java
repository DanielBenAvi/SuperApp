package superapp.miniapps.marketplaceMiniApp;


import java.util.Arrays;

public class Product {

    private String name;
    private String description;
    private String[] preferences;

    private double price;
    private String imageUrl;


    public Product() {
    }


    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Product setDescription(String description) {
        this.description = description;
        return this;
    }

    public String[] getPreferences() {
        return preferences;
    }

    public Product setPreferences(String[] preferences) {
        this.preferences = preferences;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public Product setPrice(double price) {
        this.price = price;
        return this;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public Product setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", category=" + Arrays.toString(preferences) +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}