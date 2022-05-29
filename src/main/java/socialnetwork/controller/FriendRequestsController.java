package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private Pagination paginationFriendRequests;

    @FXML
    private Label status;

    public FriendRequestsController() {
        super();
    }

    @FXML
    private void initialize(){
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        paginationFriendRequests.setPageFactory(this::createPage);
        friendRequestTableView.setItems(model);
    }

    /*
    private void initModel(){
        List<FriendRequestDTO> friendRequestDTOS = friendRequestService.findFriendRequestToUser(currentUsername);
        System.out.println(friendRequestDTOS);
        model.setAll(friendRequestDTOS);
    }
    */

    private Node createPage(Integer page) {
        int fromIndex = page * 5;
        int toIndex = Math.min(fromIndex + 5, model.size());
        friendRequestTableView.setItems(FXCollections.observableArrayList(initModel(page)));
        return friendRequestTableView;
    }

    private List<FriendRequestDTO> initModel(int page) {
        List<FriendRequestDTO> friendRequestDTOS = friendRequestService.getFriendRequestsOnPage(page, currentUsername);
        model.setAll(friendRequestDTOS);
        return friendRequestDTOS;
    }


    public void setServices(){
        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendRequestDBRepository friendRequestDBRepository = new FriendRequestDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");

        this.friendRequestService = new FriendRequestService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);
        friendRequestService.addObserver(this);
        paginationFriendRequests.setPageFactory(this::createPage);
        //initModel();
    }

    public void acceptFriendRequest(ActionEvent actionEvent){
        String message = "";
        FriendRequestDTO selected = friendRequestTableView.getSelectionModel().getSelectedItem();
        if(selected!=null){
            try{
                friendRequestService.acceptFriendRequest(selected.getUsername(), currentUsername);
                message = "You are now friends with "+ selected.getFirstName() + " " + selected.getLastName() + ".";
            }catch (Exception e){
                message = e.getMessage();
            }
        }else
            message = "No user selected!";
        status.setText(message);
    }

    public void declineFriendRequest(ActionEvent actionEvent){
        String message = "";
        FriendRequestDTO selected = friendRequestTableView.getSelectionModel().getSelectedItem();
        if(selected!=null){
            try{
                friendRequestService.declineFriendRequest(selected.getUsername(), currentUsername);
                message = "Friend request from " + selected.getFirstName() + " " + selected.getLastName() + " declined.";
            }catch (Exception e){
                message = e.getMessage();
            }
        }else
            message = "No user selected!";
        status.setText(message);
    }
    @Override
    public void update(FriendRequestEvent friendshipEvent) {
        paginationFriendRequests.setPageFactory(this::createPage);
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
