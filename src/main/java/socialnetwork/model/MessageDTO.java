package socialnetwork.model;


import socialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.util.List;

public class MessageDTO extends Entity<Long>{
    private User from;
    private List<User> to;
    private LocalDateTime date;
    private MessageDTO reply;
    private String text;
    private String fullName;

    public MessageDTO(User from, List<User> to, LocalDateTime date, MessageDTO reply, String text) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.reply = reply;
        this.text = text;
        this.fullName = from.getFirstName()+ " " + from.getLastName();
    }

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    public List<User> getTo() {
        return to;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    public String getDate() {
        return date.format(Constants.DATE_TIME_FORMATTER);
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public MessageDTO getReply() {
        return reply;
    }

    public void setReply(MessageDTO reply) {
        this.reply = reply;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "\nfrom=" + from +
                "\nto=" + to +
                "\ndate=" + date +
                "\nreply=" + reply +
                "\ntext='" + text + '\'' +
                '}';
    }
}
