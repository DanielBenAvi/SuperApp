package superapp.miniapps.datingMiniApp;

import superapp.logic.boundaries.ObjectId;
import superapp.miniapps.chat.objects.Chat;

public class MatchBoundary {

	private ObjectId profileDatingId1;
	private ObjectId profileDatingId2;

	public MatchBoundary() {}

	public MatchBoundary(ObjectId profileDatingId1, ObjectId profileDatingId2) {
		this.profileDatingId1 = profileDatingId1;
		this.profileDatingId2 = profileDatingId2;
	}

	public ObjectId getProfileDatingId1() {
		return profileDatingId1;
	}

	public MatchBoundary setProfileDatingId1(ObjectId profileDatingId1) {
		this.profileDatingId1 = profileDatingId1;
		return this;
	}

	public ObjectId getProfileDatingId2() {
		return profileDatingId2;
	}

	public MatchBoundary setProfileDatingId2(ObjectId profileDatingId2) {
		this.profileDatingId2 = profileDatingId2;
		return this;
	}

	@Override
	public String toString() {
		return "MatchBoundary{" +
				"profileDatingId1=" + profileDatingId1 +
				", profileDatingId2=" + profileDatingId2 +
				'}';
	}
}
