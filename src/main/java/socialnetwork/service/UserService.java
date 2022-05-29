package socialnetwork.service;

import socialnetwork.model.*;
import socialnetwork.model.validators.*;
import socialnetwork.repository.db.*;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserService {
    private final UserDBRepository userDBRepository;
    private final FriendshipDBRepository friendshipDBRepository;
    private final FriendRequestDBRepository friendRequestDBRepository;
    private int page = 0;
    private int size = 5;

    public UserService(UserDBRepository userDBRepository,FriendshipDBRepository friendshipDBRepository, FriendRequestDBRepository friendRequestDBRepository) {
        this.userDBRepository = userDBRepository;
        this.friendshipDBRepository = friendshipDBRepository;
        this.friendRequestDBRepository = friendRequestDBRepository;
    }

    /**
     *adds a new user
     * @param user the user we want to add
     */
    public void addUser(User user) {
        Validator<User> validator = new UserValidator();
        validator.validate(user);

        if(userDBRepository.findOne(user.getId()) != null)
            throw new ValidationException("A user with this username already exists!\n");

        userDBRepository.save(user);
    }

    /**
     *deletes an existing user
     * @param username the username of the user we want to delete
     */
    public void removeUser(String username) {
        if(username == null)
            throw new ValidationException("Invalid username!\n");

        if(userDBRepository.findOne(username) == null)
            throw new ValidationException("A user with this username doesn't exist!\n");

        friendRequestDBRepository.deleteOnesFriendRequests(username);
        friendshipDBRepository.deleteOnesFriendships(username);
        userDBRepository.delete(username);
    }

    /**
     *creates a community (dfs)
     * @param user the current user
     * @param someUsers the users in the current community
     */
    private void createCommunity(User user, List<User> someUsers) {
        someUsers.add(user);
        for(User users : userDBRepository.findAll()) {
            Tuple<String> friendship = new Tuple<>(user.getId(), users.getId());
            friendship.orderTuple();
            if(friendshipDBRepository.findOne(friendship) != null && !someUsers.contains(users))
                createCommunity(users, someUsers);
        }
    }

    /**
     *computes each community and finds the number of communities
     * @return the number of communities
     */
    public int numberOfCommunities() {
        var rez = new Object() {
            int rezultat = 0;
        };

        List<User> usedUsers = new ArrayList<>();

        this.getAll().forEach(user -> {
            if(!usedUsers.contains(user)){
                createCommunity(user, usedUsers);
                rez.rezultat += 1;
            }
        });

        return rez.rezultat;
    }

    /**
     *displays the community with the most users
     */
    public void largestCommunity() {
        var rez = new Object() {
            int maxUsers = 0;
        };

        List<User> componentaConexa = new ArrayList<>();
        List<User> rezultat = new ArrayList<>();
        List<User> usedUsers = new ArrayList<>();

        this.getAll().forEach(user -> {
            if(!usedUsers.contains(user)){
                componentaConexa.clear();
                createCommunity(user, componentaConexa);
                usedUsers.addAll(componentaConexa);
                if(rez.maxUsers < componentaConexa.size() - 1){
                    rezultat.clear();
                    rezultat.addAll(componentaConexa);
                    rez.maxUsers = componentaConexa.size() - 1;
                }
            }
        });

        rezultat.forEach(System.out::println);
    }

    public User getOne(String username) {
        if(userDBRepository.findOne(username) == null)
            throw new ValidationException("A user with this username doesn't exist!\n");

        return userDBRepository.findOne(username);
    }

    public Iterable<User> getAll() {
        return userDBRepository.findAll();
    }

    public int getPage() {
        return page;
    }

    public List<UserDTO>getUsersOnPage(int page, String username, String str) {
        this.page = page;
        Pageable pageable = new PageableImplementation(page,this.size);
        Page<User> userPage = userDBRepository.findAllExceptCurrentWithString(pageable, username, str);

        return StreamSupport.stream(userPage.getContent().spliterator(), false)
                .map(e -> new UserDTO(e.getId(), e.getFirstName(), e.getLastName(), null))
                .collect(Collectors.toList());
    }
    public List<UserDTO>getUsersOnPage0(int page, String username) {
        this.page = page;
        Pageable pageable = new PageableImplementation(page,this.size);
        Page<User> userPage = userDBRepository.findAllExceptCurrent(pageable, username);

        return StreamSupport.stream(userPage.getContent().spliterator(), false)
                .map(e -> new UserDTO(e.getId(), e.getFirstName(), e.getLastName(), null))
                .collect(Collectors.toList());
    }

    public List<UserDTO> getNextPageUsers(String username, String str) {
        this.page++;
        return getUsersOnPage(this.page, username, str);
    }
}

