package socialnetwork.model;

import socialnetwork.utils.Constants;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class EventDTO extends Event {
    private String beginDate;
    private Long nr_days_remaining;
    private String information;

    public EventDTO(Event event) {
        super(event.getName(), event.getDate(), event.getHost(), event.getLocation(), event.getDescription());
        this.beginDate = event.getDate().format(Constants.DATE_TIME_FORMATTER);
        nr_days_remaining = ChronoUnit.DAYS.between(LocalDateTime.now(), event.getDate());
        this.information = this.toString();
        this.setId(event.getId());
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public Long getNr_days_remaining() {
        return nr_days_remaining;
    }

    public void setNr_days_remaining(Long nr_days_remaining) {
        this.nr_days_remaining = nr_days_remaining;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    @Override
    public String toString() {
        return "There are " + nr_days_remaining + " days left until the \"" + getName() + "\" event!";
    }
}
