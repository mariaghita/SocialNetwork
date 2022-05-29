package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.model.UserDTO;
import socialnetwork.repository.db.FriendshipDBRepository;
import socialnetwork.repository.db.UserDBRepository;
import socialnetwork.service.FriendshipService;
import socialnetwork.utils.events.FriendshipEvent;
import socialnetwork.utils.observer.Observer;

import java.util.List;

public class FriendsController extends UserController implements Observer<FriendshipEvent> {
    FriendshipService friendshipService;
    ObservableList<UserDTO> model = FXCollections.observableArrayList();

    public FriendsController() {
        super();
    }

    @FXML
    TableView<UserDTO> tableViewFriends;

    @FXML
    TableColumn<UserDTO, String> tableColumnUsername;

    @FXML
    TableColumn<UserDTO, String> tableColumnFirstName;

    @FXML
    TableColumn<UserDTO, String> tableColumnLastName;

    @FXML
    TableColumn<UserDTO, String> tableColumnDateOfFriendship;

    @FXML
    private Button buttonAddFriend;

    @FXML
    private Button buttonRemoveFriend;

    @FXML
    private Pagination paginationFriends;

    @FXML
    private Label status;

    @FXML
    public void initialize() {
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("userName"));
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("LastName"));
        tableColumnDateOfFriendship.setCellValueFactory(new PropertyValueFactory<>("dateOfFriendship"));
        paginationFriends.setPageFactory(this::createPage);
        tableViewFriends.setItems(model);
    }

    /*
    private void initModel() {
        List<UserDTO> friendList = friendshipService.getFriendList(currentUsername);
        model.setAll(friendList);
    }
    */


    private Node createPage(Integer page) {
        int fromIndex = page * 5;
        int toIndex = Math.min(fromIndex + 5, model.size());
        tableViewFriends.setItems(FXCollections.observableArrayList(initModel(page)));
        return tableViewFriends;
    }

    private List<UserDTO> initModel(int page) {
        List<UserDTO> UserDTOS = friendshipService.getFriendshipsOnPage(page, currentUsername);
        model.setAll(UserDTOS);
        return UserDTOS;
    }

    public void setServices(){

        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");

        this.friendshipService= new FriendshipService(userDBRepository, friendshipDBRepository);

        friendshipService.addObserver(this);
        paginationFriends.setPageFactory(this::createPage);
        //initModel();
    }

    public void doRemoveFriend(ActionEvent event) {
        String message = "";
        UserDTO selected = tableViewFriends.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                friendshipService.removeFriendship(currentUsername, selected.getUserName());
                message = "You are no longer friends with " + selected.getFullName() + "!";
            } catch (Exception e) {
                message = e.getMessage();
            }
        } else
            message = "You didn't select any user that you want to unfriend!";
        status.setText(message);
    }

    @Override
    public void update(FriendshipEvent friendshipEvent) {
        paginationFriends.setPageFactory(this::createPage);
        //initModel();
    }
}
