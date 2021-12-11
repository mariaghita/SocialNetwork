package socialnetwork.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends Entity<String> {
    private String firstName;
    private String lastName;
    private List<User> friendList;

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        friendList = new ArrayList<>();
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

    public List<User> getFriends() {
        return friendList;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName ='" + firstName + '\'' +
                ", lastName ='" + lastName + '\'' +
                ", username ='" + this.getId() + '\'' +
                '}';
    }

    public void addFriend(User u){
        friendList.add(u);
    }

    public void removeFriend(User u){
        friendList.remove(u);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof User that))
            return false;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName().substring(0,2), getLastName().substring(0,2));
    }
}
