package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.model.UserDTO;
import socialnetwork.repository.db.FriendRequestDBRepository;
import socialnetwork.repository.db.FriendshipDBRepository;
import socialnetwork.repository.db.UserDBRepository;
import socialnetwork.service.FriendRequestService;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.UserService;
import socialnetwork.utils.events.FriendRequestEvent;
import socialnetwork.utils.observer.Observer;

import java.util.List;

public class DiscoverController extends UserController implements Observer<FriendRequestEvent> {
    FriendshipService friendshipService;
    UserService userService;
    FriendRequestService friendRequestService;
    private String currentUsername;

    List<UserDTO> allUsers;
    ObservableList<UserDTO> model = FXCollections.observableArrayList();

    @FXML
    private Button addFriendButton;

    @FXML
    private Button clearButton;

    @FXML
    private Button closeButton;

    @FXML
    private TextField searchBar;

    @FXML
    TableView<UserDTO> users;

    @FXML
    TableColumn<UserDTO, String> tableColumnFullName;

    @FXML
    TableColumn<UserDTO, String> tableColumnUsername;

    @FXML
    private Pagination paginationUsers;

    @FXML
    private Label status;

    @FXML
    public void initialize() {
        tableColumnFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("userName"));
        paginationUsers.setPageFactory(this::createPage);
        users.setItems(model);

        searchBar.textProperty().addListener(o -> handleFilter());
    }

    private Node createPage(Integer page) {
        int fromIndex = page * 5;
        int toIndex = Math.min(fromIndex + 5, model.size());
        users.setItems(FXCollections.observableArrayList(initModel(page)));
        return users;
    }

    private List<UserDTO> initModel(int page) {
        List<UserDTO> UserDTOS = userService.getUsersOnPage(page, currentUsername, searchBar.getText().toLowerCase());
        model.setAll(UserDTOS);
        return UserDTOS;
    }

    public void setServices(String username) {
        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendRequestDBRepository friendRequestDBRepository = new FriendRequestDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");

        this.userService = new UserService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);
        this.friendshipService= new FriendshipService(userDBRepository, friendshipDBRepository);
        this.friendRequestService = new FriendRequestService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);

        this.friendRequestService.addObserver(this);

        this.currentUsername = username;

        //setList();
        //initModel();
    }

    /*
    private void setList() {
        allUsers = StreamSupport.stream(userService.getAll().spliterator(), false)
                .filter(e -> !Objects.equals(e.getId(), currentUsername))
                .map(e -> new UserDTO(e.getId(), userService.getOne(e.getId()).getFirstName(), userService.getOne(e.getId()).getLastName(), null))
                .collect(Collectors.toList());
    }*/

    private void initModel() {
        searchBar.clear();
        paginationUsers.setPageFactory(this::createPage);
    }

    @Override
    public void update(FriendRequestEvent friendRequestEvent) {
        paginationUsers.setPageFactory(this::createPage);
    }


    private void handleFilter() {
        /*
        Predicate<UserDTO> p1 = e -> e.getFullName().toLowerCase().contains(searchBar.getText().toLowerCase());
        Predicate<UserDTO> p2 = e -> e.getFullNameRev().toLowerCase().contains(searchBar.getText().toLowerCase());

        model.setAll(allUsers
                .stream()
                .filter(p1.or(p2))
                .collect(Collectors.toList()));*/

        paginationUsers.setPageFactory(this::createPage);
        status.setText("");
    }

    public void doSendFriendRequest(ActionEvent event) {
        String message = "";
        UserDTO selected = users.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                friendRequestService.addFriendRequest(currentUsername, selected.getUserName());
                message = "Friend request sent to " + selected.getFullName() + "!";
            } catch(Exception e) {
                message = e.getMessage();
            }
        } else
            message = "You didn't select any user that you want to befriend!";
        status.setText(message);
    }

    public void doCleanSearchBar(ActionEvent event) {
        searchBar.clear();
        paginationUsers.setPageFactory(this::createPage);
    }
}
