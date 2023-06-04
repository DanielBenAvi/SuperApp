package superapp.miniapps.datingMiniApp;

public class Match {

	private String profileDatingId1;
	private String profileDatingId2;


	public Match() {

	}


	public Match(String profileDatingId1, String profileDatingId2) {
		this.profileDatingId1 = profileDatingId1;
		this.profileDatingId2 = profileDatingId2;
	}

	public String getProfileDatingId1() {
		return profileDatingId1;
	}

	public Match setProfileDatingId1(String profileDatingId1) {
		this.profileDatingId1 = profileDatingId1;
		return this;
	}

	public String getProfileDatingId2() {
		return profileDatingId2;
	}

	public Match setProfileDatingId2(String profileDatingId2) {
		this.profileDatingId2 = profileDatingId2;
		return this;
	}


	@Override
	public String toString() {
		return "MatchEntity{" +
				"profileDatingId1='" + profileDatingId1 + '\'' +
				", profileDatingId2='" + profileDatingId2 + '\'' +
				'}';
	}
}
