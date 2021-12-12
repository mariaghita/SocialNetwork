package socialnetwork.controller;

import socialnetwork.repository.db.FriendshipDBRepository;
import socialnetwork.repository.db.UserDBRepository;
import socialnetwork.service.FriendshipService;

public class FriendsController extends UserController {
    FriendshipService friendshipService;

    public FriendsController() {
        super();
    }

    public void setServices(){
        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "pepenerosu");
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "pepenerosu");

        this.friendshipService= new FriendshipService(userDBRepository, friendshipDBRepository);
    }
}
