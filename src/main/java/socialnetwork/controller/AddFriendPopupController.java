package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
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
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class AddFriendPopupController implements Observer<FriendRequestEvent> {
    FriendshipService friendshipService;
    UserService userService;
    FriendRequestService friendRequestService;
    private String currentUsername;
    Stage stage;
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
    public void initialize() {
        tableColumnFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("userName"));
        users.setItems(model);

        searchBar.textProperty().addListener(o -> handleFilter());
    }

    public void setServices(Stage stage, String username) {
        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "pepenerosu");
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "pepenerosu");
        FriendRequestDBRepository friendRequestDBRepository = new FriendRequestDBRepository("jdbc:postgresql://localhost:5432/socialnetwork", "postgres", "pepenerosu");

        this.userService = new UserService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);
        this.friendshipService= new FriendshipService(userDBRepository, friendshipDBRepository);
        this.friendRequestService = new FriendRequestService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);

        this.friendRequestService.addObserver(this);

        this.currentUsername = username;
        this.stage = stage;
        setList();
        initModel();
    }

    private void setList() {
        allUsers = StreamSupport.stream(userService.getAll().spliterator(), false)
                .filter(e -> !Objects.equals(e.getId(), currentUsername))
                .map(e -> new UserDTO(e.getId(), userService.getOne(e.getId()).getFirstName(), userService.getOne(e.getId()).getLastName(), null))
                .collect(Collectors.toList());
    }

    private void initModel() {
        searchBar.clear();
        model.setAll(allUsers);
    }

    @Override
    public void update(FriendRequestEvent friendRequestEvent) {
        initModel();
    }

    private void handleFilter() {
        model.setAll(allUsers
                .stream()
                .filter(e -> e.getFirstName().toLowerCase().contains(searchBar.getText().toLowerCase()))
                .collect(Collectors.toList()));
    }

    public void doSendFriendRequest(ActionEvent event) {
        UserDTO selected = users.getSelectionModel().getSelectedItem();
        if (selected != null) {
             try {
                 friendRequestService.addFriendRequest(currentUsername, selected.getUserName());
                 String message = "Friend request sent to " + selected.getFullName() + "!";
                 MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Success", message);
             } catch(Exception e) {
                 MessageAlert.showErrorMessage(null, e.getMessage());
             }
        } else
            MessageAlert.showErrorMessage(null, "You didn't select any user that you want to befriend!");
    }

    public void doCleanSearchBar(ActionEvent event) {
        initModel();
    }

    public void doExitPopup(ActionEvent event) {
        stage.close();
    }
}
