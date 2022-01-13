package socialnetwork.controller;

import socialnetwork.utils.events.FriendRequestEvent;
import socialnetwork.utils.observer.Observer;

public class NotificationsController extends UserController implements Observer<FriendRequestEvent> {
    @Override
    public void update(FriendRequestEvent friendRequestEvent) {

    }

    public void setServices() {
    }
}
