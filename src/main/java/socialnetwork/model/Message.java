package socialnetwork.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class Message extends Entity<Long> {
   private String from;
   private List<String> to;
   private LocalDateTime date;
   private Long orginalMessage;
   private String text;

    /**
     * constructor
     * @param from - id of the sender
     * @param to - list of usernames of the recievers
     * @param orginalMessage - id of the original message - if null -> not replying to anyone
     * @param text - the message
     */
    public Message(String from, List<String> to, Long orginalMessage, String text) {
        this.from = from;
        this.to = to;
        this.orginalMessage = orginalMessage;
        this.text = text;
    }

    /**
     * getter for sender
     * @return the username of the sender of the message
     */
    public String getFrom() {
        return from;
    }

    /**
     * setter for sender
     * @param from - username of the sender of the message
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * getter for the receivers
     * @return list with the usernames of the receivers
     */
    public List<String> getTo() {
        return to;
    }

    /**
     * setter for receivers
     * @param to - list of receivers
     */
    public void setTo(List<String> to) {
        this.to = to;
    }

    /**
     * getter for the date
     * @return - the date of the message
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * setter for date
     * @param date - the date of the message
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * getter for the original message
     * @return the id of the original message. if null -> the message does not reply to another message
     */
    public Long getOrginalMessage() {
        return orginalMessage;
    }

    /**
     * setter for the original message
     * @param orginalMessage - the message this message is replying to
     */
    public void setOrginalMessage(Long orginalMessage) {
        this.orginalMessage = orginalMessage;
    }

    /**
     * getter for text
     * @return the text of the message
     */
    public String getText() {
        return text;
    }

    /**
     * setter for text
     * @param text - the text of the message
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * override to toString() function
     * @return a string containing information about the message
     */
    @Override
    public String toString() {
        if(this.orginalMessage == 0)
            return getId() + " | " + this.text + " at " + this.date +
                "\nfrom: " + this.from + " to: " + to;
        else
            return getId() + " | " + this.text + " at " + this.date +
                "\nfrom: " + this.from + " to: " + to + " replying to: " + this.orginalMessage;
    }

}
