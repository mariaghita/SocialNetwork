package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.w3c.dom.Text;
import org.w3c.dom.events.MouseEvent;
import socialnetwork.model.Group;
import socialnetwork.model.Message;
import socialnetwork.model.MessageDTO;
import socialnetwork.model.UserDTO;
import socialnetwork.repository.db.*;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;
import socialnetwork.utils.events.MessageEvent;
import socialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessagesController extends UserController implements Observer<MessageEvent> {
    MessageService messageService;
    UserService userService;
    List<Label> messages;


    @FXML
    VBox chatBox;


    List<UserDTO> allUsers;
    ObservableList<UserDTO> model = FXCollections.observableArrayList();

    @FXML
    TableView<UserDTO> usersTableView;

    @FXML
    TableColumn<UserDTO,String> tableColumnName;

    @FXML
    TextField searchBar;

    @FXML
    Label chatUser;

    @FXML
    TextField textMessage;

    @FXML
    ScrollPane scroller;

    @FXML
    Button sendButton;

    @FXML
    Label noChatSelected;

    private AtomicBoolean friendsBool = new AtomicBoolean();
    private String friendUsername;


    public MessagesController(){super();}

    @FXML
    private void initialize(){
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        usersTableView.setItems(model);

        searchBar.textProperty().addListener(o -> handleFilter());
    }

    private void initModel(){
        searchBar.clear();
        makeChatBoxInvisible();
        model.setAll(allUsers);

    }

    private void setList(){
        allUsers = StreamSupport.stream(userService.getAll().spliterator(), false)
                .filter(e -> !Objects.equals(e.getId(), currentUsername))
                .map(e -> new UserDTO(e.getId(), userService.getOne(e.getId()).getFirstName(), userService.getOne(e.getId()).getLastName(), null))
                .collect(Collectors.toList());

    }

    public void setServices(String username){
        UserDBRepository userDBRepository = new UserDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendshipDBRepository friendshipDBRepository = new FriendshipDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        FriendRequestDBRepository friendRequestDBRepository = new FriendRequestDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        GroupDBRepository groupDBRepository = new GroupDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");
        MessageDBRepository messageDBRepository = new MessageDBRepository("jdbc:postgresql://localhost:5432/gitdatabse", "postgres", "0705");

        this.userService = new UserService(userDBRepository, friendshipDBRepository, friendRequestDBRepository);
        this.messageService = new MessageService(userDBRepository,friendshipDBRepository,friendRequestDBRepository,messageDBRepository,groupDBRepository);

        messageService.addObserver(this);

        this.currentUsername = username;


        setList();
        initModel();
    }

    private void handleFilter() {
        searchPredicate(searchBar, model, allUsers);
    }
    private void makeChatBoxInvisible(){
        this.noChatSelected.setVisible(true);
        this.chatBox.setVisible(false);
        this.scroller.setVisible(false);
        this.textMessage.setVisible(false);
        this.sendButton.setVisible(false);
    }
    private void makeChatBoxVisible(){
        this.noChatSelected.setVisible(false);
        this.chatBox.setVisible(true);
        this.scroller.setVisible(true);
        this.textMessage.setVisible(true);
        this.sendButton.setVisible(true);
    }

    static void searchPredicate(TextField searchBar, ObservableList<UserDTO> model, List<UserDTO> allUsers) {
        Predicate<UserDTO> p1 = e -> e.getFullName().toLowerCase().contains(searchBar.getText().toLowerCase());
        Predicate<UserDTO> p2 = e -> e.getFullName().toLowerCase().startsWith(searchBar.getText().toLowerCase());
        Predicate<UserDTO> p3 = e -> e.getFullNameRev().toLowerCase().contains(searchBar.getText().toLowerCase());
        Predicate<UserDTO> p4 = e -> e.getFullNameRev().toLowerCase().startsWith(searchBar.getText().toLowerCase());

        model.setAll(allUsers
                .stream()
                .filter(p1.or(p2).or(p3).or(p4))
                .collect(Collectors.toList()));
    }


    @Override
    public void update(MessageEvent messageEvent) {

    }


    public void showConversation(ActionEvent actionEvent){
        makeChatBoxVisible();
        chatBox.getChildren().clear();
        UserDTO selected = usersTableView.getSelectionModel().getSelectedItem();
        this.chatUser.setText(selected.getFullName());
        this.chatUser.setAlignment(Pos.CENTER);
        List<MessageDTO> conversation = this.messageService.getConversationWithAUser(currentUsername,selected.getUserName());

        messages = new ArrayList<Label>();
        for(MessageDTO messageDTO: conversation){
            messages.add(new Label(messageDTO.getText()));
            messages.get(messages.size()-1).setMaxWidth(Double.MAX_VALUE);


            if(messageDTO.getFrom().getId().equals(currentUsername))
                messages.get(messages.size()-1).setAlignment(Pos.CENTER_RIGHT);
            else {
                messages.get(messages.size() - 1).setAlignment(Pos.CENTER_LEFT);

            }
            chatBox.getChildren().add(messages.get(messages.size()-1));

        }
        chatBox.setSpacing(5);
        scroller.setContent(chatBox);
        scroller.setFitToWidth(chatBox.isFillWidth());
        scroller.setVvalue(1.0);
        scroller.setHvalue(1.0);
        scroller.setPannable(true);

    }

    public void sendMessage(ActionEvent actionEvent){
        UserDTO selected = usersTableView.getSelectionModel().getSelectedItem();

        if(selected!=null){
            try{
                Message message = new Message(this.currentUsername,List.of(selected.getUserName()),null,textMessage.getText());
                this.messageService.sendMessage(message);
                showConversation(actionEvent);
                this.textMessage.clear();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else
            MessageAlert.showErrorMessage(null,"nO USER SELECTED.");
    }




}
