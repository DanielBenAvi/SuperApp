package superapp.miniapps.datingMiniApp;

public class Match {

	private String user1;
	private String user2;
	
	public Match() {
	}

	public String getUser1() {
		return user1;
	}

	public Match setUser1(String user1) {
		this.user1 = user1;
		return this;
	}

	public String getUser2() {
		return user2;
	}

	public Match setUser2(String user2) {
		this.user2 = user2;
		return this;
	}

	@Override
	public String toString() {
		return "Match [user1=" + user1 + ", user2=" + user2 + "]";
	}
	
}
