package socialnetwork.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Event extends Entity<Long> {
    private String name;
    private LocalDateTime date;
    private String host;
    private String description;
    private String location;

    public Event(String name, LocalDateTime date, String host, String description, String location) {
        this.name = name;
        this.date = date;
        this.host = host;
        this.description = description;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return getDescription().equals(event.getDescription()) && getId().equals(event.getId()) && getName().equals(event.getName()) && getDate().equals(event.getDate()) && getHost().equals(event.getHost()) && Objects.equals(getLocation(), event.getLocation());
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + this.getId() +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", host='" + host + '\'' +
                ", description=" + description +
                ", location='" + location + '\'' +
                '}';
    }
}
