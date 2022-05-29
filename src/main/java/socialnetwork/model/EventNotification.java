package socialnetwork.model;

public class EventNotification {
    private Long eventid;
    private String username;
    private String status;

    public EventNotification(Long eventid, String username, String status) {
        this.eventid = eventid;
        this.username = username;
        this.status = status;
    }

    public Long getEventid() {
        return eventid;
    }

    public void setEventid(Long eventid) {
        this.eventid = eventid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "EventNotification{" +
                "eventid=" + eventid +
                ", username='" + username + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
