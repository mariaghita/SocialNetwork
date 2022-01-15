package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.model.FriendRequest;
import socialnetwork.model.FriendRequestDTO;
import socialnetwork.model.UserDTO;
import socialnetwork.repository.db.FriendRequestDBRepository;
import socialnetwork.repository.db.FriendshipDBRepository;
import socialnetwork.repository.db.UserDBRepository;
import socialnetwork.service.FriendRequestService;
import socialnetwork.utils.events.FriendRequestEvent;
import socialnetwork.utils.events.FriendshipEvent;
import socialnetwork.utils.observer.Observer;

import java.util.List;

public class SentFriendRequestsController extends UserController implements Observer<FriendRequestEvent> {
    FriendRequestService friendRequestService;
    ObservableList<FriendRequestDTO> model = FXCollections.observableArrayList();

    public SentFriendRequestsController() {
        super();
    }

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
        List<FriendRequestDTO> friendRequestDTOS = friendRequestService.findFriendRequestFromUser(currentUsername);
        System.out.println(friendRequestDTOS);
        model.setAll(friendRequestDTOS);
    }

    public void setServices(){

        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendRequestDBRepository friendRequestDBRepository = new FriendRequestDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705" );

        this.friendRequestService = new FriendRequestService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);
        friendRequestService.addObserver(this);
        initModel();
    }

    @Override
    public void update(FriendRequestEvent friendshipEvent) {
        initModel();
    }

    public void unsendFriendRequest(ActionEvent actionEvent) {
        FriendRequestDTO selected = friendRequestTableView.getSelectionModel().getSelectedItem();
        if(selected!=null){
            try{
                friendRequestService.deleteFriendRequest(currentUsername, selected.getUsername());
                String message = "Friend request to " + selected.getFirstName() + " " + selected.getLastName() + " deleted.";
                MessageAlert.showMessage(null,Alert.AlertType.INFORMATION, "Delete", message);
            }catch (Exception e){
                MessageAlert.showErrorMessage(null,e.getMessage());
            }
        }else
            MessageAlert.showErrorMessage(null,"No user selected!");
    }
}

