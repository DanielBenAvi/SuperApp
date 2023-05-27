package superapp.logic.boundaries;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import org.springframework.format.annotation.NumberFormat;

public class Location {

	@DecimalMax(value = "90.0") @DecimalMin(value = "-90.0")
	@NumberFormat(style = NumberFormat.Style.NUMBER, pattern = "#0.00")
	private Double lat;

	@DecimalMax(value = "180.0") @DecimalMin(value = "-180.0")
	@NumberFormat(style = NumberFormat.Style.NUMBER, pattern = "#0.00")
	private Double lng;

	public Location() {
	}

	public Location(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public Double getLat() {
		return lat;
	}

	public Location setLat(double lat) {
		this.lat = lat;
		return this;
	}

	public Double getLng() {
		return lng;
	}

	public Location setLng(double lng) {
		this.lng = lng;
		return this;
	}

	@Override
	public String toString() {
		return "Location [lat=" + lat + ", lng=" + lng + "]";
	}

}
