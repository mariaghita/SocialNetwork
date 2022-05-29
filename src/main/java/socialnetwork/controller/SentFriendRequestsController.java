package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import socialnetwork.model.FriendRequestDTO;
import socialnetwork.repository.db.FriendRequestDBRepository;
import socialnetwork.repository.db.FriendshipDBRepository;
import socialnetwork.repository.db.UserDBRepository;
import socialnetwork.service.FriendRequestService;
import socialnetwork.utils.events.FriendRequestEvent;
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
    private Pagination sentFriendRequestsPagination;

    @FXML
    private Label status;

    @FXML
    private void initialize(){
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        sentFriendRequestsPagination.setPageFactory(this::createPage);
        friendRequestTableView.setItems(model);
    }

    private Node createPage(Integer page) {
        int fromIndex = page * 5;
        int toIndex = Math.min(fromIndex + 5, model.size());
        friendRequestTableView.setItems(FXCollections.observableArrayList(initModel(page)));
        return friendRequestTableView;
    }

    private List<FriendRequestDTO> initModel(int page) {
        List<FriendRequestDTO> friendRequestDTOS = friendRequestService.getSentFriendRequestsOnPage(page, currentUsername);
        model.setAll(friendRequestDTOS);
        return friendRequestDTOS;
    }

    public void setServices(){

        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendRequestDBRepository friendRequestDBRepository = new FriendRequestDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");

        this.friendRequestService = new FriendRequestService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);
        friendRequestService.addObserver(this);
        sentFriendRequestsPagination.setPageFactory(this::createPage);
    }

    @Override
    public void update(FriendRequestEvent friendshipEvent) {
        sentFriendRequestsPagination.setPageFactory(this::createPage);
    }

    public void unsendFriendRequest(ActionEvent actionEvent) {
        String message = "";
        FriendRequestDTO selected = friendRequestTableView.getSelectionModel().getSelectedItem();
        if(selected!=null){
            try{
                friendRequestService.deleteFriendRequest(currentUsername, selected.getUsername());
                message = "Friend request to " + selected.getFirstName() + " " + selected.getLastName() + " deleted.";
            }catch (Exception e){
                message = e.getMessage();
            }
        }else
            message = "No user selected!";
    }
}

