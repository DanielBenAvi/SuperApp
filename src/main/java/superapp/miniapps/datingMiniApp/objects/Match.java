package superapp.miniapps.datingMiniApp.objects;

import superapp.miniapps.chat.objects.Chat;

public class Match {

	private PublicDatingProfile user1;
	private PublicDatingProfile user2;
	private Chat chat;

	public Match() {
		this.chat = new Chat();
	}


	public PublicDatingProfile getUser1() {
		return user1;
	}

	public Match setUser1(PublicDatingProfile user1) {
		this.user1 = user1;
		return this;
	}

	public PublicDatingProfile getUser2() {
		return user2;
	}

	public Match setUser2(PublicDatingProfile user2) {
		this.user2 = user2;
		return this;
	}

	public Chat getChat() {
		return chat;
	}

	public Match setChat(Chat chat) {
		this.chat = chat;
		return this;
	}

	@Override
	public String toString() {
		return "Match{" +
				"user1=" + user1 +
				", user2=" + user2 +
				", chat=" + chat +
				'}';
	}
}
