package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.Main;
import socialnetwork.model.UserDTO;
import socialnetwork.repository.db.FriendshipDBRepository;
import socialnetwork.repository.db.UserDBRepository;
import socialnetwork.service.FriendshipService;
import socialnetwork.utils.events.FriendshipEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
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
    public void initialize() {
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("userName"));
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("LastName"));
        tableColumnDateOfFriendship.setCellValueFactory(new PropertyValueFactory<>("dateOfFriendship"));
        tableViewFriends.setItems(model);
    }

    private void initModel() {
        List<UserDTO> friendList = friendshipService.getFriendList(currentUsername);
        model.setAll(friendList);
    }

    public void setServices(){
        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");

        this.friendshipService= new FriendshipService(userDBRepository, friendshipDBRepository);

        friendshipService.addObserver(this);
        initModel();
    }

    public void doAddFriend(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("addfriend-popup.fxml"));

            BorderPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add new friend!");
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            AddFriendPopupController addFriendPopupController = loader.getController();
            addFriendPopupController.setServices(dialogStage, currentUsername);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doRemoveFriend(ActionEvent event) {
        UserDTO selected = tableViewFriends.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                friendshipService.removeFriendship(currentUsername, selected.getUserName());
                String message = "You are no longer friends with " + selected.getFullName() + "!";
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", message);
            } catch (Exception e) {
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        } else
            MessageAlert.showErrorMessage(null, "You didn't select any user that you want to unfriend!");

    }

    @Override
    public void update(FriendshipEvent friendshipEvent) {
        initModel();
    }
}
