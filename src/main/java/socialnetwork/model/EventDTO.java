package socialnetwork.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class EventDTO extends Entity<Long> {
    private Long id;
    private String name;
    private LocalDateTime date;
    private Long nr_days_remaining;

    public EventDTO(Long id, String name, LocalDateTime date) {
        this.id = id;
        this.name = name;
        this.date = date;
        nr_days_remaining = ChronoUnit.DAYS.between(date, LocalDateTime.now());
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
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

    public Long getNr_days_remaining() {
        return nr_days_remaining;
    }

    public void setNr_days_remaining(Long nr_days_remaining) {
        this.nr_days_remaining = nr_days_remaining;
    }

    @Override
    public String toString() {
        return "There are " + nr_days_remaining + " days left until the \"" + name + "\" event!";
    }
}
