package socialnetwork.model;

import java.time.LocalDateTime;

public class UserDTO extends Entity<String> {
    private String firstName;
    private String lastName;
    private LocalDateTime dateOfFriendship;

    public UserDTO(String firstName, String lastName, LocalDateTime dateOfFriendship) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfFriendship = dateOfFriendship;
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

    public LocalDateTime getDateOfFriendship() {
        return dateOfFriendship;
    }

    public void setDateOfFriendship(LocalDateTime dateOfFriendship) {
        this.dateOfFriendship = dateOfFriendship;
    }

    @Override
    public String toString() {
        if(dateOfFriendship == null)
            return "First name : " + this.firstName + " | Last name : " + this.lastName;
        return "First name : " + this.firstName + " | Last name : " + this.lastName + " | Date of friendship : " + this.dateOfFriendship;
    }
}
