package socialnetwork.model;

import socialnetwork.utils.Constants;

import java.time.LocalDateTime;

public class UserDTO extends Entity<String> {
    private String userName;
    private String firstName;
    private String lastName;
    private LocalDateTime dateOfFriendship;
    private String fullName;

    public UserDTO(String userName, String firstName, String lastName, LocalDateTime dateOfFriendship) {
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfFriendship = dateOfFriendship;
        this.fullName = firstName + " " + lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getDateOfFriendship() {
        return dateOfFriendship.format(Constants.DATE_TIME_FORMATTER);
    }

    public LocalDateTime getDateOfFriendshipAsDate() {
        return dateOfFriendship;
    }

    public void setDateOfFriendship(LocalDateTime dateOfFriendship) {
        this.dateOfFriendship = dateOfFriendship;
    }

    public String getFullName() {
        return fullName;
    }

    /*
    @Override
    public String toString() {
        if(dateOfFriendship == null)
            return "First name : " + this.firstName + " | Last name : " + this.lastName;
        return "First name : " + this.firstName + " | Last name : " + this.lastName + " | Date of friendship : " + this.dateOfFriendship;
    }*/

    @Override
    public String toString() {
        if(dateOfFriendship == null)
            return  firstName +
                    " " + lastName +
                    " ";
        else
            return userName +
                    " " + firstName +
                    " " + lastName +
                    " " + dateOfFriendship.format(Constants.DATE_TIME_FORMATTER);
    }
}
