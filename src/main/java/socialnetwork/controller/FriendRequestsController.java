package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import socialnetwork.Main;
import socialnetwork.model.FriendRequestDTO;
import socialnetwork.repository.db.FriendRequestDBRepository;
import socialnetwork.repository.db.FriendshipDBRepository;
import socialnetwork.repository.db.UserDBRepository;
import socialnetwork.service.FriendRequestService;
import socialnetwork.utils.events.FriendRequestEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.util.List;

public class FriendRequestsController extends UserController implements Observer<FriendRequestEvent> {
    FriendRequestService friendRequestService;
    ObservableList<FriendRequestDTO> model = FXCollections.observableArrayList();

    @FXML
    TableView<FriendRequestDTO> friendRequestTableView;

    @FXML
    TableColumn<FriendRequestDTO, String> tableColumnFirstName;
    @FXML
    TableColumn<FriendRequestDTO, String> tableColumnLastName;
    @FXML
    TableColumn<FriendRequestDTO, String> tableColumnStatus;
    @FXML
    TableColumn<FriendRequestDTO, String> tableColumnDate;

    @FXML
    private void initialize(){
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        friendRequestTableView.setItems(model);
    }

    private void initModel(){
        List<FriendRequestDTO> friendRequestDTOS = friendRequestService.findFriendRequestToUser(currentUsername);
        System.out.println(friendRequestDTOS);
        model.setAll(friendRequestDTOS);
    }

    public FriendRequestsController() {
        super();
    }

    public void setServices(){

        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendRequestDBRepository friendRequestDBRepository = new FriendRequestDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");

        this.friendRequestService = new FriendRequestService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);
        friendRequestService.addObserver(this);
        initModel();
    }

    public void acceptFriendRequest(ActionEvent actionEvent){
        FriendRequestDTO selected = friendRequestTableView.getSelectionModel().getSelectedItem();
        if(selected!=null){
            try{
                friendRequestService.acceptFriendRequest(selected.getUsername(), currentUsername);
                String message = "You are now friends with "+ selected.getFirstName() + " " + selected.getLastName() + ".";
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Accept", message);
            }catch (Exception e){
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        }else
            MessageAlert.showErrorMessage(null, "No user selected!");
    }
    public void declineFriendRequest(ActionEvent actionEvent){
        FriendRequestDTO selected = friendRequestTableView.getSelectionModel().getSelectedItem();
        if(selected!=null){
            try{
                friendRequestService.declineFriendRequest(selected.getUsername(), currentUsername);
                String message = "Friend request from " + selected.getFirstName() + " " + selected.getLastName() + " declined.";
                MessageAlert.showMessage(null,Alert.AlertType.INFORMATION, "Decline", message);
            }catch (Exception e){
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
        }else
            MessageAlert.showErrorMessage(null,"No user selected!");
    }
    @Override
    public void update(FriendRequestEvent friendshipEvent) {
        initModel();
    }

    public void switchManageSentFriendRequests(ActionEvent event) throws IOException {
        FXMLLoader newMenu = new FXMLLoader(Main.class.getResource("sentfriendrequests-menu.fxml"));
        Scene newMenuScene = new Scene(newMenu.load(), 750, 500);

        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        SentFriendRequestsController sentFriendRequestsController = newMenu.getController();
        sentFriendRequestsController.initialize1(currentUsername);
        sentFriendRequestsController.setServices();

        stage.setScene(newMenuScene);
        stage.show();
    }
}
