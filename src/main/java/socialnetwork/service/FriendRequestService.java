package socialnetwork.service;

import socialnetwork.model.*;
import socialnetwork.model.validators.*;
import socialnetwork.repository.db.*;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.utils.events.FriendRequestEvent;
import socialnetwork.utils.events.FriendRequestEventType;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendRequestService implements Observable<FriendRequestEvent> {
    private final UserDBRepository userDBRepository;
    private final FriendshipDBRepository friendshipDBRepository;
    private final FriendRequestDBRepository friendRequestDBRepository;
    private int page = 0;
    private int size = 5;

    public FriendRequestService(UserDBRepository userDBRepository, FriendshipDBRepository friendshipDBRepository, FriendRequestDBRepository friendRequestDBRepository) {
        this.userDBRepository = userDBRepository;
        this.friendshipDBRepository = friendshipDBRepository;
        this.friendRequestDBRepository = friendRequestDBRepository;
    }

    /**
     *displays friend request to a certain user
     * @param username the user whose account we are logged in
     */
    public void findFriendRequestsTo(String username) {
        List<User> result = StreamSupport.stream(friendRequestDBRepository.findTo(username).spliterator(), false)
                .map(e -> userDBRepository.findOne(e.getId().getFirst()))
                .collect(Collectors.toList());

        if(result.size() == 0)
            System.out.println("You have no pending friend requests from other users!");
        else {
            System.out.println("You have pending friend requests from : ");
            result.forEach(System.out::println);
        }
    }

    /**
     * finds all friend requests for a user - used for table
     * @param username - the user whose account we are logged in
     * @return list of friend request DTOs
     */
    public List<FriendRequestDTO> findFriendRequestToUser(String username){
        Iterable<FriendRequest> friendRequests = friendRequestDBRepository.findTo(username);
        List<FriendRequestDTO> result = new ArrayList<FriendRequestDTO>();
        friendRequests.forEach(x->{
            User foundUser = userDBRepository.findOne(x.getId().getFirst());
            FriendRequestDTO friendRequestDTO = new FriendRequestDTO(foundUser.getFirstName(),foundUser.getLastName(),x.getLocalDateTime());
            friendRequestDTO.setUsername(foundUser.getId());
            result.add(friendRequestDTO);
        });
        return result;
    }
    /**
     *displays friend request to a certain user
     * @param username the user whose account we are logged in
     */
    public void findFriendRequestsFrom(String username) {
        List<User> result = StreamSupport.stream(friendRequestDBRepository.findFrom(username).spliterator(), false)
                .map(e -> userDBRepository.findOne(e.getId().getSecond()))
                .collect(Collectors.toList());

        if(result.size() == 0)
            System.out.println("You don't have any pending friend requests to other users!");
        else {
            System.out.println("You sent friend requests to : ");
            result.forEach(System.out::println);
        }
    }

    /**
     * finds all friend requests for a user - used for table
     * @param username - the user whose account we are logged in
     * @return list of friend request DTOs
     */
    public List<FriendRequestDTO> findFriendRequestFromUser(String username){
        Iterable<FriendRequest> friendRequests = friendRequestDBRepository.findFrom(username);
        List<FriendRequestDTO> result = new ArrayList<FriendRequestDTO>();
        friendRequests.forEach(x->{
            User foundUser = userDBRepository.findOne(x.getId().getSecond());
            FriendRequestDTO friendRequestDTO = new FriendRequestDTO(foundUser.getFirstName(),foundUser.getLastName(),x.getLocalDateTime());
            friendRequestDTO.setUsername(foundUser.getId());
            result.add(friendRequestDTO);
        });
        return result;
    }

    /**
     *username1 sends a friend requests to username2
     * @param username1 from
     * @param username2 to
     */
    public void addFriendRequest(String username1, String username2) {
        if(username2 == null)
            throw new ValidationException("Invalid username!\n");

        if(userDBRepository.findOne(username2) == null)
            throw new ValidationException("A user with this username doesn't exist!\n");

        Tuple<String> newFriendship = new Tuple<>(username1, username2);
        newFriendship.orderTuple();
        if(friendshipDBRepository.findOne(newFriendship) != null)
            throw new ValidationException("You are already friends with this user!\n");

        Tuple<String> newFriendRequest0 = new Tuple<>(username2, username1);
        if(friendRequestDBRepository.findOne(newFriendRequest0) != null)
            throw new ValidationException("You already have a pending friend request from this user!\n");

        Tuple<String> newFriendRequest1 = new Tuple<>(username1, username2);
        if(friendRequestDBRepository.findOne(newFriendRequest1) != null)
            throw new ValidationException("You already sent a friend request to this user! Wait for them to answer first!\n");

        FriendRequest newFriendRequest = new FriendRequest(username1, username2);
        Validator<FriendRequest> validator = new FriendRequestValidator();
        validator.validate(newFriendRequest);
        LocalDateTime date = LocalDateTime.now();
        newFriendRequest.setLocalDateTime(date);
        friendRequestDBRepository.save(newFriendRequest);
        notifyObservers(new FriendRequestEvent(FriendRequestEventType.PENDING, newFriendRequest));
    }

    /**
     *username2 accepts a friend request from username1
     * @param username1 from
     * @param username2 to
     */
    public void acceptFriendRequest(String username1, String username2) {
        validateFriendRequest(username1, username2);

        Friendship friendship = new Friendship(username1, username2, LocalDateTime.now());
        Validator<Friendship> validator = new FriendshipValidator();
        validator.validate(friendship);

        friendshipDBRepository.save(friendship);
        friendRequestDBRepository.delete(new Tuple<>(username1, username2));

        FriendRequest oldFriendRequest = new FriendRequest(username1, username2);
        notifyObservers(new FriendRequestEvent(FriendRequestEventType.APPROVED, oldFriendRequest));
    }

    /**
     *username2 declines a friend request from username1
     * @param username1 from
     * @param username2 to
     */
    public void declineFriendRequest(String username1, String username2) {
        validateFriendRequest(username1, username2);

        FriendRequest oldFriendRequest = friendRequestDBRepository.findOne(new Tuple<>(username1, username2));
        friendRequestDBRepository.delete(new Tuple<>(username1, username2));
        notifyObservers(new FriendRequestEvent(FriendRequestEventType.REJECTED, oldFriendRequest));
    }

    /**
     <<<<<<< HEAD
     *delete a friend request from username1 to username2
     * @param username1 from
     * @param username2 to
     */
    public void deleteFriendRequest(String username1, String username2) {
        validateFriendRequest(username1, username2);

        FriendRequest oldFriendRequest = friendRequestDBRepository.findOne(new Tuple<>(username1, username2));
        friendRequestDBRepository.delete(new Tuple<>(username1, username2));
        notifyObservers(new FriendRequestEvent(FriendRequestEventType.DELETED, oldFriendRequest));
    }

    /**
     *validates a friend request
     * @param username1 from
     * @param username2 to
     */
    private void validateFriendRequest(String username1, String username2){
        if(username1 == null)
            throw new ValidationException("Invalid username!\n");

        if(userDBRepository.findOne(username1) == null)
            throw new ValidationException("A user with this username doesn't exist!\n");

        Tuple<String> newFriendRequest = new Tuple<>(username1, username2);
        if(friendRequestDBRepository.findOne(newFriendRequest) == null)
            throw new ValidationException("You don't have a pending friend request from this user!\n");
    }

    public Iterable<FriendRequest> getAll() {
        return friendRequestDBRepository.findAll();
    }

    private List<Observer<FriendRequestEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<FriendRequestEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendRequestEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendRequestEvent e) {
        observers.forEach(x -> x.update(e));
    }

    //TODO metoda de paginare

    public int getPage() {
        return page;
    }

    public List<FriendRequestDTO>getSentFriendRequestsOnPage(int page, String username) {
        this.page = page;
        Pageable pageable = new PageableImplementation(page,this.size);
        Page<FriendRequest> sentFriendRequestsPage = friendRequestDBRepository.findAllFrom(pageable, username);

        List<FriendRequestDTO> result = new ArrayList<FriendRequestDTO>();
        sentFriendRequestsPage.getContent().forEach(x->{
            User foundUser = userDBRepository.findOne(x.getId().getSecond());
            FriendRequestDTO friendRequestDTO = new FriendRequestDTO(foundUser.getFirstName(),foundUser.getLastName(),x.getLocalDateTime());
            friendRequestDTO.setUsername(foundUser.getId());
            result.add(friendRequestDTO);
        });
        return result;
    }

    public List<FriendRequestDTO> getNextSentFriendRequestsUsers(String username) {
        this.page++;
        return getSentFriendRequestsOnPage(this.page, username);
    }

    public List<FriendRequestDTO>getFriendRequestsOnPage(int page, String username) {
        this.page = page;
        Pageable pageable = new PageableImplementation(page,this.size);
        Page<FriendRequest> sentFriendRequestsPage = friendRequestDBRepository.findAllTo(pageable, username);

        List<FriendRequestDTO> result = new ArrayList<FriendRequestDTO>();
        sentFriendRequestsPage.getContent().forEach(x->{
            User foundUser = userDBRepository.findOne(x.getId().getFirst());
            FriendRequestDTO friendRequestDTO = new FriendRequestDTO(foundUser.getFirstName(),foundUser.getLastName(),x.getLocalDateTime());
            friendRequestDTO.setUsername(foundUser.getId());
            result.add(friendRequestDTO);
        });
        return result;
    }

    public List<FriendRequestDTO> getNextFriendRequestsUsers(String username) {
        this.page++;
        return getFriendRequestsOnPage(this.page, username);
    }
}
