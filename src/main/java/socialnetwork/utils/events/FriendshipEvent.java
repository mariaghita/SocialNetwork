package socialnetwork.utils.events;

import socialnetwork.model.Friendship;

public class FriendshipEvent implements Event {
    private FriendshipEventType type;
    private Friendship data, oldData;

    public FriendshipEvent(FriendshipEventType type, Friendship data) {
        this.type = type;
        this.data = data;
    }
    public FriendshipEvent(FriendshipEventType type, Friendship data, Friendship oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public FriendshipEventType getType() {
        return type;
    }

    public Friendship getData() {
        return data;
    }

    public Friendship getOldData() {
        return oldData;
    }
}