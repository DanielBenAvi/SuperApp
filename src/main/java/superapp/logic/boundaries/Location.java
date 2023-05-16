package superapp.logic.boundaries;

public class Location {

	private Double lat;
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
