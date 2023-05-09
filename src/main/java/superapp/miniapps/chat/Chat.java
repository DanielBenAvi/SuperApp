package superapp.miniapps.chat;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    private List<Message> messages;

    public Chat() {
        this.messages = new ArrayList<>();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Chat setMessages(List<Message> messages) {
        this.messages = messages;
        return this;
    }

    @Override
    public String toString() {

        return "Chat{" +
                "messages=" + messages +
                '}';
    }
}
