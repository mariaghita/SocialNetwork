package socialnetwork.controller;

import socialnetwork.repository.db.FriendRequestDBRepository;
import socialnetwork.repository.db.FriendshipDBRepository;
import socialnetwork.repository.db.UserDBRepository;
import socialnetwork.service.FriendRequestService;
import socialnetwork.utils.events.FriendshipEvent;
import socialnetwork.utils.observer.Observer;

public class FriendRequestsController extends UserController implements Observer<FriendshipEvent> {
    FriendRequestService friendRequestService;

    public FriendRequestsController() {
        super();
    }

    public void setServices(){
        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "pepenerosu");
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "pepenerosu");
        FriendRequestDBRepository friendRequestDBRepository = new FriendRequestDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "pepenerosu");

        this.friendRequestService = new FriendRequestService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);
    }

    @Override
    public void update(FriendshipEvent friendshipEvent) {

    }
}
