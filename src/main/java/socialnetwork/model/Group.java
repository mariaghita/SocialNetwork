package socialnetwork.model;

import java.util.List;

public class Group extends Entity<Long>{
    private String name;
    private List<String> users;
    private List<Message> messages;

    /**
     * constructor
     * @param name
     */
    public Group(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
