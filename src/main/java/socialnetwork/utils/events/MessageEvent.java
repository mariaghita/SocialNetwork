package socialnetwork.utils.events;

import socialnetwork.model.Message;

public class MessageEvent implements Event{
    private MessageEventType type;
    Message message;

    public MessageEvent(MessageEventType type, Message message) {
        this.type = type;
        this.message = message;
    }

    public MessageEventType getType() {
        return type;
    }

    public void setType(MessageEventType type) {
        this.type = type;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
