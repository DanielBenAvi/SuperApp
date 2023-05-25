package superapp.miniapps.datingMiniApp;

import superapp.miniapps.chat.objects.Chat;

public class MatchEntity {

	private String profileDatingId1;
	private String profileDatingId2;
	private Chat chat;

	public MatchEntity() {
		this.chat = new Chat();
	}


	public MatchEntity(String profileDatingId1, String profileDatingId2) {
		this.profileDatingId1 = profileDatingId1;
		this.profileDatingId2 = profileDatingId2;
		this.chat = new Chat();
	}

	public String getProfileDatingId1() {
		return profileDatingId1;
	}

	public MatchEntity setProfileDatingId1(String profileDatingId1) {
		this.profileDatingId1 = profileDatingId1;
		return this;
	}

	public String getProfileDatingId2() {
		return profileDatingId2;
	}

	public MatchEntity setProfileDatingId2(String profileDatingId2) {
		this.profileDatingId2 = profileDatingId2;
		return this;
	}

	public Chat getChat() {
		return chat;
	}

	public MatchEntity setChat(Chat chat) {
		this.chat = chat;
		return this;
	}

	@Override
	public String toString() {
		return "MatchEntity{" +
				"profileDatingId1='" + profileDatingId1 + '\'' +
				", profileDatingId2='" + profileDatingId2 + '\'' +
				", chat=" + chat +
				'}';
	}
}
