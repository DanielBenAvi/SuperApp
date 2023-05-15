package superapp.miniapps.chat.objects;

import java.util.Date;

public class Message {

    private String id;
    private String to;
    private String from;
    private String message;
    private Date sentDate;

    public Message() {}


    public String getId() {
        return id;
    }

    public Message setId(String id) {
        this.id = id;
        return this;
    }

    public String getTo() {
        return to;
    }

    public Message setTo(String to) {
        this.to = to;
        return this;
    }

    public String getFrom() {
        return from;
    }

    public Message setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Message setMessage(String message) {
        this.message = message;
        return this;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public Message setSentDate(Date sentDate) {
        this.sentDate = sentDate;
        return this;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", to='" + to + '\'' +
                ", from='" + from + '\'' +
                ", message='" + message + '\'' +
                ", sentDate=" + sentDate +
                '}';
    }
}
