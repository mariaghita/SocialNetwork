package socialnetwork.utils.events;

import socialnetwork.model.FriendRequest;

public class FriendRequestEvent implements Event {
    private FriendRequestEventType type;
    FriendRequest friendRequest;

    public FriendRequestEvent(FriendRequestEventType type, FriendRequest friendRequest) {
        this.friendRequest = friendRequest;
        this.type = type;
    }

    public FriendRequest getFriendRequest() {
        return friendRequest;
    }

    public void setFriendRequest(FriendRequest friendRequest) {
        this.friendRequest = friendRequest;
    }

    public FriendRequestEventType getType() {
        return type;
    }

    public void setType(FriendRequestEventType type) {
        this.type = type;
    }
}
