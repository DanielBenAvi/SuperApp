package superapp.logic.boundaries;

public class Location {
	
	private double lat;
	private double lng;

	public Location() {
	}

	public Location(double lat, double lng) {
		this.lat = lat;
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public Location setLat(double lat) {
		this.lat = lat;
		return this;
	}

	public double getLng() {
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
