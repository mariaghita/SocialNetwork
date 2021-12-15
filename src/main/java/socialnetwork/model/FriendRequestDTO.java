package socialnetwork.model;

import socialnetwork.utils.Constants;

import java.time.LocalDateTime;

public class FriendRequestDTO {
    private String firstName,lastName;
    private LocalDateTime dateTime;
    private String status;
    private String username;

    public FriendRequestDTO(String firstName, String lastName, LocalDateTime dateTime) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateTime = dateTime;
        this.status = "pending";
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateTime() {
        return dateTime.format(Constants.DATE_TIME_FORMATTER);
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
