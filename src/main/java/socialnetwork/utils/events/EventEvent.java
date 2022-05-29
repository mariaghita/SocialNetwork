package socialnetwork.utils.events;

import socialnetwork.model.EventNotification;
import socialnetwork.model.User;
import socialnetwork.model.Event;

public class EventEvent implements socialnetwork.utils.events.Event {
    private EventEventType type;
    private EventNotification eventNotification;

    public EventEvent(EventEventType type, EventNotification eventNotification) {
        this.eventNotification = eventNotification;
        this.type = type;
    }

    public EventEventType getType() {
        return type;
    }

    public void setType(EventEventType type) {
        this.type = type;
    }

    public EventNotification getEventNotification() {
        return eventNotification;
    }

    public void setEventNotification(EventNotification eventNotification) {
        this.eventNotification = eventNotification;
    }
}
